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
	public static long clientDaySpeed;

	public long getDaySpeed() {
		return this.daySpeed;
	}

	public void setDaySpeed(long daySpeed) {
		if (this.daySpeed != daySpeed) {
			this.daySpeed = daySpeed;
			
			this.markDirty();
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound compoundTag) {
		compoundTag.putLong("daySpeed", this.daySpeed);
		return compoundTag;
	}

	public static WorldData get(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(WorldData::new, WorldData::new, "metera");
	}

	public static long getDaySpeed(World world) {
		if (world.isClient) {
			return clientDaySpeed;
		} else {
			return get((ServerWorld) world).getDaySpeed();
		}
	}
}
