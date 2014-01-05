/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

import org.lwjgl.input.*;

import com.naronco.cubeshaft.*;
import com.naronco.cubeshaft.render.*;

public class Menu {
	protected Cubeshaft game;
	protected int width, height;
	protected List<Button> buttons = new ArrayList<Button>();

	public void render(int xMouse, int yMouse) {
		for (int i = 0; i < buttons.size(); i++) {
			Button b = buttons.get(i);
			if (!b.isClickable) {
				fill(b.x - 2, b.y - 2, b.x + b.width + 2, b.y + b.height + 2,
						0xff000000);
				fill(b.x, b.y, b.x + b.width, b.y + b.height, 0xff2C2C2C);
				drawString(b.text,
						b.x + (b.width - TextRenderer.getTextLength(b.text))
								/ 2, b.y + (b.height - 16) / 2, 0x5F5F60);
			} else {
				fill(b.x - 2, b.y - 2, b.x + b.width + 2, b.y + b.height + 2,
						0xff000000);
				if (xMouse >= b.x && yMouse >= b.y && xMouse < b.x + b.width
						&& yMouse < b.y + b.height) {
					fill(b.x - 2, b.y - 2, b.x + b.width + 2, b.y + b.height
							+ 2, 0xffA0A0A0);
					fill(b.x, b.y, b.x + b.width, b.y + b.height, 0xff8080A0);
					drawString(
							b.text,
							b.x
									+ (b.width - TextRenderer
											.getTextLength(b.text)) / 2, b.y
									+ (b.height - 16) / 2, 0xFFFFA0);
				} else {
					fill(b.x, b.y, b.x + b.width, b.y + b.height, 0xff8F8F90);
					drawString(
							b.text,
							b.x
									+ (b.width - TextRenderer
											.getTextLength(b.text)) / 2, b.y
									+ (b.height - 16) / 2, 0xE0E0E0);
				}
			}
		}
	}

	protected void keyType(char c, int keyIndex) {
		if (keyIndex == Keyboard.KEY_ESCAPE) {
			this.game.setMenu(null);
			this.game.setInGame();
		}
	}

	protected void buttonClicked(Button button) {
	}

	public final void init(Cubeshaft game, int width, int height) {
		this.game = game;
		this.width = width;
		this.height = height;
		init();
	}

	public void init() {
	}

	protected static void fill(int x0, int y0, int x1, int y1, int color) {
		float a = (color >>> 24) / 255.0f;
		float r = (color >> 16 & 0xff) / 255.0f;
		float g = (color >> 8 & 0xff) / 255.0f;
		float b = (color & 0xff) / 255.0f;

		Tesselator t = Tesselator.instance;
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(r, g, b, a);
		t.begin();
		t.vertex(x0, y1, 0.0f);
		t.vertex(x1, y1, 0.0f);
		t.vertex(x1, y0, 0.0f);
		t.vertex(x0, y0, 0.0f);
		t.end();
		glDisable(GL_BLEND);
	}

	public void drawString(String text, int x, int y, int color) {
		this.game.guiText.drawString(text, x, y, color);
	}

	public void input() {
		while (Mouse.next())
			if (Mouse.getEventButtonState()) {
				int xMouse = Mouse.getEventX() * this.width / this.game.width;
				int yMouse = this.height - Mouse.getEventY() * this.height
						/ this.game.height - 1;
				int mb = Mouse.getEventButton();
				if (mb == 0)
					for (int i = 0; i < buttons.size(); i++) {
						Button b = buttons.get(i);
						if (xMouse >= b.x && yMouse >= b.y
								&& xMouse < b.x + b.width
								&& yMouse < b.y + b.height) {
							this.buttonClicked(b);
						}
					}
			}
		while (Keyboard.next())
			if (Keyboard.getEventKeyState())
				keyType(Keyboard.getEventCharacter(), Keyboard.getEventKey());
	}

	public void tick() {
	}

	public void close() {
	}
}
