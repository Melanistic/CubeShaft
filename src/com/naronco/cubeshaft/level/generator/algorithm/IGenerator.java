package com.naronco.cubeshaft.level.generator.algorithm;

import com.naronco.cubeshaft.level.generator.Biome;

public interface IGenerator {
	float Generate(int x, int y, Biome biome);
}
