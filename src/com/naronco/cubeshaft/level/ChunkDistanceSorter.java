/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level;

import java.io.*;
import java.util.*;

import com.naronco.cubeshaft.player.Player;

public class ChunkDistanceSorter implements Comparator<Chunk>, Serializable {
	private static final long serialVersionUID = 1L;
	private Player player;

	public ChunkDistanceSorter(Player player) {
		this.player = player;
	}

	public int compare(Chunk chunk, Chunk other) {
		if (chunk.isVisible || !other.isVisible) {
			if (other.isVisible) {
				float sqDist = chunk.distToPlayer(this.player);
				float otherSqDist = other.distToPlayer(this.player);

				if (sqDist == otherSqDist) {
					return 0;
				} else if (sqDist > otherSqDist) {
					return -1;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		} else {
			return -1;
		}
	}
}