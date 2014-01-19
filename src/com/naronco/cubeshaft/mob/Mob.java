/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.mob;

import java.util.ArrayList;
import java.util.List;

import com.naronco.cubeshaft.Entity;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.mob.ai.AIBase;
import com.naronco.cubeshaft.mob.ai.Navigator;
import com.naronco.cubeshaft.model.Model;

public class Mob extends Entity {
	public int maxHealth, health;
	protected Model model;
	public float xa, za;
	protected float normalSpeed = 0.2F;
	protected List<AIBase> tasks = new ArrayList<>();
	public Entity target;
	public Navigator navigator = new Navigator(this);
	
	public Mob(Level level) {
		super(level);
	}

	public void hit(Entity e, int dmg) {
	}

	public boolean isHeadInWater() {
		return this.level.containsLiquid(this.aabb.grow(0.0f, -1.0f, 0.0f), 1);
	}

	public void handelAI() {
		for (AIBase base : tasks) {
			base.task(this);
		}
	}

	public float getNormalSpeed() 
	{
		return normalSpeed;
	}
	
	@Override
	public void tick() {
		super.tick();
		handelAI();
		navigator.navigate();
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		float xa = 0.0f;
		float za = 0.0f;
		boolean inWater = isInWater();
		boolean inLava = isInLava();

		float speed = 0.007f;
		if (inWater) {
			moveRel(xa, za, speed);
			move(this.xd, this.yd, this.zd);
			this.xd *= 0.8f;
			this.yd *= 0.8f;
			this.zd *= 0.8f;
			this.yd = (float) (this.yd - 0.007);
			if ((this.collision) && (isFree(this.xd, this.yd + 0.6F, this.zd)))
				this.yd = 0.2f;
			return;
		}
		if (inLava) {
			moveRel(xa, za, speed);
			move(this.xd, this.yd, this.zd);
			this.xd *= 0.5f;
			this.yd *= 0.5f;
			this.zd *= 0.5f;
			this.yd = (float) (this.yd - 0.007);
			if ((this.collision) && (isFree(this.xd, this.yd + 0.6F, this.zd)))
				this.yd = 0.2f;
			return;
		}
		moveRel(xa, za, onGround ? 0.03f : speed);
		move(this.xd, this.yd, this.zd);
		this.xd *= 0.91f;
		this.yd *= 0.98f;
		this.zd *= 0.91f;
		this.yd -= 0.016f;
		if (onGround) {
			this.xd *= 0.6f;
			this.zd *= 0.6f;
		}
	}

	public void walk(float speed) {
		xd = -(float) (Math.sin(yRot) * speed);
		zd = -(float) (Math.cos(yRot) * speed);
	}

	public void jump() {
		if (onGround)
			yd = 0.23f;
	}

}
