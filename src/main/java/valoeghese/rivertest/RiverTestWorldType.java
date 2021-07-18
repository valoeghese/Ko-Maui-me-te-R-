package valoeghese.rivertest;

import net.minecraft.client.world.WorldType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class RiverTestWorldType extends WorldType {
	public RiverTestWorldType(String string) {
		super(string);
	}

	@Override
	public ChunkGenerator getChunkGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
		return new RiverTestChunkGenerator(new FixedBiomeSource(biomeRegistry.get(BiomeKeys.PLAINS)), seed, chunkGeneratorSettingsRegistry.get(RiverTestChunkGenerator.ID).getStructuresConfig());
	}
}
