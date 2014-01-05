package com.naronco.cubeshaft.level;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.util.glu.Sphere;

public class Sun {
	private float radius = 16.0f;

	public Sun(float radius) {
		this.radius = radius;
	}

	public void render(float x, float y, float z) {
		glPushMatrix();
		glTranslatef(x, y, z);
		Sphere s = new Sphere();
		s.draw(radius, 16, 16);
		glPopMatrix();
	}
}
