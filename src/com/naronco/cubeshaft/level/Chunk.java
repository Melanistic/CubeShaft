/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level;

import static org.lwjgl.opengl.GL11.*;

import com.naronco.cubeshaft.level.tile.*;
import com.naronco.cubeshaft.phys.*;
import com.naronco.cubeshaft.player.Player;
import com.naronco.cubeshaft.render.*;

public class Chunk {
	public AABB aabb;
	private Level level;
	private int x0;
	private int y0;
	private int z0;
	private int x1;
	private int y1;
	private int z1;
	private float x;
	private float y;
	private float z;
	private int glRenderList = -1;
	public boolean isDirty = true;
	public boolean isVisible = true;
	public static int chunkUpdates = 0;

	public Chunk(Level level, int x0, int y0, int z0, int x1, int y1, int z1) {
		this.level = level;
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x = (x0 + x1) / 2.0f;
		this.y = (y0 + y1) / 2.0f;
		this.z = (z0 + z1) / 2.0f;
		this.aabb = new AABB(x0, y0, z0, x1, y1, z1);
		this.glRenderList = glGenLists(3);
	}

	public final void update() {
		chunkUpdates++;

		Tesselator t = Tesselator.instance;
		for (int i = 0; i < 3; i++) {
			glNewList(this.glRenderList + i, GL_COMPILE);
			t.begin();

			for (int x = x0; x < x1; x++) {
				for (int y = y0; y < y1; y++) {
					for (int z = z0; z < z1; z++) {
						int type = this.level.getTile(x, y, z);
						if (type > 0) {
							Tile tile = Tile.tiles[type];
							tile.render(i, x, y, z);
						}
					}
				}
			}

			t.end();
			glEndList();
			this.isDirty = false;
		}
	}

	public int getRenderList(int index) {
		return glRenderList + index;
	}

	public float distToPlayer(Player player) {
		float xd = player.x - x;
		float yd = player.y - y;
		float zd = player.z - z;
		return xd * xd + yd * yd + zd * zd;
	}

	public void createRenderLists() {
		for (int i = 0; i < 3; i++) {
			glNewList(glRenderList + i, GL_COMPILE);
			glEndList();
		}
	}

	public void deleteRenderLists() {
		glDeleteLists(glRenderList, 3);
	}

	public final void clip(FrustumCuller clippingHelper) {
		this.isVisible = clippingHelper.isBoxInFrustrum(aabb);
	}
}
