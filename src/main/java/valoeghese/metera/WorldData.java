package valoeghese.metera;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public final class WorldData extends PersistentState {
	private WorldData(NbtCompound compoundTag) {
		this.daySpeed = compoundTag.contains("daySpeed") ? compoundTag.getLong("daySpeed") : 1L;
	}

	private WorldData() {
	}

	private long daySpeed = 1L;
	private transient ServerWorld world;
	public static long clientDaySpeed;

	public WorldData setServerWorld(ServerWorld world) {
		this.world = world;
		return this;
	}

	public long getDaySpeed() {
		return this.daySpeed;
	}

	public void setDaySpeed(long daySpeed) {
		if (this.daySpeed != daySpeed) {
			this.daySpeed = daySpeed;
			Network.syncs2c(this.world.getServer().getPlayerManager().getPlayerList(), this.daySpeed);
			this.markDirty();
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound compoundTag) {
		compoundTag.putLong("daySpeed", this.daySpeed);
		return compoundTag;
	}

	public static WorldData get(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(WorldData::new, WorldData::new, "metera").setServerWorld(world);
	}

	public static long getDaySpeed(World world) {
		if (world.isClient) {
			return clientDaySpeed;
		} else {
			return get((ServerWorld) world).getDaySpeed();
		}
	}
}
