package com.naronco.cubeshaft.render;

import util.Vec3;

import com.naronco.cubeshaft.Cubeshaft;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.level.tile.Tile;

public class TileRenderer {
	public static TileRenderer instance = new TileRenderer();

	public static final void init(Cubeshaft game) {
		instance.level = game.level;
	}

	private Level level;
	private Tesselator t = Tesselator.instance;

	public boolean render(Tile tile, int x, int y, int z, int renderType) {
		int renderMode = tile.getRenderMode();
		switch (renderMode) {
		case 0:
			return renderDefault(tile, x, y, z, renderType);
		case 1:
			return renderCross(tile, x, y, z, renderType);
		case 2:
			return renderLiquid(tile, x, y, z, renderType);
		}
		return false;
	}

	public boolean renderDefault(Tile tile, int x, int y, int z, int renderType) {
		boolean canRender = false;
		if (tile.canRender(level, x, y - 1, z, renderType, 0)) {
			renderSide(tile, x, y, z, 0);
			canRender = true;
		}
		if (tile.canRender(level, x, y + 1, z, renderType, 1)) {
			renderSide(tile, x, y, z, 1);
			canRender = true;
		}
		if (tile.canRender(level, x, y, z - 1, renderType, 2)) {
			renderSide(tile, x, y, z, 2);
			canRender = true;
		}
		if (tile.canRender(level, x, y, z + 1, renderType, 3)) {
			renderSide(tile, x, y, z, 3);
			canRender = true;
		}
		if (tile.canRender(level, x - 1, y, z, renderType, 4)) {
			renderSide(tile, x, y, z, 4);
			canRender = true;
		}
		if (tile.canRender(level, x + 1, y, z, renderType, 5)) {
			renderSide(tile, x, y, z, 5);
			canRender = true;
		}
		return canRender;
	}

	public boolean renderCross(Tile tile, int x, int y, int z, int renderType) {
		t.color(255, 255, 255);
		for (int i = 0; i < 2; i++) {
			float s = (float) (Math.sin(i * Math.PI / 2.0 + 0.7853981633974483) * 0.5);
			float c = (float) (Math.cos(i * Math.PI / 2.0 + 0.7853981633974483) * 0.5);

			float xc0 = x + 0.5f - s;
			float xc1 = x + 0.5f + s;
			float yc0 = y;
			float yc1 = y + 1.0f;
			float zc0 = z + 0.5f - c;
			float zc1 = z + 0.5f + c;

			Vec3 q00 = new Vec3(xc0, yc0, zc0);
			Vec3 q01 = new Vec3(xc1, yc0, zc1);
			Vec3 q02 = new Vec3(xc0, yc1, zc0);

			Vec3 n0 = Renderer.calcNormal(q00, q01, q02);

			Vec3 q10 = new Vec3(xc1, yc0, zc1);
			Vec3 q11 = new Vec3(xc0, yc0, zc0);
			Vec3 q12 = new Vec3(xc1, yc1, zc1);

			Vec3 n1 = Renderer.calcNormal(q10, q11, q12);

			float u0 = (tile.getTexture(i) % 16) / 16.0f;
			float u1 = u0 + 0.0624375f;
			float v0 = (tile.getTexture(i) / 16) / 16.0f;
			float v1 = v0 + 0.0624375f;

			t.normal(n0.x, n0.y, n0.z);
			t.vertexUV(xc0, yc0, zc0, u0, v1);
			t.vertexUV(xc1, yc0, zc1, u1, v1);
			t.vertexUV(xc1, yc1, zc1, u1, v0);
			t.vertexUV(xc0, yc1, zc0, u0, v0);

			t.normal(n1.x, n1.y, n1.z);
			t.vertexUV(xc1, yc0, zc1, u0, v1);
			t.vertexUV(xc0, yc0, zc0, u1, v1);
			t.vertexUV(xc0, yc1, zc0, u1, v0);
			t.vertexUV(xc1, yc1, zc1, u0, v0);
		}
		return true;
	}

