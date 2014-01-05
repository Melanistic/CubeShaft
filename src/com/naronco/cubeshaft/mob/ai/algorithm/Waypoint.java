/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.mob.ai.algorithm;

public class Waypoint {
	public int x, y, f, g;

	public Waypoint(int x, int y, int f) {
		this.x = x;
		this.y = y;
		this.f = f;
	}

	public Waypoint(Waypoint w, int f) {
		this.x = w.x;
		this.y = w.y;
		this.f = f;
	}

	public Waypoint(int x, int y) {
		this.x = x;
		this.y = y;
		this.f = 0;
	}
}
