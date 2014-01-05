/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level;

import java.util.*;

import com.naronco.cubeshaft.player.Player;

public class ChunkSorter implements Comparator<Chunk> {
	private Player player;

	public ChunkSorter(Player player) {
		this.player = player;
	}

	public int compare(Chunk c0, Chunk c1) {
		float dist1 = c0.distToPlayer(player);
		float dist2 = c1.distToPlayer(player);
		if (dist1 == dist2)
			return 0;
		else if (dist1 > dist2)
			return 1;
		else
			return -1;
	}
}
