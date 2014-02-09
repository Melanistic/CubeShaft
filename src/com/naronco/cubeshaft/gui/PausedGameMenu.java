/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.gui;

import com.naronco.cubeshaft.level.Level;

public class PausedGameMenu extends Menu {
	public void init() {
		this.buttons.add(new Button(3, this.width / 2 - 200, this.height / 4, 400, 40, "Back to game"));
		this.buttons.add(new Button(1, this.width / 2 - 200, this.height / 4 + 60, 190, 40, "Save"));
		this.buttons.add(new Button(2, this.width / 2 + 10, this.height / 4 + 60, 190, 40, "Load"));
		//this.buttons.add(new Button(0, this.width / 2 - 200, this.height / 4 + 180, 400, 40, "Generate new level"));
		this.buttons.add(new Button(0, this.width / 2 - 200, this.height / 4 + 180, 400, 40, "Save and Quit"));
	}

	protected void buttonClicked(Button b) {
		if (b.id == 0) 
		{
			//this.game.generateNewLevel();
			//this.game.setMenu(null);
			//this.game.setInGame();
			Level l = game.level;
			game.level = new Level();
			game.level.init("", 1, 1, 1, new byte[1]);
			game.levelManager.save(l.name, l, game.player);
			l = null;
			System.gc();
			this.game.setMenu(new StartMenu());
		}
		if (b.id == 1) {
			this.game.setMenu(new LevelSaveMenu());
		}
		if (b.id == 2) {
			this.game.setMenu(new LevelLoadMenu());
		}
		if (b.id == 3) {
			this.game.setMenu(null);
			this.game.setInGame();
		}
	}

	public void render(int xMouse, int yMouse) {
		fill(0, 0, width, height, 0xa0050500);
		drawString("Game menu", (this.width - TextRenderer.getTextLength("Game menu")) / 2, 40, 0xfffffff);
		drawString("y: " + game.player.y, 10, 30, 0xeeeeee);
		super.render(xMouse, yMouse);
	}
}
