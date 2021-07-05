package valoeghese.metera;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class Metera implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Metera");

	public static EntityType<TeRaa> TE_RAA;

	@Override
	public void onInitialize() {
		TE_RAA = Registry.register(Registry.ENTITY_TYPE, "metera:te_raa", FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, TeRaa::new)
				.dimensions(EntityDimensions.fixed(4.0f, 4.0f))
				.build());
		FabricDefaultAttributeRegistry.register(TE_RAA, MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 50.0D));

		Registry.register(Registry.ITEM, "metera:suspicious_looking_sunbeam", new EventTriggerItem(new Item.Settings().fireproof().maxCount(1)));

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			// I think each tick adds 1 to time of day so this should run
			ServerWorld world = server.getWorld(World.OVERWORLD);
			if (world.getTimeOfDay() % 24000L == 21000L) {
				for (ServerPlayerEntity player : world.getPlayers()) {
					ChunkPos cp = player.getChunkPos();
					BlockPos bp = player.getBlockPos();

					if (cp.x == 0 && cp.z == 0 && player.getPos().getY() > world.getChunk(cp.x, cp.z).getHeightmap(Heightmap.Type.MOTION_BLOCKING).get(bp.getX(), bp.getZ())) {
						TeRaa boss = TE_RAA.create(world);
						boss.refreshPositionAndAngles(bp.up(), 0, 0);
						world.spawnEntity(boss);
					}
				}
			}
		});

		// I don't feel like using a CMI
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			MeteraModels.init();
			Network.init();
		}
	}
}
