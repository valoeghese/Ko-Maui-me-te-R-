package valoeghese.metera.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
// TODO do this *later*
public class MixinWorldRenderer {
	//@Inject(at = @At(value = "INVOKE", ordinal = 1, target = "Lnet.minecraft.client.render.BufferRenderer;draw(Lnet.minecraft.client.render.BufferRenderer;)V"), method = "renderSky")
}
