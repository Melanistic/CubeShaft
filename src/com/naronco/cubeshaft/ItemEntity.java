/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft;

import static org.lwjgl.opengl.GL11.*;

import java.util.List;

import com.naronco.cubeshaft.level.IEntitySelector;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.level.tile.Tile;
import com.naronco.cubeshaft.render.Tesselator;
import com.naronco.cubeshaft.render.TileRenderer;

public class ItemEntity extends Entity {
	private int time;
	private int pickup;
	private Tile tile;
	private int value;

	public ItemEntity(Level level, Tile tile, float x, float y, float z) {
		this(level, tile, x, y, z, 1);
	}

	public ItemEntity(Level level, Tile tile, float x, float y, float z, int val) {
		super(level);
		this.tile = tile;
		setSize(0.2f, 0.2f);
		setPos(x, y, z);
		value = val;
	}

	public void tick() {
		time++;
		xRot = time * 2 / 3.0f;
		
		if(pickup>0)
			pickup--;

		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.xRotO = xRot;
		if (this.y < -100.0f) {
			this.removed = true;
		}

		move(xd, yd, zd);
		this.yd = (float) (this.yd - 0.016);
		this.xd *= 0.91f;
		this.yd *= 0.98f;
		this.zd *= 0.91f;
		if (onGround) {
			this.xd *= 0.7f;
			this.zd *= 0.7f;
		}
		if (!removed) {
			List<Entity> e = level.getEntitysExcludingEntity(
					aabb.grow(1, 1, 1), this, new IEntitySelector() {
						@Override
						public boolean isValidEntity(Entity e) {
							return e instanceof ItemEntity;
						}
					});

			for (Entity en : e) {
				if (!en.removed
						&& ((ItemEntity) en).getTile() == this.getTile()) {
					this.value += ((ItemEntity) en).value;
					en.removed = true;
				}
			}
		}
	}
	public void setTimebeforPickup(int t)
	{
		pickup = t;
	}
	public int getTimebeforPickup()
	{
		return pickup;
	}

	public void render(float delta) {
		float step = (float) Math.sin(time / 4.0f / 3.0f) * 0.1f;
		glColor4f(1, 1, 1, 1);
		glPushMatrix();

		float xx = xo + (x - xo) * delta;
		float yy = yo + (y - yo) * delta;
		float zz = zo + (z - zo) * delta;
		float xr = xRotO + (xRot - xRotO) * delta;

		glTranslatef(xx + 0.25f, yy + step, zz + 0.25f);
		glRotatef(xr, 0, 1, 0);
		glTranslatef(-0.25f, 0, -0.25f);
		glScalef(0.5f, 0.5f, 0.5f);
		Tesselator t = Tesselator.instance;
		t.begin();
		for (int i = 0; i < 6; i++) {
			TileRenderer.instance.renderSide(tile, 0, 0, 0, i);
		}
		t.end();
		glPopMatrix();
	}

	public Tile getTile() {
		return tile;
	}
}
