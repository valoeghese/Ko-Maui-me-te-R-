package valoeghese.metera.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import valoeghese.metera.Network;
import valoeghese.metera.WorldData;

@SuppressWarnings("deprecation")
@Mixin(PlayerManager.class)
public class MixinPlayerManager {
	@Shadow
	@Final
	private MinecraftServer server;

	@Inject(at = @At("RETURN"), method = "onPlayerConnect")
	private void onOnPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
		long speed = WorldData.getDaySpeed(this.server.getWorld(World.OVERWORLD));
		Network.syncs2c(List.of(player), speed);
	}
}
