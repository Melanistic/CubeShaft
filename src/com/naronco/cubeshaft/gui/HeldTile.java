/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.gui;

import com.naronco.cubeshaft.level.tile.*;

public class HeldTile {
	public Tile tile;
	public float heldPos;
	public float lastPos;
	public boolean moving = false;
	public float heldOffset;
}
