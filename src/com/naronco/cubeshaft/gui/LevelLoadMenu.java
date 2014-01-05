/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.gui;

import org.lwjgl.input.*;

public class LevelLoadMenu extends Menu {
	private String levelName = "";
	private String tmpLevelName = "";
	private boolean levelExists = true;

	public void init() {
		this.buttons.add(new Button(0, (width / 2 - 200) / 2, height - 40, 200,
				30, "Back"));
		this.buttons.add(new Button(1, width / 2 + (width / 2 - 200) / 2,
				height - 40, 200, 30, "Load"));
	}

	protected void keyType(char c, int keyIndex) {
		if (keyIndex == Keyboard.KEY_ESCAPE)
			return;
		String ch = c + "";
		if (keyIndex == Keyboard.KEY_BACK && levelName.length() >= 1) {
			levelName = levelName.substring(0, levelName.length() - 1);
		} else if (keyIndex != Keyboard.KEY_BACK) {
			if (levelName.length() > 12)
				return;
			if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				ch = ch.toLowerCase();
			}
			levelName += ch;
		}
	}

	protected void buttonClicked(Button b) {
		if (b.id == 0) {
			this.game.setMenu(new PausedGameMenu());
		}
		if (this.game.levelManager.exists(levelName)) {
			this.game.levelManager.load(levelName, this.game.level,
					this.game.player);
			this.game.setMenu(new PausedGameMenu());
		} else {
			levelExists = false;
			tmpLevelName = levelName;
		}
	}

	public void render(int xMouse, int yMouse) {
		fill(0, 0, width, height, 0xa0050500);
		drawString("Save level",
				(this.width - TextRenderer.getTextLength("Save level")) / 2,
				40, 0xffffff);
		drawString(
				"Type level name:",
				(this.width - TextRenderer.getTextLength("Type level name:")) / 2,
				80, 0xffffff);

		int textFieldWidth = 12;

		fill((width - textFieldWidth * 16) / 2 - 2, (height - 16) / 2 - 2,
				(width + textFieldWidth * 16) / 2 + 2,
				(height + 2 * 16) / 2 + 2, 0xff808080);
		fill((width - textFieldWidth * 16) / 2, (height - 16) / 2,
				(width + textFieldWidth * 16) / 2, (height + 2 * 16) / 2,
				0xff000000);
		drawString(levelName,
				(this.width - TextRenderer.getTextLength(levelName)) / 2,
				height / 2 - 4, 0xffffff);
		if (System.currentTimeMillis() / 450 % 2 == 0) {
			drawString(
					"_",
					(this.width + TextRenderer.getTextLength(levelName)) / 2 - 2,
					height / 2 - 4, 0xffffff);
		}

		if (!levelExists) {
			String warning = "The level " + tmpLevelName + " doesnt exists!";
			drawString(warning,
					(this.width - TextRenderer.getTextLength(warning)) / 2,
					height / 2 + 40, 0xff0000);
		}
		super.render(xMouse, yMouse);
	}
}
