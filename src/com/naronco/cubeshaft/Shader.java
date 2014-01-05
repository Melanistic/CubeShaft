/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft;

import static org.lwjgl.opengl.ARBFragmentShader.*;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBVertexShader.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Shader {
	private boolean useShader = false;
	private int program;
	private int vertShader, fragShader;

	public Shader(String name) {
		try {
			vertShader = createShader("/shader/" + name + ".vert",
					GL_VERTEX_SHADER_ARB);
			fragShader = createShader("/shader/" + name + ".frag",
					GL_FRAGMENT_SHADER_ARB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		program = glCreateProgramObjectARB();
		if (program == 0)
			return;

		glAttachObjectARB(program, vertShader);
		glAttachObjectARB(program, fragShader);

		glLinkProgramARB(program);
		if (glGetObjectParameteriARB(program, GL_OBJECT_LINK_STATUS_ARB) == GL_FALSE) {
			System.err.println(getInfoLog(program));
			return;
		}

		glValidateProgramARB(program);
		if (glGetObjectParameteriARB(program, GL_OBJECT_VALIDATE_STATUS_ARB) == GL_FALSE) {
			System.err.println(getInfoLog(program));
			return;
		}

		useShader = true;
	}

	public int createShader(String filename, int shaderType) throws Exception {
		int shader = 0;
		try {
			shader = glCreateShaderObjectARB(shaderType);

			if (shader == 0)
				return 0;

			glShaderSourceARB(shader, readFile(filename));
			glCompileShaderARB(shader);

			if (glGetObjectParameteriARB(shader, GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE)
				throw new RuntimeException("Error creating shader: "
						+ getInfoLog(shader));

			return shader;
		} catch (Exception exc) {
			glDeleteObjectARB(shader);
			throw exc;
		}
	}

	private static String getInfoLog(int obj) {
		return glGetInfoLogARB(obj,
				glGetObjectParameteriARB(obj, GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

	private String readFile(String filename) throws Exception {
		StringBuilder source = new StringBuilder();
		BufferedReader reader;

		Exception exception = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					Shader.class.getResourceAsStream(filename)));
			try {
				String line;
				while ((line = reader.readLine()) != null)
					source.append(line).append('\n');
			} catch (Exception e) {
				exception = e;
			} finally {
				try {
					reader.close();
				} catch (Exception e) {
					if (exception == null)
						exception = e;
					else
						e.printStackTrace();
				}
			}

			if (exception != null)
				throw exception;

		} catch (Exception e) {
			exception = e;
		}
		return source.toString();
	}

	public void enable() {
		if (!useShader)
			return;
		glUseProgramObjectARB(program);
	}

	public void disable() {
		if (!useShader)
			return;
		glUseProgramObjectARB(0);
	}

	public void bind(String name, int val) {
		int loc = glGetUniformLocationARB(program, name);
		glUniform1iARB(loc, val);
	}

	public void bind(String name, float val) {
		int loc = glGetUniformLocationARB(program, name);
		glUniform1fARB(loc, val);
	}
}
