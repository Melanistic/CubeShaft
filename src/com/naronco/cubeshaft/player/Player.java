/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.player;

import static org.lwjgl.opengl.GL11.*;

import com.melanistics.TickHandler;
import com.melanistics.event.PlayerJumpEvent;
import com.naronco.cubeshaft.Entity;
import com.naronco.cubeshaft.INamedEntity;
import com.naronco.cubeshaft.ItemEntity;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.mob.Mob;
import com.naronco.cubeshaft.model.HumanModel;
import com.naronco.cubeshaft.phys.HitBox;
import com.naronco.cubeshaft.render.TextureLoader;

public class Player extends Entity implements INamedEntity
{
	public static HumanModel PLAYER_MODEL = new HumanModel();
	public boolean[] keys = new boolean[10];
	public Inventory inventory;
	private String name;

	public Player(Level level, String par1) {
		
		super(level);
		name = par1;
		this.heightOffset = 1.62f;
		this.inventory = new Inventory();
		hitbox = HitBox.getHumanModel(bbHeight, bbWidth);
	}
	
	@Override
	public void collide(Entity e, String part) {
		if (e instanceof Mob)
			push(e);
		if (e instanceof ItemEntity && ((ItemEntity) e).getTimebeforPickup()==0) {
			ItemEntity i = (ItemEntity) e;
			if (inventory.items.size() < 13 && !inventory.items.contains(i.getTile())) 
			{
				inventory.add(i.getTile().id);
			}
			e.removed = true;
		}
	}

	private void push(Entity e) {
		float xd = e.x - this.x;
		float zd = e.z - this.z;
		e.xd += xd * 0.1f;
		e.zd += zd * 0.1f;
		this.xd -= xd * 0.1f;
		this.zd -= zd * 0.1f;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		float xa = 0.0f;
		float za = 0.0f;
		boolean inWater = isInWater();
		boolean inLava = isInLava();
		if (this.keys[0])
			za -= 1.0f;
		if (this.keys[1])
			za += 1.0f;
		if (this.keys[2])
			xa -= 1.0f;
		if (this.keys[3])
			xa += 1.0f;
		if (this.keys[4]) 
		{		
			float jump = yd;
			if (inWater) 		
				jump += 0.01;
			
			else if (inLava) 	
				jump += 0.01;
			
			else if (onGround) 	
				jump = 0.23f;
			
			PlayerJumpEvent e = new PlayerJumpEvent(this, jump);
			TickHandler.EVENT_BUS.postEvent(e);
			if(!e.isCancled() && (onGround || inLava || inWater))
			{
				this.yd = (float)e.getResult();
			}
		}
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

	public void render(float delta) {
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D,
				TextureLoader.load("/mob/char.png", GL_NEAREST));
		glPushMatrix();

		double time = System.nanoTime() / 1000000000.0D * 10.0D * 1.0f;
		float s = 0.05833333f;

		glTranslatef(this.xo + (this.x - this.xo) * delta, this.y
				+ (this.y - this.yo) * delta, this.z + (this.z - this.zo)
				* delta);
		glScalef(1.0f, -1.0f, 1.0f);
		glScalef(s, s, s);
		glRotatef(xRot, 0.0f, 1.0f, 0.0f);
		HumanModel model = (HumanModel) PLAYER_MODEL;
		model.head.xRot = xRot;
		model.head.yRot = yRot;
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

	@Override
	public boolean hasName() 
	{
		return true;
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public void setName(String s) 
	{
		name = s;
	}
}
