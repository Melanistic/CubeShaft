/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.tile;

import java.util.Random;

import com.naronco.cubeshaft.ItemEntity;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.particle.Particle;
import com.naronco.cubeshaft.particle.ParticleEngine;
import com.naronco.cubeshaft.phys.AABB;
import com.naronco.cubeshaft.render.Tesselator;
import com.naronco.cubeshaft.render.TileRenderer;

public class Tile {
	public static final Tile[] tiles = new Tile[256];
	public static final Tile stone = new Tile(1, 1);
	public static final Tile grass = new GrassTile(2);
	public static final Tile dirt = new DirtTile(3, 2);
	public static final Tile planks = new Tile(5, 4);
	public static final Tile sapling = new SaplingTile(6);
	public static final Tile sand = new Tile(7, 17);
	public static final Tile flowingWater = new FlowingLiquidTile(8, 1);
	public static final Tile water = new LiquidTile(9, 1);
	public static final Tile flowingLava = new FlowingLiquidTile(10, 2);
	public static final Tile lava = new LiquidTile(11, 2);
	public static final Tile wood = new WoodTile(12);
	public static final Tile leaves = new LeafTile(13);
	public static final Tile tallGrass = new TallGrassTile(14);
	public static final Tile glass = new GlassTile(15);
	public static final Tile brick = new Tile(16, 21);
	public static final Tile rock = new Tile(17, 16 * 15);
	public static final Tile hellStone = new Tile(18, 16 * 15 + 1);
	public static final Tile bamboo = new BambooTile(19, 16);
	public static final Tile fence = new FenceTile(20, 17);

	public int texIndex;
	public final int id;
	public float x0, y0, z0;
	public float x1, y1, z1;

	protected Tile(int id) {
		tiles[id] = this;
		this.id = id;
		setBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	public void setBounds(float x0, float y0, float z0, float x1, float y1,
			float z1) {
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
	}

	protected Tile(int id, int texIndex) {
		this(id);
		this.texIndex = texIndex;
	}

	public boolean render(int renderType, int x, int y, int z) {
		return TileRenderer.instance.render(this, x, y, z, renderType);
	}

	public void renderGui(int renderType, int x, int y, int z) {
		TileRenderer.instance.render(this, x, y, z, renderType);
	}

	public boolean canRender(Level level, int x, int y, int z, int renderType,
			int side) {
		if (renderType == 2)
			return false;
		Tile tile = tiles[level.getTile(x, y, z)];
		if (!(tile == null ? false : tile.isNormalRender()))
			return true;
		return false;
	}

	public int getTexture(int side) {
		return texIndex;
	}

	public static void renderHover(Tesselator t, int x, int y, int z, int side) {
		float x0 = x;
		float x1 = x + 1.0f;
		float y0 = y;
		float y1 = y + 1.0f;
		float z0 = z;
		float z1 = z + 1.0f;

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

	public static AABB getDefaultAABB(int x, int y, int z) {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}

	public AABB getAABB(int x, int y, int z) {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}

	public boolean isNormalRender() {
		return true;
	}

	public boolean isPickable() {
		return true;
	}

	public void tick(Level level, int x, int y, int z, Random random) {
	}

	public void destroy(Level level, int x, int y, int z,
			ParticleEngine particleEngine) {
		synchronized (particleEngine.particles) {
			for (int xp = 0; xp < 6; xp++)
				for (int yp = 0; yp < 6; yp++)
					for (int zp = 0; zp < 6; zp++) {
						float xx = x + (xp + 0.5f) / 6 * 0.8f + 0.1f;
						float yy = y + (yp + 0.5f) / 6 * 0.8f + 0.1f;
						float zz = z + (zp + 0.5f) / 6 * 0.8f + 0.1f;
						Particle p = new Particle(level, xx, yy, zz, xx - x
								- 0.5f, yy - y - 0.5f, zz - z - 0.5f,
								this.texIndex);
						particleEngine.particles.add(p);
					}
		}
		float xx = (float) (Math.random() - Math.random()) * 0.3f;
		float yy = (float) (Math.random() - Math.random()) * 0.3f;
		float zz = (float) (Math.random() - Math.random()) * 0.3f;
		level.addEntity(new ItemEntity(level, this, x + 0.25f + xx, y + 0.25f
				+ yy, z + 0.25f + zz));
	}

	public int getLiquidType() {
		return 0;
	}

	public void neighborChange(Level level, int x, int y, int z, int id) {
	}

	public void tileAdd(Level level, int x, int y, int z) {
	}

	public int getRenderMode() {
		return 0;
	}
}
