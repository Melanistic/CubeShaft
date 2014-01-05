/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.mob.ai.algorithm;

import java.util.Arrays;
import java.util.Vector;

public class Path {
	public Vector<Waypoint> path;
	int step;

	public Path() {
		path = new Vector<Waypoint>();
		step = 0;
	}

	public Waypoint getCurrentPoint() throws Exception {
		if (path.get(step) != null)
			return path.get(step);
		throw new Exception("No Waypoint point" + step + " found! ("
				+ path.size() + " Entries found)");
	}

	public void addPoint(Waypoint p) {
		path.add(p);
	}

	public boolean isEmpty() {
		return path.isEmpty();
	}

	public void use(Waypoint[] waypoints) {
		path.clear();
		path.addAll(Arrays.asList(waypoints));
	}

	public Waypoint removeMin() throws Exception {
		int removePlace = -1;
		int removeF = 100000;
		for (int i = 0; i < path.size(); i++) {
			if (path.get(i) != null) {
				if (path.get(i).f < removeF) {
					removeF = path.get(i).f;
					removePlace = i;
				}
			}
		}
		if (removePlace != -1) {
			Waypoint removed = path.get(removePlace);
			path.remove(removePlace);
			return removed;
		}
		throw new Exception("Path = NULL");
	}

	public int g(Waypoint w) {
		return path.get(path.indexOf(w)).g;
	}

	public Waypoint getWaypoint(int x, int y) {
		for (Waypoint w : path) {
			if (w.x == x && w.y == y) {
				return w;
			}
		}
		return null;
	}
}
