/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.phys;

public class AABB {
	public float x0, y0, z0;
	public float x1, y1, z1;

	public AABB(float x0, float y0, float z0, float x1, float y1, float z1) {
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
	}

	public AABB grow(float xa, float ya, float za) {
		float x0 = this.x0 - xa;
		float y0 = this.y0 - ya;
		float z0 = this.z0 - za;
		float x1 = this.x1 + xa;
		float y1 = this.y1 + ya;
		float z1 = this.z1 + za;
		return new AABB(x0, y0, z0, x1, y1, z1);
	}

	public boolean intersects(AABB bb) {
		if ((bb.x1 <= x0) || (bb.x0 >= x1)) {
			return false;
		}
		if ((bb.y1 <= y0) || (bb.y0 >= y1)) {
			return false;
		}
		return (bb.z1 > z0) && (bb.z0 < z1);
	}

	public void move(float xa, float ya, float za) {
		this.x0 += xa;
		this.y0 += ya;
		this.z0 += za;
		this.x1 += xa;
		this.y1 += ya;
		this.z1 += za;
	}

	public AABB copie() {
		return new AABB(x0, y0, z0, x1, y1, z1);
	}
}
