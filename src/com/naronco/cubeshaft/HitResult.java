/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft;

import com.naronco.cubeshaft.player.Player;

public class HitResult {
	public int x;
	public int y;
	public int z;
	public int side;
	public Entity entity;

	public HitResult(int x, int y, int z, int side) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = side;
	}

	public HitResult(Entity e) {
		this.entity = e;
	}

	float distance(Player player) {
		float xd = this.x - player.x;
		float yd = this.y - player.y;
		float zd = this.z - player.z;
		return xd * xd + yd * yd + zd * zd;
	}
}