	public boolean renderLiquid(Tile tile, int x, int y, int z, int renderType) {
		boolean canRender = false;
		if (tile.canRender(level, x, y - 1, z, renderType, 0)) {
			renderSide(tile, x, y, z, 0);
			renderFlippedSide(tile, x, y, z, 0);
			canRender = true;
		}
		if (tile.canRender(level, x, y + 1, z, renderType, 1)) {
			renderSide(tile, x, y, z, 1);
			renderFlippedSide(tile, x, y, z, 1);
			canRender = true;
		}
		if (tile.canRender(level, x, y, z - 1, renderType, 2)) {
			renderSide(tile, x, y, z, 2);
			renderFlippedSide(tile, x, y, z, 2);
			canRender = true;
		}
		if (tile.canRender(level, x, y, z + 1, renderType, 3)) {
			renderSide(tile, x, y, z, 3);
			renderFlippedSide(tile, x, y, z, 3);
			canRender = true;
		}
		if (tile.canRender(level, x - 1, y, z, renderType, 4)) {
			renderSide(tile, x, y, z, 4);
			renderFlippedSide(tile, x, y, z, 4);
			canRender = true;
		}
		if (tile.canRender(level, x + 1, y, z, renderType, 5)) {
			renderSide(tile, x, y, z, 5);
			renderFlippedSide(tile, x, y, z, 5);
			canRender = true;
		}
		return canRender;
	}

	public void renderSide(Tile tile, float xo, float yo, float zo, int side) {
		int xTex = tile.getTexture(side) % 16 << 5;
		int yTex = tile.getTexture(side) / 16 << 5;
		float u0 = 0, u1 = 0, v0 = 0, v1 = 0;
		if (side == 0 || side == 1) {
			u0 = tile.x0 / 16.0f;
			u1 = tile.x1 / 16.0f;
			v0 = tile.z0 / 16.0f;
			v1 = tile.z1 / 16.0f;
		} else if (side == 2 || side == 3) {
			u0 = tile.x0 / 16.0f;
			u1 = tile.x1 / 16.0f;
			v0 = tile.y0 / 16.0f;
			v1 = tile.y1 / 16.0f;
		} else if (side == 4 || side == 5) {
			u0 = tile.z0 / 16.0f;
			u1 = tile.z1 / 16.0f;
			v0 = tile.y0 / 16.0f;
			v1 = tile.y1 / 16.0f;
		}
		u0 += xTex / 512.0f;
		u1 += xTex / 512.0f;
		v0 += yTex / 512.0f;
		v1 += yTex / 512.0f;

		float x0 = xo + tile.x0;
		float x1 = xo + tile.x1;
		float y0 = yo + tile.y0;
		float y1 = yo + tile.y1;
		float z0 = zo + tile.z0;
		float z1 = zo + tile.z1;

		if (side == 0) {
			t.normal(0, -1, 0);
			t.vertexUV(x0, y0, z1, u0, v1);
			t.vertexUV(x0, y0, z0, u0, v0);
			t.vertexUV(x1, y0, z0, u1, v0);
			t.vertexUV(x1, y0, z1, u1, v1);
			return;
		}
		if (side == 1) {
			t.normal(0, 1, 0);
			t.vertexUV(x1, y1, z1, u0, v1);
			t.vertexUV(x1, y1, z0, u0, v0);
			t.vertexUV(x0, y1, z0, u1, v0);
			t.vertexUV(x0, y1, z1, u1, v1);
			return;
		}
		if (side == 2) {
			t.normal(0, 0, -1);
			t.vertexUV(x0, y1, z0, u0, v0);
			t.vertexUV(x1, y1, z0, u1, v0);
			t.vertexUV(x1, y0, z0, u1, v1);
			t.vertexUV(x0, y0, z0, u0, v1);
			return;
		}
		if (side == 3) {
			t.normal(0, 0, 1);
			t.vertexUV(x0, y1, z1, u1, v0);
			t.vertexUV(x0, y0, z1, u1, v1);
			t.vertexUV(x1, y0, z1, u0, v1);
			t.vertexUV(x1, y1, z1, u0, v0);
			return;
		}
		if (side == 4) {
			t.normal(-1, 0, 0);
			t.vertexUV(x0, y1, z1, u0, v0);
			t.vertexUV(x0, y1, z0, u1, v0);
			t.vertexUV(x0, y0, z0, u1, v1);
			t.vertexUV(x0, y0, z1, u0, v1);
			return;
		}
		if (side == 5) {
			t.normal(1, 0, 0);
			t.vertexUV(x1, y0, z1, u1, v1);
			t.vertexUV(x1, y0, z0, u0, v1);
			t.vertexUV(x1, y1, z0, u0, v0);
			t.vertexUV(x1, y1, z1, u1, v0);
			return;
		}
	}

