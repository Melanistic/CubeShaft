/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.particle;

import java.util.ArrayList;
import java.util.List;

import util.Vec3;

import com.naronco.cubeshaft.player.Player;
import com.naronco.cubeshaft.render.Renderer;
import com.naronco.cubeshaft.render.Tesselator;

public class ParticleEngine {
	public List<Particle> particles = new ArrayList<Particle>();

	public void render(Player player, float delta) {
		Tesselator t = Tesselator.instance;
		synchronized (particles) {
			if (particles.size() == 0)
				return;
			float f0 = -(float) Math.cos(player.xRot * Math.PI / 180.0);
			float f1 = -(float) Math.sin(player.xRot * Math.PI / 180.0);
			float f2 = -f1 * (float) Math.sin(player.yRot * Math.PI / 180.0);
			float f3 = f0 * (float) Math.sin(player.yRot * Math.PI / 180.0);
			float f4 = (float) Math.cos(player.yRot * Math.PI / 180.0);
			t.begin();
			for (int i = 0; i < this.particles.size(); i++) {
				Particle p = particles.get(i);
				t.color(230, 230, 230);

				float u0 = (p.texture % 16 + p.xTex / 4.0f) / 16.0f;
				float u1 = u0 + 0.01560938f;
				float v0 = (p.texture / 16 + p.xTex / 4.0f) / 16.0f;
				float v1 = v0 + 0.01560938f;

				float f = 0.1f * p.d;

				float x = p.xo + (p.x - p.xo) * delta;
				float y = p.yo + (p.y - p.yo) * delta;
				float z = p.zo + (p.z - p.zo) * delta;

				Vec3 p0 = new Vec3(x - f0 * f - f2 * f, y - f4 * f, z - f1 * f
						- f3 * f);
				Vec3 p1 = new Vec3(x - f0 * f + f2 * f, y + f4 * f, z - f1 * f
						+ f3 * f);
				Vec3 p2 = new Vec3(x + f0 * f - f2 * f, y - f4 * f, z + f1 * f
						- f3 * f);

				Vec3 normal = Renderer.calcNormal(p0, p1, p2);

				t.normal(normal.x, normal.y, normal.z);
				t.vertexUV(x - f0 * f - f2 * f, y - f4 * f,
						z - f1 * f - f3 * f, u0, v1);
				t.vertexUV(x - f0 * f + f2 * f, y + f4 * f,
						z - f1 * f + f3 * f, u0, v0);
				t.vertexUV(x + f0 * f + f2 * f, y + f4 * f,
						z + f1 * f + f3 * f, u1, v0);
				t.vertexUV(x + f0 * f - f2 * f, y - f4 * f,
						z + f1 * f - f3 * f, u1, v1);
			}
		}
		t.end();
	}
}
