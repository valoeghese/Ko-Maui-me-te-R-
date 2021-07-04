package valoeghese.metera;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

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

		// I don't feel like using a CMI
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			MeteraModels.init();
			Network.init();
		}
	}
}
