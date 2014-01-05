/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package util;

public class Vec3 {
	public float x;
	public float y;
	public float z;

	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vec3 copy() {
		return new Vec3(x, y, z);
	}

	public Vec3 sub(Vec3 s) {
		x -= s.x;
		y -= s.y;
		z -= s.z;
		return this;
	}

	public Vec3 div(float v) {
		x /= v;
		y /= v;
		z /= v;
		return this;
	}

	public Vec3 cross(Vec3 s) {
		float _x = s.y * z - s.z * y;
		float _y = s.z * x - s.x * z;
		float _z = s.x * y - s.y * x;
		return set(_x, _y, _z);
	}

	public Vec3 normalize() {
		return div(length());
	}

	public float dot(Vec3 p) {
		return x * p.x + y * p.y + z * p.z;
	}

	public float length() {
		return (float) Math.sqrt(dot(this));
	}
}
