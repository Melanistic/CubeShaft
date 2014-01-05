/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.render;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import com.naronco.cubeshaft.phys.*;

public final class FrustumCuller {

	private static FrustumCuller instance = new FrustumCuller();

	public float[][] frustrum = new float[16][16];
	public float[] projectionMatrix = new float[16];
	public float[] modelviewMatrix = new float[16];
	public float[] clippingMatrix = new float[16];

	private FloatBuffer projectionMatrixBuffer = BufferUtils
			.createFloatBuffer(16);
	private FloatBuffer modelviewMatrixBuffer = BufferUtils
			.createFloatBuffer(16);

	public static FrustumCuller getInstance() {
		instance.projectionMatrixBuffer.clear();
		instance.modelviewMatrixBuffer.clear();
		GL11.glGetFloat(2983, instance.projectionMatrixBuffer);
		GL11.glGetFloat(2982, instance.modelviewMatrixBuffer);
		instance.projectionMatrixBuffer.flip().limit(16);
		instance.projectionMatrixBuffer.get(instance.projectionMatrix);
		instance.modelviewMatrixBuffer.flip().limit(16);
		instance.modelviewMatrixBuffer.get(instance.modelviewMatrix);
		instance.clippingMatrix[0] = instance.modelviewMatrix[0]
				* instance.projectionMatrix[0] + instance.modelviewMatrix[1]
				* instance.projectionMatrix[4] + instance.modelviewMatrix[2]
				* instance.projectionMatrix[8] + instance.modelviewMatrix[3]
				* instance.projectionMatrix[12];
		instance.clippingMatrix[1] = instance.modelviewMatrix[0]
				* instance.projectionMatrix[1] + instance.modelviewMatrix[1]
				* instance.projectionMatrix[5] + instance.modelviewMatrix[2]
				* instance.projectionMatrix[9] + instance.modelviewMatrix[3]
				* instance.projectionMatrix[13];
		instance.clippingMatrix[2] = instance.modelviewMatrix[0]
				* instance.projectionMatrix[2] + instance.modelviewMatrix[1]
				* instance.projectionMatrix[6] + instance.modelviewMatrix[2]
				* instance.projectionMatrix[10] + instance.modelviewMatrix[3]
				* instance.projectionMatrix[14];
		instance.clippingMatrix[3] = instance.modelviewMatrix[0]
				* instance.projectionMatrix[3] + instance.modelviewMatrix[1]
				* instance.projectionMatrix[7] + instance.modelviewMatrix[2]
				* instance.projectionMatrix[11] + instance.modelviewMatrix[3]
				* instance.projectionMatrix[15];
		instance.clippingMatrix[4] = instance.modelviewMatrix[4]
				* instance.projectionMatrix[0] + instance.modelviewMatrix[5]
				* instance.projectionMatrix[4] + instance.modelviewMatrix[6]
				* instance.projectionMatrix[8] + instance.modelviewMatrix[7]
				* instance.projectionMatrix[12];
		instance.clippingMatrix[5] = instance.modelviewMatrix[4]
				* instance.projectionMatrix[1] + instance.modelviewMatrix[5]
				* instance.projectionMatrix[5] + instance.modelviewMatrix[6]
				* instance.projectionMatrix[9] + instance.modelviewMatrix[7]
				* instance.projectionMatrix[13];
		instance.clippingMatrix[6] = instance.modelviewMatrix[4]
				* instance.projectionMatrix[2] + instance.modelviewMatrix[5]
				* instance.projectionMatrix[6] + instance.modelviewMatrix[6]
				* instance.projectionMatrix[10] + instance.modelviewMatrix[7]
				* instance.projectionMatrix[14];
		instance.clippingMatrix[7] = instance.modelviewMatrix[4]
				* instance.projectionMatrix[3] + instance.modelviewMatrix[5]
				* instance.projectionMatrix[7] + instance.modelviewMatrix[6]
				* instance.projectionMatrix[11] + instance.modelviewMatrix[7]
				* instance.projectionMatrix[15];
		instance.clippingMatrix[8] = instance.modelviewMatrix[8]
				* instance.projectionMatrix[0] + instance.modelviewMatrix[9]
				* instance.projectionMatrix[4] + instance.modelviewMatrix[10]
				* instance.projectionMatrix[8] + instance.modelviewMatrix[11]
				* instance.projectionMatrix[12];
		instance.clippingMatrix[9] = instance.modelviewMatrix[8]
				* instance.projectionMatrix[1] + instance.modelviewMatrix[9]
				* instance.projectionMatrix[5] + instance.modelviewMatrix[10]
				* instance.projectionMatrix[9] + instance.modelviewMatrix[11]
				* instance.projectionMatrix[13];
		instance.clippingMatrix[10] = instance.modelviewMatrix[8]
				* instance.projectionMatrix[2] + instance.modelviewMatrix[9]
				* instance.projectionMatrix[6] + instance.modelviewMatrix[10]
				* instance.projectionMatrix[10] + instance.modelviewMatrix[11]
				* instance.projectionMatrix[14];
		instance.clippingMatrix[11] = instance.modelviewMatrix[8]
				* instance.projectionMatrix[3] + instance.modelviewMatrix[9]
				* instance.projectionMatrix[7] + instance.modelviewMatrix[10]
				* instance.projectionMatrix[11] + instance.modelviewMatrix[11]
				* instance.projectionMatrix[15];
		instance.clippingMatrix[12] = instance.modelviewMatrix[12]
				* instance.projectionMatrix[0] + instance.modelviewMatrix[13]
				* instance.projectionMatrix[4] + instance.modelviewMatrix[14]
				* instance.projectionMatrix[8] + instance.modelviewMatrix[15]
				* instance.projectionMatrix[12];
		instance.clippingMatrix[13] = instance.modelviewMatrix[12]
				* instance.projectionMatrix[1] + instance.modelviewMatrix[13]
				* instance.projectionMatrix[5] + instance.modelviewMatrix[14]
				* instance.projectionMatrix[9] + instance.modelviewMatrix[15]
				* instance.projectionMatrix[13];
		instance.clippingMatrix[14] = instance.modelviewMatrix[12]
				* instance.projectionMatrix[2] + instance.modelviewMatrix[13]
				* instance.projectionMatrix[6] + instance.modelviewMatrix[14]
				* instance.projectionMatrix[10] + instance.modelviewMatrix[15]
				* instance.projectionMatrix[14];
		instance.clippingMatrix[15] = instance.modelviewMatrix[12]
				* instance.projectionMatrix[3] + instance.modelviewMatrix[13]
				* instance.projectionMatrix[7] + instance.modelviewMatrix[14]
				* instance.projectionMatrix[11] + instance.modelviewMatrix[15]
				* instance.projectionMatrix[15];
		instance.frustrum[0][0] = instance.clippingMatrix[3]
				- instance.clippingMatrix[0];
		instance.frustrum[0][1] = instance.clippingMatrix[7]
				- instance.clippingMatrix[4];
		instance.frustrum[0][2] = instance.clippingMatrix[11]
				- instance.clippingMatrix[8];
		instance.frustrum[0][3] = instance.clippingMatrix[15]
				- instance.clippingMatrix[12];
		normalize(instance.frustrum, 0);
		instance.frustrum[1][0] = instance.clippingMatrix[3]
				+ instance.clippingMatrix[0];
		instance.frustrum[1][1] = instance.clippingMatrix[7]
				+ instance.clippingMatrix[4];
		instance.frustrum[1][2] = instance.clippingMatrix[11]
				+ instance.clippingMatrix[8];
		instance.frustrum[1][3] = instance.clippingMatrix[15]
				+ instance.clippingMatrix[12];
		normalize(instance.frustrum, 1);
		instance.frustrum[2][0] = instance.clippingMatrix[3]
				+ instance.clippingMatrix[1];
		instance.frustrum[2][1] = instance.clippingMatrix[7]
				+ instance.clippingMatrix[5];
		instance.frustrum[2][2] = instance.clippingMatrix[11]
				+ instance.clippingMatrix[9];
		instance.frustrum[2][3] = instance.clippingMatrix[15]
				+ instance.clippingMatrix[13];
		normalize(instance.frustrum, 2);
		instance.frustrum[3][0] = instance.clippingMatrix[3]
				- instance.clippingMatrix[1];
		instance.frustrum[3][1] = instance.clippingMatrix[7]
				- instance.clippingMatrix[5];
		instance.frustrum[3][2] = instance.clippingMatrix[11]
				- instance.clippingMatrix[9];
		instance.frustrum[3][3] = instance.clippingMatrix[15]
				- instance.clippingMatrix[13];
		normalize(instance.frustrum, 3);
		instance.frustrum[4][0] = instance.clippingMatrix[3]
				- instance.clippingMatrix[2];
		instance.frustrum[4][1] = instance.clippingMatrix[7]
				- instance.clippingMatrix[6];
		instance.frustrum[4][2] = instance.clippingMatrix[11]
				- instance.clippingMatrix[10];
		instance.frustrum[4][3] = instance.clippingMatrix[15]
				- instance.clippingMatrix[14];
		normalize(instance.frustrum, 4);
		instance.frustrum[5][0] = instance.clippingMatrix[3]
				+ instance.clippingMatrix[2];
		instance.frustrum[5][1] = instance.clippingMatrix[7]
				+ instance.clippingMatrix[6];
		instance.frustrum[5][2] = instance.clippingMatrix[11]
				+ instance.clippingMatrix[10];
		instance.frustrum[5][3] = instance.clippingMatrix[15]
				+ instance.clippingMatrix[14];
		normalize(instance.frustrum, 5);
		return instance;
	}

