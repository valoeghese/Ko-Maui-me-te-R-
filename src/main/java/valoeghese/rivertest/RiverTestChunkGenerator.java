
package valoeghese.rivertest;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class RiverTestChunkGenerator extends ChunkGenerator {
	public static final Identifier ID = new Identifier("metera:river_test");

	public static final Codec<RiverTestChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
	instance.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(chunkGenerator -> chunkGenerator.populationSource),
			Codec.LONG.fieldOf("seed").stable().forGetter(chunkGenerator -> chunkGenerator.seed),
			StructuresConfig.CODEC.fieldOf("structures").forGetter(RiverTestChunkGenerator::getStructuresConfig))
	.apply(instance, RiverTestChunkGenerator::new));

	public RiverTestChunkGenerator(BiomeSource biomeSource, long seed, StructuresConfig structuresConfig) {
		super(biomeSource, structuresConfig);
		this.seed = seed;
		this.noise = new OpenSimplexGenerator(new Random(seed));
	}

	private final long seed;
	private final OpenSimplexGenerator noise;

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new RiverTestChunkGenerator(biomeSource.withSeed(seed), seed, getStructuresConfig());
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int chunkX = chunkPos.x;
		int chunkZ = chunkPos.z;
		ChunkRandom rand = new ChunkRandom();
		rand.setTerrainSeed(chunkX, chunkZ);

		int startX = chunkPos.getStartX();
		int startZ = chunkPos.getStartZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for(int xo = 0; xo < 16; ++xo) {
			for(int zo = 0; zo < 16; ++zo) {
				int x = startX + xo;
				int z = startZ + zo;
				int height = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, xo, zo) + 1;
				double noise = 0;
				int minSurfaceLevel = 63;
				region.getBiome(mutable.set(startX + xo, height, startZ + zo)).buildSurface(rand, chunk, x, z, height, noise, STONE, WATER, this.getSeaLevel(), minSurfaceLevel, region.getSeed());
			}
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
		// TODO Auto-generated method stub
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world) {
		int gridX = x / 1250;
		int gridZ = z / 1250;

		// four flags for terrain gen
		double mountainousness = 0.0;
		double plainsness = 0.0;
		double hillsness = 0.0;
		double oceanness = 0.0;

		// random large number
		double md = 1000.0;
		double pd = 1000.0;
		double hd = 1000.0;
		double od = 1000.0;

		for (int gxo = -1; gxo <= 1; ++gxo) {
			for (int gzo = -1; gzo <= 1; ++gzo) {
				Vec2d1i mark = Voronoi.sampleVoronoiGrid(gxo, gzo, (int) (this.seed & 0xFFFFFFFL));

				double dx = mark.x() - x;
				double dy = mark.y() - z;
				double sqrdist = dx * dx + dy * dy;

				int type = mark.value() % 4;

				if (sqrdist <= 1.0) {
					switch (type) {
					case 0:
						if (sqrdist < md) {
							md = sqrdist;
							mountainousness = 1.0 - sqrdist;
						}
						break;
					case 1:
						if (sqrdist < pd) {
							pd = sqrdist;
							plainsness = 1.0 - sqrdist;
						}
						break;
					case 2:
						if (sqrdist < hd) {
							hd = sqrdist;
							hillsness = 1.0 - sqrdist;
						}
						break;
					case 3:
						if (sqrdist < od) {
							od = sqrdist;
							oceanness = 1.0 - sqrdist;
						}
						break;
					}
				}
			}
		}

		double divisi = hillsness + plainsness + mountainousness + oceanness + 0.01;
		// a constant value of something with weight 0.01 always resides in background to prevent div by 0

		double period = ((620.0 * (mountainousness + oceanness)) + (280 * (hillsness + plainsness + 0.01))) / divisi;
		double amplitude = ((30 * hillsness) + (45 * mountainousness) + (180 * (oceanness + plainsness + 0.01))) / divisi;
		double base = ((52 * oceanness) + (96 * hillsness) + (140 * mountainousness) + (75 * (plainsness + 0.01))) / divisi;
		double noise = 1.0 - 2 * Math.abs(this.noise.sample(x / period, z / period));
		noise *= amplitude;
		noise += base;

		// todo rivers
		// I will use so many approximations
		// that I will put the "engineering" in
		// "software engineering".
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		// TODO should I implement cave stuff here too
		BlockState[] states = new BlockState[world.getHeight()];
		int height = this.getHeight(x, z, Type.WORLD_SURFACE_WG, world);

		int i = 0;
		int y;

		for (y = world.getBottomY(); y < height; ++y) {
			states[i++] = STONE;
		}

		int seaLevel = this.getSeaLevel();

		while (y++ < seaLevel) {
			states[i++] = WATER;
		}

		while (i < states.length) {
			states[i++] = AIR;
		}

		return new VerticalBlockSample(world.getBottomY(), states);
	}

	public static final BlockState GRIMSTONE = Blocks.DEEPSLATE.getDefaultState();
	public static final BlockState STONE = Blocks.STONE.getDefaultState();
	public static final BlockState AIR = Blocks.AIR.getDefaultState();
	public static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
	public static final BlockState WATER = Blocks.WATER.getDefaultState();
}
