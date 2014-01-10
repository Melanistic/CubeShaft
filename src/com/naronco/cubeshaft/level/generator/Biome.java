package com.naronco.cubeshaft.level.generator;

public enum Biome {
	Plains, Forest, Water, Desert, None;
	
	public static float getMin(Biome b)
	{
		if(b == Plains)
		{
			return 55 / 255f;
		} else if(b == Forest)
		{
			return 110 / 255f;
		} else if(b == Water)
		{
			return 20 / 255f;
		} else if(b == Desert)
		{
			return 64 / 255f;
		} else return 0;
	}
	
	public static float getMax(Biome b)
	{
		if(b == Plains)
		{
			return 120 / 255f;
		} else if(b == Forest)
		{
			return 230 / 255f;
		} else if(b == Water)
		{
			return 55 / 255f;
		} else if(b == Desert)
		{
			return 90 / 255f;
		} else return 1;
	}
	
	public static Biome getBiome(float height)
	{
		if (height > 0.7f)
		{
			return Forest;
		}
		else if (height > 0.6f)
		{
			return Plains;
		}
		else if (height > 0.3f)
		{
			return Water;
		}
		else if (height > 0.25f)
		{
			return Plains;
		}
		else
		{
			return Desert;
		}
	}
}
