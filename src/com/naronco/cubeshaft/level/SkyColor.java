package com.naronco.cubeshaft.level;

public class SkyColor {
	private int[] skyPixels = Misc.getPixels("skycol");

	public int getSkyColor(int time) {
		float index = (time / 3600f * 128) % 128;
		float g2 = index % 1;
		float g1 = 1f - g2;

		if ((int) index == 127)
			return skyPixels[127];

		int color = 0;

		color |= (int) ((skyPixels[(int) index] >> 16 & 0xff) * g1 + (skyPixels[(int) index + 1] >> 16 & 0xff)
				* g2) << 16;
		color |= (int) ((skyPixels[(int) index] >> 8 & 0xff) * g1 + (skyPixels[(int) index + 1] >> 8 & 0xff)
				* g2) << 8;
		color |= (int) ((skyPixels[(int) index] & 0xff) * g1 + (skyPixels[(int) index + 1] & 0xff)
				* g2);

		return color;
	}
}
