/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.model;

import static org.lwjgl.opengl.GL11.*;

public class Cube {
	private Point[] points;
	private Quad[] sides;
	private int u;
	private int v;
	private float x;
	private float y;
	private float z;
	public float xRot;
	public float yRot;
	public float zRot;
	private boolean hasList = false;
	private int list = 0;

	public Cube(int u, int v) {
		this.u = u;
		this.v = v;
	}

	public void setBounds(float x, float y, float z, int width, int height,
			int depth) {
		this.points = new Point[8];
		this.sides = new Quad[6];
		float w = x + width;
		float h = y + height;
		float d = z + depth;
		Point p0 = new Point(x, y, z, 0.0f, 0.0f);
		Point p1 = new Point(w, y, z, 0.0f, 8.0f);
		Point p2 = new Point(w, h, z, 8.0f, 8.0f);
		Point p3 = new Point(x, h, z, 8.0f, 0.0f);
		Point p4 = new Point(x, y, d, 0.0f, 0.0f);
		Point p5 = new Point(w, y, d, 0.0f, 8.0f);
		Point p6 = new Point(w, h, d, 8.0f, 8.0f);
		Point p7 = new Point(x, h, d, 8.0f, 0.0f);
		this.points[0] = p0;
		this.points[1] = p1;
		this.points[2] = p2;
		this.points[3] = p3;
		this.points[4] = p4;
		this.points[5] = p5;
		this.points[6] = p6;
		this.points[7] = p7;
		this.sides[0] = new Quad(new Point[] { p5, p1, p2, p6 }, u + depth
				+ width, v + depth, u + depth + width + depth, v + depth
				+ height);
		this.sides[1] = new Quad(new Point[] { p0, p4, p7, p3 }, u, v + depth,
				u + depth, v + depth + height);
		this.sides[2] = new Quad(new Point[] { p5, p4, p0, p1 }, u + depth, v,
				u + depth + width, v + depth);
		this.sides[3] = new Quad(new Point[] { p2, p3, p7, p6 }, u + depth
				+ width, v, u + depth + width + width, v + depth);
		this.sides[4] = new Quad(new Point[] { p1, p0, p3, p2 }, u + depth, v
				+ depth, u + depth + width, v + depth + height);
		this.sides[5] = new Quad(new Point[] { p4, p5, p6, p7 }, u + depth
				+ width + depth, v + depth, u + depth + width + depth + width,
				v + depth + height);
	}

	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void render() {
		if (!this.hasList) {
			this.list = glGenLists(1);
			glNewList(this.list, GL_COMPILE);
			glBegin(GL_QUADS);
			for (int i = 0; i < this.sides.length; i++) {
				Quad side = this.sides[i];
				glColor3f(1.0f, 1.0f, 1.0f);
				for (int j = 3; j >= 0; j--) {
					Point point = side.points[j];
					glTexCoord2f(point.u / 63.999001f, point.v / 31.999001f);
					glVertex3f(point.pos.x, point.pos.y, point.pos.z);
				}
			}
			glEnd();
			glEndList();
			this.hasList = true;
		}
		float rmul = 57.29578f;
		glPushMatrix();
		glTranslatef(this.x, this.y, this.z);
		glRotatef(this.zRot * rmul, 0.0f, 0.0f, 1.0f);
		glRotatef(this.yRot * rmul, 0.0f, 1.0f, 0.0f);
		glRotatef(this.xRot * rmul, 1.0f, 0.0f, 0.0f);
		glCallList(this.list);
		glPopMatrix();
	}
}
