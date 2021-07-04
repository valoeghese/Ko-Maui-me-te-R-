package valoeghese.metera.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import valoeghese.metera.WorldData;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
	@ModifyConstant(
			method = "tickTime()V",
			constant = @Constant(longValue = 1L, ordinal = 1)
			)
	private long getIncrement(long incr) {
		return WorldData.getActualDaySpeed((World) (Object) this);
	}
}
