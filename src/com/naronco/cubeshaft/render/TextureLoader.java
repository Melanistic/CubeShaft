/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.awt.image.*;
import java.nio.*;
import java.util.*;

import javax.imageio.*;

import org.lwjgl.*;

public class TextureLoader {
	private static Map<String, Integer> loadedTextures = new HashMap<String, Integer>();

	public static final int load(String filename, int textureMode) {
		try {
			if (loadedTextures.containsKey(filename)) {
				return loadedTextures.get(filename).intValue();
			}
			IntBuffer buffer = (IntBuffer) BufferUtils.createIntBuffer(1)
					.clear();
			glGenTextures(buffer);

			int id = buffer.get(0);
			loadedTextures.put(filename, id);
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, textureMode);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, textureMode);

			BufferedImage img = ImageIO.read(TextureLoader.class
					.getResource(filename));
			int width = img.getWidth();
			int height = img.getHeight();
			ByteBuffer pixelsByteBuffer = BufferUtils.createByteBuffer(width
					* height << 2);
			int[] intPixels = new int[width * height];
			byte[] bytePixels = new byte[width * height << 2];

			img.getRGB(0, 0, width, height, intPixels, 0, width);
			for (int i = 0; i < intPixels.length; i++) {
				int a = intPixels[i] >>> 24;
				int r = intPixels[i] >> 16 & 0xff;
				int g = intPixels[i] >> 8 & 0xff;
				int b = intPixels[i] & 0xff;
				bytePixels[(i << 2) + 0] = (byte) r;
				bytePixels[(i << 2) + 1] = (byte) g;
				bytePixels[(i << 2) + 2] = (byte) b;
				bytePixels[(i << 2) + 3] = (byte) a;
			}

			pixelsByteBuffer.put(bytePixels);
			pixelsByteBuffer.position(0).limit(bytePixels.length);
			gluBuild2DMipmaps(GL_TEXTURE_2D, GL_RGBA, width, height, GL_RGBA,
					GL_UNSIGNED_BYTE, pixelsByteBuffer);
			return id;
		} catch (Exception e) {
		}
		throw new RuntimeException("!!");
	}
}
