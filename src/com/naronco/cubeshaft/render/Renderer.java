/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.render;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import util.Vec3;

import com.naronco.cubeshaft.Cubeshaft;
import com.naronco.cubeshaft.gui.HeldTile;
import com.naronco.cubeshaft.level.tile.Tile;

public class Renderer {
	private Cubeshaft game;
	private float redFog;
	private float greenFog;
	private float blueFog;
	public HeldTile heldTile;
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
	private int viewDistance = 0;

	public Renderer(Cubeshaft game) {
		this.game = game;
		this.heldTile = new HeldTile();
		this.viewDistance = game.levelRenderer.viewDistance;
	}

	public Vec3 getEyePos(float delta) {
		float x = this.game.player.xo
				+ (this.game.player.x - this.game.player.xo) * delta;
		float y = this.game.player.yo
				+ (this.game.player.y - this.game.player.yo) * delta;
		float z = this.game.player.zo
				+ (this.game.player.z - this.game.player.zo) * delta;
		return new Vec3(x, y, z);
	}

	public void enableGuiMode() {
		glClear(256);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, game.width, game.height, 0.0, 100.0, 300.0);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glTranslatef(0.0f, 0.0f, -200.0f);
	}

	public void renderFog() {
		Tile tile = Tile.tiles[this.game.level.getTile(
				(int) this.game.player.x, (int) (this.game.player.y + 0.12f),
				(int) this.game.player.z)];
		if (tile != null && tile.getLiquidType() == 1) {
			glFogi(GL_FOG_MODE, GL_EXP);
			glFogf(GL_FOG_DENSITY, 0.1f);
			glFog(GL_FOG_COLOR, getFloatBuffer(0.02f, 0.02f, 0.2f, 1.0f));
			glLightModel(GL_LIGHT_MODEL_AMBIENT,
					getFloatBuffer(0.3f, 0.3f, 0.7f, 1.0f));
		} else if (tile != null && tile.getLiquidType() == 2) {
			glFogi(GL_FOG_MODE, GL_EXP);
			glFogf(GL_FOG_DENSITY, 2.0f);
			glFog(GL_FOG_COLOR, getFloatBuffer(0.6f, 0.1f, 0.0f, 1.0f));
			glLightModel(GL_LIGHT_MODEL_AMBIENT,
					getFloatBuffer(0.4f, 0.3f, 0.3f, 1.0f));
		} else {
			glFogi(GL_FOG_MODE, GL_EXP);
			glFogf(GL_FOG_DENSITY,
					0.001f * (1 << (viewDistance + 1) << viewDistance));
			glFog(GL_FOG_COLOR,
					this.getFloatBuffer(redFog, greenFog, blueFog, 1.0f));
			glLightModel(GL_LIGHT_MODEL_AMBIENT,
					getFloatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
		}
		// glEnable(GL_COLOR_MATERIAL);
		// glColorMaterial(GL_FRONT, GL_AMBIENT);
		// glEnable(GL_LIGHTING);
	}

	public static Vec3 calcNormal(Vec3 p0, Vec3 p1, Vec3 p2) {
		Vec3 np0 = p1.copy().sub(p0);
		Vec3 np1 = p2.copy().sub(p0);
		return np0.copy().cross(np1).normalize();
	}

	private FloatBuffer getFloatBuffer(float... fs) {
		this.buffer.clear();
		this.buffer.put(fs);
		this.buffer.flip();
		return this.buffer;
	}
}
