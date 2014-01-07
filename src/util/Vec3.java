/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package util;

public class Vec3 {
	public float x, y, z;

	public Vec3() {
		clear();
	}

	public Vec3(float x, float y, float z) {
		set(x, y, z);
	}

	public Vec3(Vec3 v) {
		set(v);
	}

	public Vec3 clear() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
		return this;
	}

	public Vec3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vec3 set(Vec3 v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		return this;
	}

	public Vec3 add(Vec3 s) {
		x += s.x;
		y += s.y;
		z += s.z;
		return this;
	}

	public Vec3 sub(Vec3 s) {
		x -= s.x;
		y -= s.y;
		z -= s.z;
		return this;
	}

	public Vec3 mul(float v) {
		x *= v;
		y *= v;
		z *= v;
		return this;
	}

	public Vec3 div(float v) {
		v = 1.0f / v;
		x *= v;
		y *= v;
		z *= v;
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

	public float lengthSquared() {
		return dot(this);
	}

	public float length() {
		return (float) Math.sqrt(dot(this));
	}

	public Vec3 lerp(Vec3 to, float t) {
		return add(to.copy().sub(this).mul(t));
	}

	public Vec3 copy() {
		return new Vec3(x, y, z);
	}
}
