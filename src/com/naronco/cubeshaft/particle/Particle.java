/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.particle;

import com.naronco.cubeshaft.Entity;
import com.naronco.cubeshaft.level.Level;

public class Particle extends Entity {
	private float xxa;
	private float yya;
	private float zza;
	public int texture;
	float xTex;
	float yTex;
	private int time = 0;
	private int removeTime = 0;
	float d;

	public Particle(Level level, float x, float y, float z, float xs, float ys,
			float zs, int texture) {
		super(level);
		this.texture = texture;
		setSize(0.2f, 0.2f);
		this.heightOffset = this.bbHeight / 2.0f;
		setPos(x, y, z);
		this.xxa = xs + (float) (Math.random() * 2.0 - 1.0) * 0.4f;
		this.yya = ys + (float) (Math.random() * 2.0 - 1.0) * 0.4f;
		this.zza = zs + (float) (Math.random() * 2.0 - 1.0) * 0.4f;
		float f = (float) (Math.random() + Math.random() + 1.0) * 0.15f;
		float dist = (float) Math.sqrt(xxa * xxa + yya * yya + zza * zza);
		this.xxa = this.xxa / dist * f * 0.16f;
		this.yya = this.yya / dist * f * 0.16f + 0.03f;
		this.zza = this.zza / dist * f * 0.16f;
		this.xTex = (float) Math.random() * 3.0f;
		this.yTex = (float) Math.random() * 3.0f;
		this.d = (float) (Math.random() * 0.5 + 0.5);
		this.removeTime = (int) (4.0 / (Math.random() * 0.9 + 0.1) * 3.0);
		this.time = 0;
	}

	public boolean blocks(Entity e) {
		return false;
	}

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.time++ >= this.removeTime) {
			this.removed = true;
		}
		this.yya = (float) (this.yya - 0.01);
		move(this.xxa, this.yya, this.zza);
		this.xxa *= 0.98f;
		this.yya *= 0.98f;
		this.zza *= 0.98f;
		if (this.onGround) {
			this.xxa *= 0.7f;
			this.zza *= 0.7f;
		}
	}
}
