/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.gui;

public class Button {
	public int x;
	public int y;
	public int width;
	public int height;
	public String text;
	public int id;
	public boolean isClickable = true;

	public Button(int id, int x, int y, int width, int height, String text) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
	}
}
