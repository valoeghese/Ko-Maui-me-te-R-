package valoeghese.metera;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class Metera implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Metera");

	@Override
	public void onInitialize() {
		EntityType<TeRaa> teRaa = Registry.register(Registry.ENTITY_TYPE, "metera:te_raa", FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, RedTailedTropicBirdEntity::new)
				.dimensions(EntityDimensions.fixed(0.4f, 0.8f))
				.build());
		Defa
	}
}
