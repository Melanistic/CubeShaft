/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.model;

public class Quad {
	public Point[] points;

	public Quad(Point[] points) {
		this.points = points;
	}

	public Quad(Point[] points, int u0, int v0, int u1, int v1) {
		this(points);
		this.points[0] = points[0].copy(u1, v0);
		this.points[1] = points[1].copy(u0, v0);
		this.points[2] = points[2].copy(u0, v1);
		this.points[3] = points[3].copy(u1, v1);
	}
}
