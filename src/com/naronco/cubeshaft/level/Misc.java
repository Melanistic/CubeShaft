package com.naronco.cubeshaft.level;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Misc {
	public static int[] getPixels(String filename) {
		try {
			BufferedImage img = ImageIO.read(Misc.class.getResource("/misc/"
					+ filename + ".png"));
			return img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0,
					img.getWidth());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
