/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.naronco.cubeshaft.Cubeshaft;
import com.naronco.cubeshaft.Entity;
import com.naronco.cubeshaft.level.generator.struct.TreeLevelStruct;
import com.naronco.cubeshaft.level.tile.Tile;
import com.naronco.cubeshaft.mob.Mob;
import com.naronco.cubeshaft.mob.MobSkeleton;
import com.naronco.cubeshaft.mob.ai.Waypoint;
import com.naronco.cubeshaft.phys.AABB;
import com.naronco.cubeshaft.player.Player;

public class Level {
	public int width, depth, height;
	public byte[] tiles;
	public int[] heightMap;

	public List<LevelRenderer> levelRenderers = new ArrayList<LevelRenderer>();

	public Random random = new Random();
	public List<Entity> entities = new ArrayList<Entity>();
	public List<Player> players = new ArrayList<Player>();

	public int skyColor = -1;
	public Sun sun = new Sun(8.0f);
	public SkyColor sky = new SkyColor();
	public int time;

	public void init(int width, int height, int depth, byte[] tiles) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.tiles = tiles;
		heightMap = new int[width * depth];
		calcLightDepths(0, 0, width, depth);
		for (int i = 0; i < levelRenderers.size(); i++) {
			levelRenderers.get(i).init();
		}
	}

	public void calcLightDepths(int x, int z, int w, int d) {
		for (int xx = x; xx < x + w; xx++)
			for (int zz = z; zz < z + d; zz++) {
				int blocker = 0;
				for (int yy = this.height - 1; yy > 0; yy--) {
					blocker = yy;
					Tile tile = Tile.tiles[getTile(xx, yy, zz)];
					if (tile == null ? false : tile.isNormalRender()) {
						break;
					}
				}
				heightMap[xx + zz * width] = blocker + 1;
			}
	}

	public List<AABB> getCubes(Entity source, AABB aabb) {
		List<AABB> result = new ArrayList<AABB>();

		int x0 = (int) Math.floor(aabb.x0);
		int x1 = (int) Math.floor(aabb.x1 + 1);
		int y0 = (int) Math.floor(aabb.y0);
		int y1 = (int) Math.floor(aabb.y1 + 1);
		int z0 = (int) Math.floor(aabb.z0);
		int z1 = (int) Math.floor(aabb.z1 + 1);

		for (int x = x0; x < x1; x++)
			for (int y = y0; y < y1; y++)
				for (int z = z0; z < z1; z++)
					if (x >= 0 && y >= 0 && z >= 0 && x < this.width
							&& z < this.depth && y < this.height) {
						Tile tile = Tile.tiles[this.getTile(x, y, z)];
						if (tile != null) {
							AABB bb = tile.getAABB(x, y, z);
							if (bb != null && bb.intersects(aabb)) {
								result.add(bb);
							}
						}
					}

		return result;
	}

	public List<Entity> getEntitysExcludingEntity(AABB par1, Entity par2) 
	{
		return getEntitysExcludingEntity(par1, par2, new IEntitySelector() 
		{
			@Override
			public boolean isValidEntity(Entity e) 
			{
				return true;
			}
		});
	}

	public List<Entity> getEntitysExcludingEntity(AABB par1, Entity par2, IEntitySelector par3) {
		List<Entity> result = new ArrayList<>();
		
		synchronized (entities) 
		{
			for (Entity e : entities) {
				AABB tmpBB = e.aabb.copie();
				if (par1.intersects(tmpBB) && par3.isValidEntity(e))
					result.add(e);
			}
		}
		synchronized (players) 
		{
			for (Entity e : players) {
				AABB tmpBB = e.aabb.copie();
				if (par1.intersects(tmpBB) && par3.isValidEntity(e))
					result.add(e);
			}
		}
		result.remove(par2);
		return result;
	}

	public boolean setTile(int x, int y, int z, int id) {
		if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= depth)
			return false;
		if (id == tiles[(y * width + z) * depth + x])
			return false;
		tiles[(y * width + z) * depth + x] = (byte) id;
		neighborChange(x - 1, y, z, id);
		neighborChange(x + 1, y, z, id);
		neighborChange(x, y - 1, z, id);
		neighborChange(x, y + 1, z, id);
		neighborChange(x, y, z - 1, id);
		neighborChange(x, y, z + 1, id);

		tileAdd(x, y, z, id);

		calcLightDepths(x, z, 1, 1);
		for (int i = 0; i < this.levelRenderers.size(); i++) {
			levelRenderers.get(i).queueChunks(x - 1, y - 1, z - 1, x + 1,
					y + 1, z + 1);
		}
		return true;
	}

	public boolean setTileNoUpdate(int x, int y, int z, int id) {
		if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= depth)
			return false;
		if (id == tiles[(y * width + z) * depth + x])
			return false;
		tiles[(y * width + z) * depth + x] = (byte) id;
		return true;
	}

	private void neighborChange(int x, int y, int z, int id) {
		if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= depth)
			return;
		Tile tile = Tile.tiles[tiles[(y * width + z) * depth + x]];
		if (tile != null) {
			tile.neighborChange(this, x, y, z, id);
		}
	}

	private void tileAdd(int x, int y, int z, int id) {
		if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= depth)
			return;
		Tile tile = Tile.tiles[id];
		if (tile != null) {
			tile.tileAdd(this, x, y, z);
		}
	}

	public int getTile(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= depth)
			return 0;
		return tiles[(y * width + z) * depth + x] & 0xff;
	}
	
	public int getHeigh(int x, int z) 
	{
		int y = 0;
		int id = getTile(x, y, z);
		while(id!=0)
		{		
			y++;
		}
		return y;		
	}
	
	public boolean isDaytime()
	{
		return time>0 && time <1800;
	}
	
	public void addEntity(Entity e) {
		synchronized (entities) {
			entities.add(e);
		}
	}

	public boolean maybeGrowTree(int x, int y, int z) {
		int lowerTile = getTile(x, y - 1, z);
		if (lowerTile != Tile.grass.id && lowerTile != Tile.dirt.id)
			return false;
		for (int yc = y + 1; yc <= y + 10; yc++)
			if (getTile(x, yc, z) != 0)
				return false;
		for (int yc = y; yc <= y + 10; yc++)
			if (getTile(x, yc, z) == Tile.water.id
					|| getTile(x, yc, z) == Tile.flowingWater.id)
				return false;
		for (int xc = x - 2; xc <= x + 2; xc++)
			for (int yc = y + 3; yc <= y + 10; yc++)
				for (int zc = z - 2; zc <= z + 2; zc++) {
					int tile = getTile(xc, yc, zc);
					if (tile != 0)
						return false;
				}
		return true;
	}

	public void growTree(int x, int y, int z) {
		TreeLevelStruct tree = new TreeLevelStruct();
		tree.generate(this, x, y, z, random);
	}

	public boolean containsLiquid(AABB aabb, int liquidType) {
		int x0 = (int) Math.floor(aabb.x0);
		int x1 = (int) Math.floor(aabb.x1 + 1);
		int y0 = (int) Math.floor(aabb.y0);
		int y1 = (int) Math.floor(aabb.y1 + 1);
		int z0 = (int) Math.floor(aabb.z0);
		int z1 = (int) Math.floor(aabb.z1 + 1);

		for (int x = x0; x < x1; x++)
			for (int y = y0; y < y1; y++)
				for (int z = z0; z < z1; z++)
					if (x >= 0 && y >= 0 && z >= 0 && x < this.width
							&& z < this.depth && y < this.height) {
						Tile tile = Tile.tiles[this.getTile(x, y, z)];
						if (tile != null && tile.getLiquidType() == liquidType)
							return true;
					}

		return false;
	}

	public void tick() 
	{
		time = (time + 2) % 3600;
		// time=900;
		skyColor = sky.getSkyColor(time);
		
		List<Entity> remove = new ArrayList<Entity>();
		for (Entity e : entities) {
			e.tick();
			if (e.y < -50) e.removed = true;
			if (e.removed)
				remove.add(e);
		}
		entities.removeAll(remove);
		
		if(!isDaytime() && random.nextInt(20)==0);
		{
			Player p = Cubeshaft.game.player;
			List l = getEntitysExcludingEntity(p.aabb.copie().grow(64, 64, 64), p, new IEntitySelector() 
			{	
				@Override
				public boolean isValidEntity(Entity e) 
				{
					return e instanceof Mob;
				}
			});
			if(l.size() < 3)
			{
				spawnMobs();
			}
		}
	}
	
	private void spawnMobs()
	{
		Player p = Cubeshaft.game.player;
		float x = (random.nextInt(32)-random.nextInt(32));
		float z = (random.nextInt(32)-random.nextInt(32));
		
		if(Math.sqrt(x*x+z*z)>16)
		{	
		int y = getHeigh((int)x, (int)z);
		
		MobSkeleton sk = new MobSkeleton(this);
		sk.setPos(p.x + x, y, p.z + z);
		
		addEntity(sk);
		}
	}
	private void DespawnMobs()
	{
		for(Entity e : entities)
		{
			if(e instanceof Mob)
			{
				if(new Waypoint( Cubeshaft.game.player).getDistansTo(new Waypoint(e))>80)
				{
					e.removed = true;
				}
			}
		}
	}
	
}
