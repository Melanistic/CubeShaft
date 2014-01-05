/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import com.naronco.cubeshaft.HitResult;
import com.naronco.cubeshaft.level.tile.Tile;
import com.naronco.cubeshaft.render.Tesselator;

public class LevelRenderer {
	public Level level;
	public Chunk[] chunkCache;
	public List<Chunk> loadedChunks = new ArrayList<Chunk>();
	private int xChunks;
	private int yChunks;
	private int zChunks;
	public int viewDistance = 0;

	public LevelRenderer(Level level) {
		this.level = level;
		level.levelRenderers.add(this);
		init();
	}

	public void init() {
		if (this.chunkCache != null) {
			for (int i = 0; i < chunkCache.length; i++) {
				this.chunkCache[i].deleteRenderLists();
			}
		}

		this.xChunks = (level.width + 16 - 1) / 16;
		this.yChunks = (level.height + 16 - 1) / 16;
		this.zChunks = (level.depth + 16 - 1) / 16;
		this.chunkCache = new Chunk[xChunks * yChunks * zChunks];

		for (int xc = 0; xc < xChunks; xc++)
			for (int yc = 0; yc < yChunks; yc++)
				for (int zc = 0; zc < zChunks; zc++) {
					int xc0 = xc << 4;
					int yc0 = yc << 4;
					int zc0 = zc << 4;

					int xc1 = (xc + 1) << 4;
					int yc1 = (yc + 1) << 4;
					int zc1 = (zc + 1) << 4;

					if (xc1 > level.width)
						xc1 = level.width;
					if (yc1 > level.height)
						yc1 = level.height;
					if (zc1 > level.depth)
						zc1 = level.depth;

					this.chunkCache[(yc * xChunks + zc) * zChunks + xc] = new Chunk(
							level, xc0, yc0, zc0, xc1, yc1, zc1);
				}

		queueChunks(0, 0, 0, level.width, level.height, level.depth);
	}

	public void render(int index) {
		for (int i = 0; i < loadedChunks.size(); i++) {
			Chunk c = loadedChunks.get(i);
			if (c.isVisible)
				glCallList(c.getRenderList(index));
		}
	}

	public static void renderPickBox(HitResult hitResult) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glLineWidth(1.5f);
		glColor4f(0.0f, 0.0f, 0.0f, 0.8f);
		float x = hitResult.x;
		float y = hitResult.y;
		float z = hitResult.z;
		glBegin(GL_LINE_STRIP);
		glVertex3f(x, y, z);
		glVertex3f(x + 1.0f, y, z);
		glVertex3f(x + 1.0f, y, z + 1.0f);
		glVertex3f(x, y, z + 1.0f);
		glVertex3f(x, y, z);
		glEnd();
		glBegin(GL_LINE_STRIP);
		glVertex3f(x, y + 1.0f, z);
		glVertex3f(x + 1.0f, y + 1.0f, z);
		glVertex3f(x + 1.0f, y + 1.0f, z + 1.0f);
		glVertex3f(x, y + 1.0f, z + 1.0f);
		glVertex3f(x, y + 1.0f, z);
		glEnd();
		glBegin(GL_LINES);
		glVertex3f(x, y, z);
		glVertex3f(x, y + 1.0f, z);
		glVertex3f(x + 1.0f, y, z);
		glVertex3f(x + 1.0f, y + 1.0f, z);
		glVertex3f(x + 1.0f, y, z + 1.0f);
		glVertex3f(x + 1.0f, y + 1.0f, z + 1.0f);
		glVertex3f(x, y, z + 1.0f);
		glVertex3f(x, y + 1.0f, z + 1.0f);
		glEnd();
		glDisable(GL_BLEND);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glColor4f(1.0f, 1.0f, 1.0f, 0.3f);
		Tesselator t = Tesselator.instance;
		t.begin();
		Tile.renderHover(t, (int) x, (int) y, (int) z, hitResult.side);
		t.end();
		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		glDisable(GL_BLEND);
	}

	public final void queueChunks(int x0, int y0, int z0, int x1, int y1, int z1) {
		x0 = (int) Math.floor(x0 / 16.0);
		x1 = (int) Math.floor(x1 / 16.0);
		y0 = (int) Math.floor(y0 / 16.0);
		y1 = (int) Math.floor(y1 / 16.0);
		z0 = (int) Math.floor(z0 / 16.0);
		z1 = (int) Math.floor(z1 / 16.0);
		if (x0 < 0)
			x0 = 0;
		if (y0 < 0)
			y0 = 0;
		if (z0 < 0)
			z0 = 0;
		if (x1 >= xChunks)
			x1 = xChunks - 1;
		if (y1 >= yChunks)
			y1 = yChunks - 1;
		if (z1 >= zChunks)
			z1 = zChunks - 1;

		for (int x = x0; x <= x1; x++)
			for (int y = y0; y <= y1; y++)
				for (int z = z0; z <= z1; z++) {
					Chunk chunk = this.chunkCache[(y * this.zChunks + z)
							* this.xChunks + x];
					chunk.isDirty = true;
				}
	}
}
