package valoeghese.metera;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
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
}
