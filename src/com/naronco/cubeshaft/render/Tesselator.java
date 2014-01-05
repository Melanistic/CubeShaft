/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GLContext;

public class Tesselator {
	private static boolean TRIANGLE_MODE = false;
	private static boolean USE_VBO = false;
	private ByteBuffer byteBuffer;
	private IntBuffer intBuffer;
	private FloatBuffer floatBuffer;
	private ShortBuffer shortBuffer;
	private int[] array;
	private int vertices;
	private double u;
	private double v;
	private int tex2;
	private int col;
	private boolean hasColor = false;
	private boolean hasTexture = false;
	private boolean hasTexture2 = false;
	private boolean hasNormal = false;
	private int p = 0;
	private int count = 0;
	private boolean noColor = false;
	private int mode;
	private double xo;
	private double yo;
	private double zo;
	private int normal;

	public static Tesselator instance = new Tesselator(0x200000);

	private boolean tesselating = false;
	private boolean vboMode = false;
	private IntBuffer vboIds;
	private int vboId = 0;
	private int vboCounts = 10;
	private int size;

	public Tesselator(int size) {
		this.size = size;

		this.byteBuffer = BufferUtils.createByteBuffer(size * 4);
		this.intBuffer = byteBuffer.asIntBuffer();
		this.floatBuffer = byteBuffer.asFloatBuffer();
		this.shortBuffer = byteBuffer.asShortBuffer();
		this.array = new int[size];

		this.vboMode = ((USE_VBO) && (GLContext.getCapabilities().GL_ARB_vertex_buffer_object));
		if (this.vboMode) {
			this.vboIds = BufferUtils.createIntBuffer(this.vboCounts);
			ARBVertexBufferObject.glGenBuffersARB(this.vboIds);
		}
	}

	public void end() {
		if (!this.tesselating)
			throw new IllegalStateException("Not tesselating!");
		this.tesselating = false;
		if (this.vertices > 0) {
			this.intBuffer.clear();
			this.intBuffer.put(this.array, 0, this.p);

			this.byteBuffer.position(0);
			this.byteBuffer.limit(this.p * 4);

			if (this.vboMode) {
				this.vboId = ((this.vboId + 1) % this.vboCounts);
				ARBVertexBufferObject.glBindBufferARB(34962,
						this.vboIds.get(this.vboId));

				ARBVertexBufferObject.glBufferDataARB(34962, this.byteBuffer,
						35040);
			}

			if (this.hasTexture) {
				if (this.vboMode) {
					glTexCoordPointer(2, 5126, 32, 12L);
				} else {
					this.floatBuffer.position(3);
					glTexCoordPointer(2, 32, this.floatBuffer);
				}
				glEnableClientState(32888);
			}
			if (this.hasTexture2) {
				glClientActiveTexture(GL_TEXTURE1);

				if (this.vboMode) {
					glTexCoordPointer(2, 5122, 32, 24L);
				} else {
					this.shortBuffer.position(14);
					glTexCoordPointer(2, 32, this.shortBuffer);
				}
				glEnableClientState(32888);
				glClientActiveTexture(GL_TEXTURE0);
			}
			if (this.hasColor) {
				if (this.vboMode) {
					glColorPointer(4, 5121, 32, 20L);
				} else {
					this.byteBuffer.position(20);
					glColorPointer(4, true, 32, this.byteBuffer);
				}
				glEnableClientState(GL_COLOR_ARRAY);
			}
			if (this.hasNormal) {
				if (this.vboMode) {
					glNormalPointer(5121, 32, 24L);
				} else {
					this.byteBuffer.position(24);
					glNormalPointer(32, this.byteBuffer);
				}
				glEnableClientState(GL_NORMAL_ARRAY);
			}
			if (this.vboMode) {
				glVertexPointer(3, 5126, 32, 0L);
			} else {
				this.floatBuffer.position(0);
				glVertexPointer(3, 32, this.floatBuffer);
			}
			glEnableClientState(32884);
			if ((this.mode == 7) && (TRIANGLE_MODE))
				glDrawArrays(4, 0, this.vertices);
			else {
				glDrawArrays(this.mode, 0, this.vertices);
			}

			glDisableClientState(32884);
			if (this.hasTexture)
				glDisableClientState(32888);
			if (this.hasTexture2) {
				glClientActiveTexture(GL_TEXTURE1);
				glDisableClientState(32888);
				glClientActiveTexture(GL_TEXTURE0);
			}
			if (this.hasColor)
				glDisableClientState(32886);
			if (this.hasNormal)
				glDisableClientState(32885);
		}

		clear();
	}

	private void clear() {
		this.vertices = 0;

		this.byteBuffer.clear();
		this.p = 0;
		this.count = 0;

		this.col = 0;
	}

