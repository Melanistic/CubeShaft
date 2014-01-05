/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.mob;

import static org.lwjgl.opengl.GL11.*;

import com.naronco.cubeshaft.Entity;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.model.SkeletonModel;
import com.naronco.cubeshaft.render.TextureLoader;

public class Human extends Mob {
	public static SkeletonModel HUMAN_MODEL = new SkeletonModel();
	private float rot;
	private float startTime;
	private float aDir;

	public Human(Level level, float x, float y, float z) {
		super(level);
		this.model = Human.HUMAN_MODEL;
		this.aDir = (float) (Math.random() + 1.0) * 0.01f;
		setPos(x, y, z);
		this.startTime = (float) Math.random() * 1239813.0f;
	}

	public void tick() {
		this.xo = x;
		this.yo = y;
		this.zo = z;
		if (this.y < -100.0f) {
			this.removed = true;
		}

		// this.rot += aDir;
		this.aDir = (float) (aDir * 0.99);
		this.aDir = (float) (this.aDir + (Math.random() - Math.random())
				* Math.random() * Math.random() * 0.07999999821186066);

		xa = (float) Math.sin(rot);
		za = (float) Math.cos(rot);

		boolean inWater = isInWater();
		boolean inLava = isInLava();
		if (inWater || inLava)
			yd += 0.04f;
		else if (onGround && Math.random() < 0.008)
			this.yd = 0.5f;
		if (inWater) {
			moveRel(xa, za, 0.02f);
			move(this.xd, this.yd, this.zd);
			this.xd *= 0.8f;
			this.yd *= 0.8f;
			this.zd *= 0.8f;
			this.yd = (float) (this.yd - 0.02);
			if ((this.collision) && (isFree(this.xd, this.yd + 0.6f, this.zd)))
				this.yd = 0.3F;
			return;
		}
		if (inLava) {
			moveRel(xa, za, 0.02f);
			move(this.xd, this.yd, this.zd);
			this.xd *= 0.5f;
			this.yd *= 0.5f;
			this.zd *= 0.5f;
			this.yd = (float) (this.yd - 0.02);
			if ((this.collision) && (isFree(this.xd, this.yd + 0.6f, this.zd)))
				this.yd = 0.3F;
			return;
		}

		moveRel(xa, za, onGround ? 0.09f : 0.019f);
		this.yd = (float) (this.yd - 0.08);
		move(this.xd, this.yd, this.zd);
		this.xd *= 0.91f;
		this.yd *= 0.98f;
		this.zd *= 0.91f;
		if (onGround) {
			this.xd *= 0.7f;
			this.zd *= 0.7f;
		}
	}

	public void render(float delta) {
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D,
				TextureLoader.load("/skeleton.png", GL_NEAREST));
		glPushMatrix();

		double time = System.nanoTime() / 1000000000.0D * 10.0D * 1.0f
				+ this.startTime;
		float s = 0.05833333f;
		float transPos = (float) (-Math.abs(Math.sin(time * 0.6662)) * 5.0 - 23.0);

		glTranslatef(this.xo + (this.x - this.xo) * delta, this.y
				+ (this.y - this.yo) * delta, this.z + (this.z - this.zo)
				* delta);
		glScalef(1.0f, -1.0f, 1.0f);
		glScalef(s, s, s);
		glTranslatef(0.0f, transPos, 0.0f);
		glRotatef(this.rot * 57.29578f + 180.0f, 0.0f, 1.0f, 0.0f);
		SkeletonModel model = (SkeletonModel) this.model;
		// model.head.yRot = (float) Math.sin(time * 0.83);
		// model.head.xRot = (float) Math.sin(time) * 0.8f;
		model.rightArm.xRot = (float) Math.sin(time * 0.6662 + Math.PI) * 1.0f;
		model.leftArm.xRot = (float) Math.sin(time * 0.6662) * 1.0f;
		model.rightLeg.xRot = (float) Math.sin(time * 0.6662) * 0.7f;
		model.leftLeg.xRot = (float) Math.sin(time * 0.6662 + Math.PI) * 0.7f;
		model.head.render();
		model.body.render();
		model.rightArm.render();
		model.leftArm.render();
		model.rightLeg.render();
		model.leftLeg.render();
		glPopMatrix();
		glDisable(GL_TEXTURE_2D);
	}

	public void hit(Entity e, int dmg) {
	}
}
