/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.entity;

import static org.lwjgl.opengl.GL11.*;

import com.naronco.cubeshaft.Entity;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.level.tile.Tile;
import com.naronco.cubeshaft.render.Tesselator;
import com.naronco.cubeshaft.render.TextureLoader;
import com.naronco.cubeshaft.render.TileRenderer;

public class TileEntity extends Entity {
	private Tile tile;

	public TileEntity(Level level, Tile tile, int x, int y, int z) {
		super(level);
		this.tile = tile;
		setSize(0.98f, 0.98f);
		setPos(x, y, z);
	}

	public void tick() {
		this.xo = x;
		this.yo = y;
		this.zo = z;
		if (this.y < -100.0f) {
			this.removed = true;
		}

		move(xd, yd, zd);
		this.yd = (float) (this.yd - 0.06);
		this.yd *= 0.93f;
		if (onGround) {
			int xt = (int) x;
			int yt = (int) y;
			int zt = (int) z;
			removed = true;
			level.setTile(xt, yt, zt, tile.id);
		}
	}

	public void render(float delta) {
		glEnable(GL_TEXTURE_2D);
		glColor4f(1, 1, 1, 1);

		glBindTexture(GL_TEXTURE_2D,
				TextureLoader.load("/terrain.png", GL_NEAREST));
		glPushMatrix();

		float xx = xo + (x - xo) * delta;
		float yy = yo + (y - yo) * delta;
		float zz = zo + (z - zo) * delta;

		glTranslatef(xx, yy, zz);
		Tesselator t = Tesselator.instance;
		t.begin();
		for (int i = 0; i < 6; i++) {
			TileRenderer.instance.renderSide(tile, 0, 0, 0, i);
		}
		t.end();
		glPopMatrix();
		glDisable(GL_TEXTURE_2D);
	}
}
