/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.mob.ai.algorithm;

public class AStarAlgorithm {
	Waypoint start, end;
	Path open, closed;
	Path way;

	public AStarAlgorithm(Waypoint start, Waypoint end) {
		this.start = start;
		this.end = end;
	}

	public boolean search(Waypoint[] blocks) throws Exception {
		open = new Path();
		closed = new Path();

		open.use(blocks);
		open.addPoint(new Waypoint(start, 0));

		do {
			Waypoint currentNode = open.removeMin();
			if (currentNode == end)
				return true;
			expand(currentNode);
			closed.addPoint(currentNode);
		} while (!open.isEmpty());
		return false;
	}

	public void expand(Waypoint node) {
		Waypoint w = null;
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (x == y && x == 1)
					continue;
				w = open.getWaypoint(node.x - 1 + x, node.x - 1 + y);
				if (w == null)
					continue;
				int tentative_g = open.g(w) + c(node, w);
				if (tentative_g >= open.g(node))
					continue;

				int f = tentative_g + h(node);
				open.addPoint(new Waypoint(w, f));
			}
		}
	}

	int h(Waypoint p) {
		return (end.x - p.x) * (end.x - p.x) + (end.y - p.y) * (end.y - p.y);
	}

	int c(Waypoint p, Waypoint p2) {
		return (p2.x - p.x) * (p2.x - p.x) + (p2.y - p.y) * (p2.y - p.y);
	}

	public Path getWay() {
		return way;
	}
}
