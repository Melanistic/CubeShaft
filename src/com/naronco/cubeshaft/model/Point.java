/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.model;

import util.*;

public class Point {
	public Vec3 pos;
	public float u, v;

	public Point(float x, float y, float z, float u, float v) {
		this(new Vec3(x, y, z), u, v);
	}

	public Point copy(float u, float v) {
		return new Point(this, u, v);
	}

	public Point(Point p, float u, float v) {
		this.pos = p.pos;
		this.u = u;
		this.v = v;
	}

	public Point(Vec3 pos, float u, float v) {
		this.pos = pos;
		this.u = u;
		this.v = v;
	}
}
