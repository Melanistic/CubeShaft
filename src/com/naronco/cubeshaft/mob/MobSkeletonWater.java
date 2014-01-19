package com.naronco.cubeshaft.mob;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.mob.ai.AIEntitySearch;
import com.naronco.cubeshaft.model.SkeletonModel;
import com.naronco.cubeshaft.player.Player;
import com.naronco.cubeshaft.render.TextureLoader;

public class MobSkeletonWater extends Mob {

	private SkeletonModel model = new SkeletonModel();
	
	public MobSkeletonWater(Level level) {
		super(level);
		tasks.add(new AIEntitySearch(32, Player.class));
	}
	
	@Override
	public float getNormalSpeed() 
	{
		return isInWater() ? normalSpeed*2 : normalSpeed;
	}
	
	@Override
	public void render(float delta) {
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D,
				TextureLoader.load("/mob/skeletonSunken.png", GL_NEAREST));
		glPushMatrix();

		double time = System.nanoTime() / 1000000000.0D * 10.0D * 1.0f;
		float s = 0.05833333f;

		glTranslatef(this.xo + (this.x - this.xo) * delta,
				(this.y + (this.y - this.yo) * delta) + 1.6f, this.z
						+ (this.z - this.zo) * delta);
		glScalef(1.0f, -1.0f, 1.0f);
		glScalef(s, s, s);
		glRotatef(xRot, 0.0f, 1.0f, 0.0f);

		model.head.xRot = xRot;
		model.head.yRot = yRot;
		model.body.yRot = yRot;
		model.rightArm.xRot = (float) Math.sin(time * 0.6662 + Math.PI) * 1.0f;
		model.leftArm.xRot = (float) Math.sin(time * 0.6662) * 1.0f;
		model.rightLeg.xRot = (float) Math.sin(time * 0.6662) * 0.7f;
		model.leftLeg.xRot = (float) Math.sin(time * 0.6662 + Math.PI) * 0.7f;
		model.head.render();
		model.body.render();
		model.rightArm.render();
		model.leftArm.render();
		model.rightLeg.render();
		model.leftLeg.render();
		glPopMatrix();
		glBindTexture(GL_TEXTURE_2D,
				TextureLoader.load("/terrain.png", GL_NEAREST));
		glDisable(GL_TEXTURE_2D);
	}
}