	public void renderFlippedSide(Tile tile, int xo, int yo, int zo, int side) {
		int xTex = tile.getTexture(side) % 16 << 5;
		int yTex = tile.getTexture(side) / 16 << 5;
		float u0 = 0, u1 = 0, v0 = 0, v1 = 0;
		if (side == 0 || side == 1) {
			u0 = tile.x0 / 16.0f;
			u1 = tile.x1 / 16.0f;
			v0 = tile.z0 / 16.0f;
			v1 = tile.z1 / 16.0f;
		} else if (side == 2 || side == 3) {
			u0 = tile.x0 / 16.0f;
			u1 = tile.x1 / 16.0f;
			v0 = tile.y0 / 16.0f;
			v1 = tile.y1 / 16.0f;
		} else if (side == 4 || side == 5) {
			u0 = tile.z0 / 16.0f;
			u1 = tile.z1 / 16.0f;
			v0 = tile.y0 / 16.0f;
			v1 = tile.y1 / 16.0f;
		}
		u0 += xTex / 512.0f;
		u1 += xTex / 512.0f;
		v0 += yTex / 512.0f;
		v1 += yTex / 512.0f;

		float x0 = xo + tile.x0;
		float x1 = xo + tile.x1;
		float y0 = yo + tile.y0;
		float y1 = yo + tile.y1;
		float z0 = zo + tile.z0;
		float z1 = zo + tile.z1;

		if (side == 0) {
			t.vertexUV(x1, y0, z1, u0, v1);
			t.vertexUV(x1, y0, z0, u0, v0);
			t.vertexUV(x0, y0, z0, u1, v0);
			t.vertexUV(x0, y0, z1, u1, v1);
			return;
		}
		if (side == 1) {
			t.vertexUV(x0, y1, z1, u0, v1);
			t.vertexUV(x0, y1, z0, u0, v0);
			t.vertexUV(x1, y1, z0, u1, v0);
			t.vertexUV(x1, y1, z1, u1, v1);
			return;
		}
		if (side == 2) {
			t.vertexUV(x0, y0, z0, u0, v0);
			t.vertexUV(x1, y0, z0, u1, v0);
			t.vertexUV(x1, y1, z0, u1, v1);
			t.vertexUV(x0, y1, z0, u0, v1);
			return;
		}
		if (side == 3) {
			t.vertexUV(x1, y1, z1, u1, v0);
			t.vertexUV(x1, y0, z1, u1, v1);
			t.vertexUV(x0, y0, z1, u0, v1);
			t.vertexUV(x0, y1, z1, u0, v0);
			return;
		}
		if (side == 4) {
			t.vertexUV(x0, y0, z1, u0, v0);
			t.vertexUV(x0, y0, z0, u1, v0);
			t.vertexUV(x0, y1, z0, u1, v1);
			t.vertexUV(x0, y1, z1, u0, v1);
			return;
		}
		if (side == 5) {
			t.vertexUV(x1, y1, z1, u1, v1);
			t.vertexUV(x1, y1, z0, u0, v1);
			t.vertexUV(x1, y0, z0, u0, v0);
			t.vertexUV(x1, y0, z1, u1, v0);
			return;
		}
	}
}
