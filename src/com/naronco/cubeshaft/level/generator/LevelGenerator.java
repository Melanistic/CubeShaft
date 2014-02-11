/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 *
 * According to Main Coder Jojo, this project is a Team Melanistic project. 
 * Please remove Naronco copyright so we can add Melanistic Copyright.
 */

package com.naronco.cubeshaft.level.generator;

import java.util.Random;

import com.naronco.cubeshaft.Cubeshaft;
import com.naronco.cubeshaft.gui.ProgressMenu;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.level.generator.algorithm.IGenerator;
import com.naronco.cubeshaft.level.generator.algorithm.SimplexGenerator;
import com.naronco.cubeshaft.level.generator.struct.TreeLevelStruct;
import com.naronco.cubeshaft.level.tile.Tile;

public class LevelGenerator {
	private Cubeshaft game;
	private int width;
	private int height;
	private int depth;
	private Random random = new Random();

	public LevelGenerator(Cubeshaft game) {
		this.game = game;
	}
	
	public int getHeight(int[] heightmap, int x, int y)
	{
		if(x < 0) x = 0;
		if(x >= width) x = width - 1;
		if(y < 0) y = 0;
		if(y >= depth) y = depth - 1;
		return heightmap[x + y * depth];
	}

	public boolean generate(String name, Level level, int w, int h, int d) {
		this.game.setProgressTitle("Generating level");
		level.init(name, w, h, d, new byte[w * h * d]);

		this.width = w;
		this.height = h;
		this.depth = d;

		setProgressText("Raising..");

		/*
		 * NoiseMap noise1 = new NoiseMap(width, depth, 32); NoiseMap noise2 =
		 * new NoiseMap(width, depth, 32); CombinedNoiseMap noiseMap = new
		 * CombinedNoiseMap(noise1, noise2);
		 * 
		 * int[] heightMap = new int[width * depth]; for (int x = 0; x < width;
		 * x++) { setProgress(x * 100 / (width - 1)); for (int z = 0; z < depth;
		 * z++) { heightMap[x + z * width] = (int) ((noiseMap.values[x + z
		 * width]) 2 + height / 2 - 2); } }
		 */

		IGenerator biomeGenerator = new SimplexGenerator(random, 16, 0.5f,
				0.005f);
		Biome[] biomes = new Biome[width * depth];
		for (int x = 0; x < width; x++) {
			setProgress(x * 50 / (width - 1));
			for (int z = 0; z < depth; z++) {
				biomes[x + z * depth] = Biome.getBiome(biomeGenerator.Generate(
						x, z, Biome.None));
			}
		}

		IGenerator heightmapGenerator = new SimplexGenerator(random, 32, 0.25f,
				0.015f);
		int[] heightMap = new int[width * depth];

		
		for (int x = 0; x < width; x++) {
			setProgress(x * 50 / (width - 1) + 50);
			for (int z = 0; z < depth; z++) {
				heightMap[x + z * depth] = (int) (heightmapGenerator.Generate(x, z, biomes[x + z * depth]) * height);
			}
		}
		
		setProgressText("Smoothing..");
		double[] gausMatrix = { 
			0.00296901674395065, 0.013306209891014005, 0.02193823127971504, 0.013306209891014005, 0.00296901674395065, 
			0.013306209891014005, 0.05963429543618023, 0.09832033134884507, 0.05963429543618023, 0.013306209891014005, 
			0.02193823127971504, 0.09832033134884507, 0.16210282163712417, 0.09832033134884507, 0.02193823127971504, 
			0.013306209891014005, 0.05963429543618023, 0.09832033134884507, 0.05963429543618023, 0.013306209891014005, 
			0.00296901674395065, 0.013306209891014005, 0.02193823127971504, 0.013306209891014005, 0.00296901674395065 
		};
		int gausDepth = 5;
		int iters = 4;
		for(int i = 0; i < iters; i++)
		{
			for (int x = 0; x < width; x++) {
				setProgress(x * (100 / iters) / (width - 1) + 100 / iters * i);
				for (int y = 0; y < depth; y++) {
					double total = 0;
					for(int gx = 0; gx < gausDepth; gx++)
					{
						for(int gy = 0; gy < gausDepth; gy++)
						{
							total += getHeight(heightMap, x - 2 + gx, y - 2 + gy) * gausMatrix[gx + gy * gausDepth];
						}
					}
					heightMap[x + y * depth] = (int)(total);
				}
			}
		}
		for (int x = 0; x < width; x++) {
			setProgress(x * 100 / (width - 1));
			for (int z = 0; z < depth; z++) {
				int total = 0;
				for(int gx = 0; gx < 7; gx++)
				{
					for(int gy = 0; gy < 7; gy++)
					{
						total += getHeight(heightMap, x - 3 + gx, z - 3 + gy);
					}
				}
				heightMap[x + z * depth] = (int)(total / 49.0f);
			}
		}

		setProgressText("Erosing..");
		
		for (int x = 0; x < width; x++) {
			setProgress(x * 100 / (width - 1));
			for (int y = 0; y < depth; y++) {
				heightMap[x + y * depth] = heightMap[x + y * depth] - 2 + random.nextInt(5);
			}
		}
		setProgressText("Smoothing..");
		for (int x = 0; x < width; x++) {
			setProgress(x * 100 / (width - 1));
			for (int y = 0; y < depth; y++) {
				double total = 0;
				for(int gx = 0; gx < gausDepth; gx++)
				{
					for(int gy = 0; gy < gausDepth; gy++)
					{
						total += getHeight(heightMap, x - 2 + gx, y - 2 + gy) * gausMatrix[gx + gy * gausDepth];
					}
				}
				heightMap[x + y * depth] = (int)(total);
			}
		}

		setProgressText("Soiling..");
		for (int x = 0; x < width; x++) {
			setProgress(x * 100 / (width - 1));
			for (int z = 0; z < depth; z++)
				for (int y = 0; y < height; y++) {
					Biome biome = biomes[x + z * width];
					int hei = heightMap[x + z * width];

					int tile = 0;
					if (biome == Biome.Desert) {
						if (y < hei) tile = Tile.sand.id;
					} else if (biome == Biome.Water) {
						if(y < hei) tile = Tile.stone.id;
					} else {
						if (y == hei && y != height / 2 - 3)
							tile = Tile.grass.id;
						else if (y == hei && y == height / 2 - 3)
							tile = Tile.sand.id;
						else if (y >= hei && y < hei)
							tile = Tile.dirt.id;
						else if (y < hei && y >= hei - 5)
							tile = random.nextInt(2) == 0 ? Tile.dirt.id
									: Tile.stone.id;
						else if (y < hei - 5)
							tile = Tile.stone.id;
					}
					level.setTileNoUpdate(x, y, z, tile);
				}
		}

		setProgressText("Watering..");
		for (int x = 0; x < width; x++) {
			this.setProgress(x * 100 / (width - 1));
			for (int z = 0; z < depth; z++)
				for (int y = 0; y < height / 4; y++) {
					int tile = level.getTile(x, y, z);
					if (tile == 0)
						if ((level.getTile(x, y - 1, z - 1) == 0)
								|| (level.getTile(x, y - 1, z + 1) == 0)
								|| (level.getTile(x - 1, y - 1, z) == 0)
								|| (level.getTile(x + 1, y - 1, z) == 0))
							level.setTileNoUpdate(x, y, z, Tile.flowingWater.id);
						else
							level.setTileNoUpdate(x, y, z, Tile.water.id);
					else if (tile == Tile.grass.id && y < height / 2 - 3)
						level.setTileNoUpdate(x, y, z, Tile.dirt.id);
				}
		}
		
		setProgressText("Carving..");
		int count = width * height * depth / 256 / 64 / 16;
		for (int i = 0; i < count; i++) {
			setProgress(i * 100 / (count - 1));
			double xx = (int) (random.nextFloat() * width);
			double yy = (int) (random.nextFloat() * (height / 4 * 3));
			double zz = (int) (random.nextFloat() * depth);

			float a = (float) (Math.random() * 20.0f);

			int length = random.nextInt(400) + 200;
			for (int j = 0; j < length; j++) {
				if (j % 3 == 0) {
					a += (float) Math.random() * 45.0f - 22.0f;
					xx += Math.sin(Math.toRadians(a));
					zz += Math.cos(Math.toRadians(a));
				}

				int xc = (int) xx;
				int yc = (int) yy;
				int zc = (int) zz;

				int r = 4;
				for (int x = xc - r; x <= xc + r; x++)
					for (int y = yc - r; y <= yc + r; y++)
						for (int z = zc - r; z <= zc + r; z++)
							if (x >= 0 && y >= 0 && z >= 0 && x < width
									&& y < height && z < depth) {
								int tile = level.getTile(x, y, z);
								int xd = x - xc;
								int yd = y - yc;
								int zd = z - zc;

								int dist = xd * xd + yd * yd + zd * zd;
								if (dist < r * r
										&& tile != 0
										&& Tile.tiles[tile].getLiquidType() == 0)
									level.setTileNoUpdate(x, y, z, 0);
							}
			}
		}

		
		 setProgressText("Planting.."); 
		 int treeCount = width *depth / 256;
		 for (int i = 0; i < treeCount; i++) 
		 { 
			 setProgress(i * 100 / (treeCount - 1) / 2);
			 int x = random.nextInt(width); 
			 int z = random.nextInt(depth); 
			 int y = heightMap[x + z * width] + 1; 
			 new TreeLevelStruct().generate(level, x, y, z, random);
		 }
		 for (int y = 0; y < height; y++) 
		 { 
			 setProgress(50 + y * 50 / (height - 1)); 
			 for (int x = 0; x < width; x++) 
				 for (int z = 0; z < depth; z++) 
				 { 
					 int tile = level.getTile(x, y, z); 
					 if (tile == Tile.grass.id) 
					 { 
						 int upperTile = level.getTile(x, y + 1, z); 
						 if (upperTile == 0 && random.nextInt(5) == 0) 
							 level.setTileNoUpdate(x, y + 1, z, Tile.tallGrass.id);
					 }
				 }
		 } 
		 int bambooCount = width * depth / 256;
		 /*
		 Random r = new Random();
		 for(int i = 0; i < bambooCount; i++) 
		 {
			 setProgress(i * 100 / (bambooCount - 1)); 
			 int x = random.nextInt(width);
			 int z = random.nextInt(depth); 
			 int y = heightMap[x + z * width] + 1;
			 if(level.getTile(x, y - 1, z) == Tile.sand.id) 
			 {
				 if ((level.getTile(x, y - 1, z - 1) == Tile.water.id) || (level.getTile(x, y - 1, z + 1) == Tile.water.id) || (level.getTile(x - 1, y - 1, z) == Tile.water.id) || (level.getTile(x + 1, y - 1, z) == Tile.water.id)) 
				 {
					 float temp =  r.nextFloat() * 4; 
					 if (temp >= 0)
						 level.setTileNoUpdate(x, y, z, Tile.bamboo.id); 
					 if (temp >= 1)
						 level.setTileNoUpdate(x, y + 1, z, Tile.bamboo.id); 
					 if (temp >= 2)
						 level.setTileNoUpdate(x, y + 2, z, Tile.bamboo.id); 
					 if (temp >= 3)
						 level.setTileNoUpdate(x, y + 3, z, Tile.bamboo.id);
					 setProgress(i * 100 / (bambooCount - 1)); 
				 }
				 else
					 i--;
			 } 
			 else 
				 i--; 
		 }
		 */

		//  overlay(flyingIslands);
		//  overlay(hell);

		level.calcLightDepths(0, 0, width, depth);
		for (int i = 0; i < level.levelRenderers.size(); i++) {
			level.levelRenderers.get(i).init();
		}

		game.endLoading();
		return true;
	}
	
	public void generateFlatmap(String name, Level level, int w, int h, int d)
	{
		game.setProgressTitle("Generating Flatmap");
		game.setProgressText("Generating...");
		
		Tile[] tiles = new Tile[]{Tile.bedrock,Tile.stone,Tile.stone,Tile.stone,Tile.dirt,Tile.dirt,Tile.grass};
		
		byte[] b = new byte[w*h*d];	
		for(int j = 1;j<w;j++)
		{
			game.setProgress((int) ((j/(float)w) * 100.0));
			for(int l = 1;l<d;l++)
			{
				for(int k=0;k<tiles.length;k++)
				{
				////b[(x + l) * z + j] = (byte) Tile.stone.id;
					int i = (k * w + l) * d + j;
					if(i>=b.length||i<0)continue;
					b[i] = (byte) tiles[k].id;
				}
			}
				
		}
		level.init(name, w, h, d, b);
	}
	
	private void setProgressText(String text) {
		ProgressMenu menu=(ProgressMenu)game.menu;
		menu.setProgressText(text);
	}

	private void setProgress(int progress) {
		ProgressMenu menu=(ProgressMenu)game.menu;
		menu.setProgress(progress);
	}
}
