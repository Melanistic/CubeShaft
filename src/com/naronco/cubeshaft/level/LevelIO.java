/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.melanistics.DataSave;
import com.naronco.cubeshaft.*;
import com.naronco.cubeshaft.player.Player;

public class LevelIO {
	private static final int VERSION = 1;
	private Cubeshaft game;

	public LevelIO(Cubeshaft game) {
		this.game = game;
	}

	public void save3(String levelName, Level level, Player player) {
		try {
			File path = new File("world");
			path.mkdir();

			if (!path.exists()) {
				DataOutputStream dos = new DataOutputStream(
						new FileOutputStream(path));
				dos.close();
			}

			DataOutputStream dos = new DataOutputStream(new FileOutputStream(
					new File("world/" + levelName + ".csworld")));
			dos.writeShort(VERSION);

			dos.writeShort(level.width);
			dos.writeShort(level.height);
			dos.writeShort(level.depth);

			dos.writeFloat(player.x);
			dos.writeFloat(player.y);
			dos.writeFloat(player.z);
			dos.writeFloat(player.xRot);
			dos.writeFloat(player.yRot);

			game.setProgressTitle("Saving level..");
			game.setProgressText("");
			for (int i = 0; i < level.tiles.length; i++) {
				byte tile = level.tiles[i];
				dos.writeByte(tile);
			}

			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void load3(String levelName, Level level, Player player) {
		try {
			File path = new File("world/" + levelName + ".csworld");
			if (!path.exists()) {
				System.err.println(levelName + " world doesnt exist!");
				return;
			}

			DataInputStream dis = new DataInputStream(new FileInputStream(
					new File("world/" + levelName + ".csworld")));
			short version = dis.readShort();
			if (version != LevelIO.VERSION) {
				System.err.println("Version outdated!");
				dis.close();
				return;
			}

			int width = dis.readShort();
			int height = dis.readShort();
			int depth = dis.readShort();
			float x = dis.readFloat();
			float y = dis.readFloat();
			float z = dis.readFloat();
			float xRot = dis.readFloat();
			float yRot = dis.readFloat();

			player.setPos(x, y, z);
			player.xRot = xRot;
			player.yRot = yRot;

			int levelSize = width * height * depth;
			byte[] tiles = new byte[levelSize];

			game.setProgressTitle("Loading level..");
			game.setProgressText("");
			for (int i = 0; i < levelSize; i++) {
				tiles[i] = dis.readByte();
			}

			level.init(width, height, depth, tiles);
			dis.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public boolean exists(String levelName) {
		File path = new File("world/" + levelName + ".csworld");
		return path.exists();
	}

	public void save(String levelName, Level level, Player player) {
		System.out.println(levelName);
		DataSave s = new DataSave("World");
		s.setInt("w", level.width);
		s.setInt("h", level.height);
		s.setInt("d", level.depth);

		s.setFloat("x", player.x);
		s.setFloat("y", player.y);
		s.setFloat("z", player.z);
		s.setFloat("xr", player.xRot);
		s.setFloat("yr", player.yRot);
		
		//DataSave map = new DataSave("Map");
		System.out.println(level.tiles.length);
		
		char[] b = new char[level.tiles.length*2];
		
		for(int i=0; i<level.tiles.length;i++)
		{
			String s2 =  Integer.toHexString(level.tiles[i]);
			s2 = s2.length()<=1 ? "0"+s2 : s2;
			b[i] = s2.charAt(0);
			b[i+level.tiles.length] = s2.charAt(1);
		}
		//s.setData("Map", map);
		s.setString("map", String.valueOf(b));
		//s.setString("Map", Arrays.toString(level.tiles));
		clearLevel(level);
		level = null;
		System.gc();
		try {

			File path = new File("world");
			path.mkdir();

			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(
					new File(path, levelName + ".csworld")));
			DataSave.xmlIO.writeIO(out, s);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Can't save World!");
		}
	}

	public void load(String levelName, Level level, Player player) {
		try {
			GZIPInputStream in = new GZIPInputStream(
					new FileInputStream(
					new File("world", levelName + ".csworld")));
			DataSave s = DataSave.xmlIO.readIO(in)[0];

			float x = s.getFloat("x");
			float y = s.getFloat("y");
			float z = s.getFloat("z");
			float xRot = s.getFloat("xr");
			float yRot = s.getFloat("yr");

			player.setPos(x, y, z);
			player.xRot = xRot;
			player.yRot = yRot;

			int width = s.getInt("w");
			int height = s.getInt("h");
			int depth = s.getInt("d");

			//game.setProgressTitle("Loading level..");
			//game.setProgressText("");

			int levelSize = width * height * depth;
			byte[] tiles = new byte[levelSize];
			//DataSave map = s.getData("Map");
			String ma = s.getString("map");
			for(int i=0;i<tiles.length;i++)
			{
				tiles[i] = (byte)(int)(Integer.decode("0x" + ma.charAt(i) + ma.charAt(i + tiles.length) ));//ma.charAt(i);
			}
			s.clear();
			s = null;
			System.gc();
			
			level.init(width, height, depth, tiles);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void clearLevel(Level l)
	{
		l.entities.clear();
		l.players.clear();
		l.heightMap = null;
		l.tiles = null;
	}
}