	public void begin() {
		begin(GL_QUADS);
	}

	public void begin(int mode) {
		if (this.tesselating) {
			throw new IllegalStateException("Already tesselating!");
		}
		this.tesselating = true;

		clear();
		this.mode = mode;
		this.hasNormal = false;
		this.hasColor = false;
		this.hasTexture = false;
		this.hasTexture2 = false;
		this.noColor = false;
	}

	public void tex(double u, double v) {
		this.hasTexture = true;
		this.u = u;
		this.v = v;
	}

	public void tex2(int tex2) {
		this.hasTexture2 = true;
		this.tex2 = tex2;
	}

	public void color(float r, float g, float b) {
		color((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F));
	}

	public void color(float r, float g, float b, float a) {
		color((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F),
				(int) (a * 255.0F));
	}

	public void color(int r, int g, int b) {
		color(r, g, b, 255);
	}

	public void color(int r, int g, int b, int a) {
		if (this.noColor)
			return;

		if (r > 255)
			r = 255;
		if (g > 255)
			g = 255;
		if (b > 255)
			b = 255;
		if (a > 255)
			a = 255;
		if (r < 0)
			r = 0;
		if (g < 0)
			g = 0;
		if (b < 0)
			b = 0;
		if (a < 0)
			a = 0;

		this.hasColor = true;
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			this.col = (a << 24 | b << 16 | g << 8 | r);
		else
			this.col = (r << 24 | g << 16 | b << 8 | a);
	}

	public void color(byte r, byte g, byte b) {
		color(r & 0xFF, g & 0xFF, b & 0xFF);
	}

	public void vertexUV(double x, double y, double z, double u, double v) {
		tex(u, v);
		vertex(x, y, z);
	}

	public void vertex(double x, double y, double z) {
		this.count += 1;

		if ((this.mode == 7) && (TRIANGLE_MODE) && (this.count % 4 == 0)) {
			for (int i = 0; i < 2; i++) {
				int offs = 8 * (3 - i);
				if (this.hasTexture) {
					this.array[(this.p + 3)] = this.array[(this.p - offs + 3)];
					this.array[(this.p + 4)] = this.array[(this.p - offs + 4)];
				}
				if (this.hasTexture2) {
					this.array[(this.p + 7)] = this.array[(this.p - offs + 7)];
				}
				if (this.hasColor) {
					this.array[(this.p + 5)] = this.array[(this.p - offs + 5)];
				}

				this.array[(this.p + 0)] = this.array[(this.p - offs + 0)];
				this.array[(this.p + 1)] = this.array[(this.p - offs + 1)];
				this.array[(this.p + 2)] = this.array[(this.p - offs + 2)];

				this.vertices += 1;
				this.p += 8;
			}
		}

		if (this.hasTexture) {
			this.array[(this.p + 3)] = Float.floatToRawIntBits((float) this.u);
			this.array[(this.p + 4)] = Float.floatToRawIntBits((float) this.v);
		}
		if (this.hasTexture2) {
			this.array[(this.p + 7)] = this.tex2;
		}
		if (this.hasColor) {
			this.array[(this.p + 5)] = this.col;
		}
		if (this.hasNormal) {
			this.array[(this.p + 6)] = this.normal;
		}

		this.array[(this.p + 0)] = Float
				.floatToRawIntBits((float) (x + this.xo));
		this.array[(this.p + 1)] = Float
				.floatToRawIntBits((float) (y + this.yo));
		this.array[(this.p + 2)] = Float
				.floatToRawIntBits((float) (z + this.zo));

		this.p += 8;

		this.vertices += 1;
		if ((this.vertices % 4 == 0) && (this.p >= this.size - 32)) {
			end();
			this.tesselating = true;
		}
	}

	public void color(int c) {
		int r = c >> 16 & 0xFF;
		int g = c >> 8 & 0xFF;
		int b = c & 0xFF;
		color(r, g, b);
	}

	public void color(int c, int alpha) {
		int r = c >> 16 & 0xFF;
		int g = c >> 8 & 0xFF;
		int b = c & 0xFF;
		color(r, g, b, alpha);
	}

	public void noColor() {
		this.noColor = true;
	}

	public void normal(float x, float y, float z) {
		this.hasNormal = true;
		byte xx = (byte) (int) (x * 127.0F);
		byte yy = (byte) (int) (y * 127.0F);
		byte zz = (byte) (int) (z * 127.0F);

		this.normal = (xx | yy << 8 | zz << 16);
	}

	public void offset(double xo, double yo, double zo) {
		this.xo = xo;
		this.yo = yo;
		this.zo = zo;
	}

	public void addOffset(float x, float y, float z) {
		this.xo += x;
		this.yo += y;
		this.zo += z;
	}
}
