package valoeghese.metera.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import valoeghese.metera.TeRaa;

@Mixin(FishingBobberEntity.class)
public class MixinBobber {
	@Shadow
	private Entity hookedEntity;

	@Inject(at = @At("HEAD"), method = "updateHookedEntityId")
	private void onUpdateHookedEntityId(@Nullable Entity entity, CallbackInfo info) {
		if (this.hookedEntity != entity) {
			if (this.hookedEntity != null && this.hookedEntity instanceof TeRaa) {
				((TeRaa) this.hookedEntity).addDragger(-1);
			}
			if (entity != null && entity instanceof TeRaa) {
				((TeRaa) entity).addDragger(1);
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "remove")
	private void onRemove(Entity.RemovalReason reason, CallbackInfo info) {
		// and this only runs server side I think
		if (this.hookedEntity != null && this.hookedEntity instanceof TeRaa) {
			((TeRaa) this.hookedEntity).addDragger(-1);
		}
	}

	@Inject(at = @At("HEAD"), method = "onRemoved")
	private void onOnRemoved(CallbackInfo info) {
		// this only runs client side it should be fine
		if (this.hookedEntity != null && this.hookedEntity instanceof TeRaa) {
			((TeRaa) this.hookedEntity).addDragger(-1);
		}
	}
}
