package valoeghese.metera;

import java.util.List;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
public class Network {
	public static final Identifier SYNC_ID = new Identifier("metera", "sync");

	// doesn't touch client only classes shell be right mate
	public static void init() {
		ClientSidePacketRegistry.INSTANCE.register(SYNC_ID, (context, data) -> {
			WorldData.clientDaySpeed = data.readLong();
		});
	}

	public static void syncs2c(List<ServerPlayerEntity> players, long speed) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeLong(speed);

		for (ServerPlayerEntity player : players) {
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Network.SYNC_ID, buf);
		}
	}
}
