/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.gui;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.*;

import javax.imageio.*;

import com.naronco.cubeshaft.render.*;

public class TextRenderer {
	private static int[] charWidth = new int[256];
	private int texture;

	public TextRenderer(String filename) {
		BufferedImage img;
		try {
			img = ImageIO.read(getClass().getResource(filename));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		int width = img.getWidth();
		int height = img.getHeight();
		int[] pixels = new int[width * height];
		img.getRGB(0, 0, width, height, pixels, 0, width);
		for (int i = 0; i < 128; i++) {
			int xi = i % 16;
			int yi = i / 16;
			int cw = 0;
			int xo = 15;
			while (xo >= 0 && cw == 0) {
				for (int yo = 0; yo < 15; yo++) {
					int xx = xi * 16 + xo;
					int yy = yi * 16 + yo;
					if ((pixels[xx + yy * width] & 0xff00000) != 0x00) {
						cw = xo + 4;
					}
				}
				xo--;
			}
			if (i == 32) cw = 12;
			charWidth[i] = cw;
		}
		this.texture = TextureLoader.load(filename, GL_NEAREST);
	}

	public void drawString(String text, int x, int y, int color) {
		drawString(text, x, y + 1, color, true);
		drawString(text, x, y - 1, color, true);
		drawString(text, x + 1, y, color, true);
		drawString(text, x - 1, y, color, true);
		drawString(text, x, y, color, false);
	}

	public void drawString(String text, int x, int y, int color, boolean dark) {
		char[] chars = text.toCharArray();
		if (dark) color = (color & 0xfcfcfc) >> 2;
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, texture);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		Tesselator t = Tesselator.instance;
		t.begin();
		t.color(color);
		int xOffs = 0;
		for (int j = 0; j < text.length(); j++) {
			if (chars[j] == '&') {
				String colorString = "0123456789abcdef";
				int m = ((colorString.indexOf(chars[j + 1])) & 0x8) << 3;

				int r = (color & 0x4) * 191 + m;
				int g = (color & 0x2) * 191 + m;
				int b = (color & 0x1) * 191 + m;
				color = r << 16 | g << 8 | b;

				j += 2;
				if (dark) color = (color & 0xfcfcfc) >> 1;
			}
			int xTex = chars[j] % 16 << 4;
			int yTex = chars[j] / 16 << 4;
			if (!dark) t.color((color & 0xfcfcfc) / 2);
			t.vertexUV(x + xOffs + 1, y + 16, 0.0f, xTex / 256.0f, (yTex + 16) / 256.0f);
			t.vertexUV(x + xOffs + 17, y + 16, 0.0f, (xTex + 16) / 256.0f, (yTex + 16) / 256.0f);
			t.vertexUV(x + xOffs + 17, y, 0.0f, (xTex + 16) / 256.0f, yTex / 256.0f);
			t.vertexUV(x + xOffs + 1, y, 0.0f, xTex / 256.0f, yTex / 256.0f);

			if (!dark) t.color((color & 0xfcfcfc) / 2);
			t.vertexUV(x + xOffs + 1, y + 17, 0.0f, xTex / 256.0f, (yTex + 16) / 256.0f);
			t.vertexUV(x + xOffs + 17, y + 17, 0.0f, (xTex + 16) / 256.0f, (yTex + 16) / 256.0f);
			t.vertexUV(x + xOffs + 17, y + 1, 0.0f, (xTex + 16) / 256.0f, yTex / 256.0f);
			t.vertexUV(x + xOffs + 1, y + 1, 0.0f, xTex / 256.0f, yTex / 256.0f);

			if (!dark) t.color((color & 0xfcfcfc) / 2);
			t.vertexUV(x + xOffs, y + 17, 0.0f, xTex / 256.0f, (yTex + 16) / 256.0f);
			t.vertexUV(x + xOffs + 16, y + 17, 0.0f, (xTex + 16) / 256.0f, (yTex + 16) / 256.0f);
			t.vertexUV(x + xOffs + 16, y + 1, 0.0f, (xTex + 16) / 256.0f, yTex / 256.0f);
			t.vertexUV(x + xOffs, y + 1, 0.0f, xTex / 256.0f, yTex / 256.0f);

			t.color(color);
			t.vertexUV(x + xOffs, y + 16, 0.0f, xTex / 256.0f, (yTex + 16) / 256.0f);
			t.vertexUV(x + xOffs + 16, y + 16, 0.0f, (xTex + 16) / 256.0f, (yTex + 16) / 256.0f);
			t.vertexUV(x + xOffs + 16, y, 0.0f, (xTex + 16) / 256.0f, yTex / 256.0f);
			t.vertexUV(x + xOffs, y, 0.0f, xTex / 256.0f, yTex / 256.0f);
			xOffs += charWidth[chars[j]];
		}
		t.end();

		glDisable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
	}

	public static int getTextLength(String text) {
		char[] chars = text.toCharArray();
		int length = 0;
		for (int i = 0; i < chars.length; i++)
			if (chars[i] == '&') i++;
			else length += charWidth[chars[i]];
		return length;
	}
}
