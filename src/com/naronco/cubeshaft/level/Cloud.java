package com.naronco.cubeshaft.level;

import java.util.Random;

import com.naronco.cubeshaft.render.Tesselator;

public class Cloud {
	public static final int CLOUD_MAP_SIZE = 8;
	public static final boolean[] cloudMap = new boolean[CLOUD_MAP_SIZE
			* CLOUD_MAP_SIZE];

	public void render() {
		Tesselator t = Tesselator.instance;
		for (int x = 0; x < CLOUD_MAP_SIZE; x++) {
			for (int z = 0; z < CLOUD_MAP_SIZE; z++) {
				int col = cloudMap[x + z * CLOUD_MAP_SIZE] ? 0xffffff : 0;
				if (col != 0) {
					t.begin();
					t.color(0xffffff);
					renderCloud(t, x * 16, 120, z * 16, 0);
					renderCloud(t, x * 16, 120, z * 16, 1);

					t.color(0xd0d0d0);
					renderCloud(t, x * 16, 120, z * 16, 2);
					t.color(0xc0c0c0);
					renderCloud(t, x * 16, 120, z * 16, 3);
					t.color(0xd0d0d0);
					renderCloud(t, x * 16, 120, z * 16, 4);
					t.color(0xc0c0c0);
					renderCloud(t, x * 16, 120, z * 16, 5);
					t.end();
				}
			}
		}
	}

	public void renderCloud(Tesselator t, float xo, float yo, float zo, int side) {
		float x0 = xo;
		float x1 = xo + 16;
		float y0 = yo;
		float y1 = yo + 4;
		float z0 = zo;
		float z1 = zo + 16;

		if (side == 0) {
			t.normal(0, -1, 0);
			t.vertex(x0, y0, z1);
			t.vertex(x0, y0, z0);
			t.vertex(x1, y0, z0);
			t.vertex(x1, y0, z1);
			return;
		}
		if (side == 1) {
			t.normal(0, 1, 0);
			t.vertex(x1, y1, z1);
			t.vertex(x1, y1, z0);
			t.vertex(x0, y1, z0);
			t.vertex(x0, y1, z1);
			return;
		}
		if (side == 2) {
			t.normal(0, 0, -1);
			t.vertex(x0, y1, z0);
			t.vertex(x1, y1, z0);
			t.vertex(x1, y0, z0);
			t.vertex(x0, y0, z0);
			return;
		}
		if (side == 3) {
			t.normal(0, 0, 1);
			t.vertex(x0, y1, z1);
			t.vertex(x0, y0, z1);
			t.vertex(x1, y0, z1);
			t.vertex(x1, y1, z1);
			return;
		}
		if (side == 4) {
			t.normal(-1, 0, 0);
			t.vertex(x0, y1, z1);
			t.vertex(x0, y1, z0);
			t.vertex(x0, y0, z0);
			t.vertex(x0, y0, z1);
			return;
		}
		if (side == 5) {
			t.normal(1, 0, 0);
			t.vertex(x1, y0, z1);
			t.vertex(x1, y0, z0);
			t.vertex(x1, y1, z0);
			t.vertex(x1, y1, z1);
			return;
		}
	}

	static {
		Random random = new Random();
		for (int i = 0; i < cloudMap.length; i++) {
			if (random.nextBoolean())
				cloudMap[i] = true;
		}
	}
}
