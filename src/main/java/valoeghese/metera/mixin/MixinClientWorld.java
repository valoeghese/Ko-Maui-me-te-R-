package valoeghese.metera.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
	@ModifyConstant(
			method = "tickTime()V",
			constant = @Constant(longValue = 1L, ordinal = 1)
			)
	private long getIncrement(long incr) {
		return 5L;
	}
}