	private static void normalize(float[][] var0, int var1) {
		float var2 = (float) Math
				.sqrt(var0[var1][0] * var0[var1][0] + var0[var1][1]
						* var0[var1][1] + var0[var1][2] * var0[var1][2]);
		var0[var1][0] /= var2;
		var0[var1][1] /= var2;
		var0[var1][2] /= var2;
		var0[var1][3] /= var2;
	}

	public boolean isBoxInFrustrum(float x1, float y1, float z1, float x2,
			float y2, float z2) {
		for (int var7 = 0; var7 < 6; ++var7) {
			if (this.frustrum[var7][0] * x1 + this.frustrum[var7][1] * y1
					+ this.frustrum[var7][2] * z1 + this.frustrum[var7][3] <= 0.0F
					&& this.frustrum[var7][0] * x2 + this.frustrum[var7][1]
							* y1 + this.frustrum[var7][2] * z1
							+ this.frustrum[var7][3] <= 0.0F
					&& this.frustrum[var7][0] * x1 + this.frustrum[var7][1]
							* y2 + this.frustrum[var7][2] * z1
							+ this.frustrum[var7][3] <= 0.0F
					&& this.frustrum[var7][0] * x2 + this.frustrum[var7][1]
							* y2 + this.frustrum[var7][2] * z1
							+ this.frustrum[var7][3] <= 0.0F
					&& this.frustrum[var7][0] * x1 + this.frustrum[var7][1]
							* y1 + this.frustrum[var7][2] * z2
							+ this.frustrum[var7][3] <= 0.0F
					&& this.frustrum[var7][0] * x2 + this.frustrum[var7][1]
							* y1 + this.frustrum[var7][2] * z2
							+ this.frustrum[var7][3] <= 0.0F
					&& this.frustrum[var7][0] * x1 + this.frustrum[var7][1]
							* y2 + this.frustrum[var7][2] * z2
							+ this.frustrum[var7][3] <= 0.0F
					&& this.frustrum[var7][0] * x2 + this.frustrum[var7][1]
							* y2 + this.frustrum[var7][2] * z2
							+ this.frustrum[var7][3] <= 0.0F) {
				return false;
			}
		}

		return true;
	}

	public boolean isBoxInFrustrum(AABB bb) {
		return isBoxInFrustrum(bb.x0, bb.y0, bb.z0, bb.x1, bb.y1, bb.z1);
	}
}
