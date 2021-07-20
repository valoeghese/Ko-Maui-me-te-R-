package valoeghese.metera;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class TeRaa extends FlyingEntity {
	public TeRaa(EntityType<? extends FlyingEntity> entityType, World world) {
		super(entityType, world);
		this.setNoGravity(true);
		this.bossBar = new ServerBossBar(getDisplayName(), ServerBossBar.Color.YELLOW, ServerBossBar.Style.PROGRESS);
	}

	private final ServerBossBar bossBar;
	private static final TrackedData<Boolean> INVULNERABLE = DataTracker.registerData(TeRaa.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> DRAGGERS = DataTracker.registerData(TeRaa.class, TrackedDataHandlerRegistry.INTEGER);
	private static final DamageSource BOBBER_DAMAGE = new DamageSource("meteraBobber") {
	};

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(DRAGGERS, 0);
		this.dataTracker.startTracking(INVULNERABLE, false);
	}

	private boolean invulnerable() {
		return this.dataTracker.get(INVULNERABLE) || this.getHealth() == 0.42069F;
	}

	private void markInvulnerable() {
		this.dataTracker.set(INVULNERABLE, true);

		if (!this.world.isClient) {
			// clear players from boss bar
			this.bossBar.clearPlayers();
		}

		this.setHealth(0.42069F); // I swear I literally have to have a marker here it doesn't save properly
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(7, new ShootFireballGoal());
		this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, (livingEntity) -> {
			return true; // todo only shoot those who have roped them?
		}));
	}

	public void addDragger(int count) {
		this.dataTracker.set(DRAGGERS, this.dataTracker.get(DRAGGERS) + count);
	}

	/**
	 * Used when it needs to calculate or recalculate velocity
	 */
	public void calculateVelocity(boolean drag) {
		long targetTime = 23000;
		long thisTime = this.world.getTimeOfDay() % 24000L;

		if (thisTime > targetTime) {
			this.setVelocity(0.0, 1.0, 0.0);
		} else {
			double targetHeight = this.world.getTopY() + 64;
			double thisHeight = this.getY();
			this.setVelocity(0.0, (targetHeight - thisHeight) / (double) ((targetTime - thisTime) / WorldData.getActualDaySpeed(this.world)) * (drag ? 0.2 : 1.0), 0.0);
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.invulnerable() || !(source == BOBBER_DAMAGE || source == DamageSource.OUT_OF_WORLD)) {
			// Nothing.
			return false;
		} else {
			return super.damage(source, amount);
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		if (source == BOBBER_DAMAGE) {
			this.markInvulnerable();

			if (!this.world.isClient) {
				Vec3d pos = this.getPos();
				this.world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 4.0f, 1.0f);
				WorldData.get(((ServerWorld) this.world)).setDaySpeed(1L);
			}
		} else {
			super.onDeath(source);
		}
	}

	@Override
	public void tick() {
		if (this.invulnerable()) {
			this.calculateVelocity(false);
		} else {
			// Invulnerable once past the time limit
			long targetTime = 23000;
			long thisTime = this.world.getTimeOfDay() % 24000L;

			if (thisTime > targetTime) {
				this.markInvulnerable();
			}

			if (!this.world.isClient) {
				this.calculateVelocity(this.dataTracker.get(DRAGGERS) > 0);
			}

			this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
		}

		super.tick();

		if (!this.world.isClient) {
			if (this.getY() > this.world.getTopY() + 64) {
				this.remove(RemovalReason.DISCARDED);
			} else {
				int draggers = this.dataTracker.get(DRAGGERS);

				if (draggers > 0 && this.world.getTime() % 2 == 0) {
					this.damage(BOBBER_DAMAGE, draggers);
				}

				BlockPos start = this.getBlockPos();
				Mutable pos = new Mutable();

				for (int y = 4; y >= 3; --y) {
					pos.setY(start.getY() + y);

					if (pos.getY() < this.world.getTopY()) {
						for (int x = -2; x <= 2; ++x) {
							pos.setX(start.getX() + x);

							for (int z = -2; z <= 2; ++z) {
								pos.setZ(start.getZ() + z);
								// breakBlock checks air for us, so no need to duplicate the check.
								this.world.breakBlock(pos, false);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onStartedTrackingBy(ServerPlayerEntity player) {
		// tracked data seems to break idk why
		if (!this.invulnerable()) {
			this.bossBar.addPlayer(player);
		}
	}

	@Override
	public void onStoppedTrackingBy(ServerPlayerEntity player) {
		this.bossBar.removePlayer(player);
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	private class ShootFireballGoal extends Goal {
		public int cooldown;

		public boolean canStart() {
			return TeRaa.this.getTarget() != null;
		}

		public void start() {
			this.cooldown = 0;
		}

		public void tick() {
			LivingEntity livingEntity = TeRaa.this.getTarget();

			if (livingEntity.squaredDistanceTo(TeRaa.this) < 2048.0D && TeRaa.this.canSee(livingEntity)) {
				World world = TeRaa.this.world;
				++this.cooldown;
				if (this.cooldown == 10 && !TeRaa.this.isSilent()) {
					world.syncWorldEvent((PlayerEntity)null, WorldEvents.LAVA_EXTINGUISHED, TeRaa.this.getBlockPos(), 0);
				}

				if (this.cooldown == 20) {
					Vec3d vec3d = TeRaa.this.getRotationVec(1.0F);
					double f = livingEntity.getX() - (TeRaa.this.getX() + vec3d.x * 4.0D);
					double g = livingEntity.getBodyY(0.5D) - (0.5D + TeRaa.this.getBodyY(0.5D));
					double h = livingEntity.getZ() - (TeRaa.this.getZ() + vec3d.z * 4.0D);
					if (!TeRaa.this.isSilent()) {
						world.syncWorldEvent((PlayerEntity)null, WorldEvents.GHAST_SHOOTS, TeRaa.this.getBlockPos(), 0);
					}

					FireballEntity fireballEntity = new FireballEntity(world, TeRaa.this, f, g, h, 6);
					fireballEntity.setPosition(TeRaa.this.getX() + vec3d.x * 4.0D, TeRaa.this.getBodyY(0.5D) + 0.5D, fireballEntity.getZ() + vec3d.z * 4.0D);
					world.spawnEntity(fireballEntity);
					this.cooldown = -40;
				}
			} else if (this.cooldown > 0) {
				--this.cooldown;
			}
		}
	}
}
