package com.naronco.cubeshaft.render;

import static org.lwjgl.opengl.GL11.*;
import util.Mth;

public class SkyDome {
	private int renderList;
	private boolean needsUpdate=true;
	
	public SkyDome() {
		renderList=glGenLists(1);
	}
	
	public void render() {
		if(needsUpdate) {
			glNewList(renderList, GL_COMPILE);
			Tesselator t=Tesselator.instance;
			
			t.begin(GL_LINES);
			t.color(0xffffff);
			
			float fv=Mth.PI_TIMES_TWO*0.1f;
			float dv=Mth.PI*0.1f;
			for(float f=0; f<Mth.PI_TIMES_TWO; f+=fv) {
				float x=Mth.sin(f);
				float z=Mth.cos(f);
				for(float d=0; d<Mth.PI_OVER_TWO; d+=dv) {
					float y=Mth.sin(d);
					float xzMul=Mth.cos(d);
					x*=xzMul;
					z*=xzMul;
					t.vertex(x, y, z);
				}
			}
			
			t.end();
			
			glEndList();
		}
		
		glCallList(renderList);
	}
}
