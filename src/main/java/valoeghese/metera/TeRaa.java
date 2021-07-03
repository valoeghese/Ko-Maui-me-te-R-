package valoeghese.metera;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class TeRaa extends FlyingEntity {
	public TeRaa(EntityType<? extends FlyingEntity> entityType, World world) {
		super(entityType, world);
		this.setNoGravity(true);
		bossBar = new ServerBossBar(getDisplayName(), ServerBossBar.Color.YELLOW, ServerBossBar.Style.PROGRESS);
	}

	private final ServerBossBar bossBar;

	@Override
	protected void initGoals() {
		this.goalSelector.add(7, new ShootFireballGoal());
		this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, (livingEntity) -> {
			return true; // todo only shoot those who have roped them
		}));
	}

	@Override
	public void tick() {
		if (!this.world.isClient) {
			this.setVelocity(0.0, 1.0, 0.0); // todo the sun rises
		}

		super.tick();

		if (!this.world.isClient) {
			if (this.getY() > this.getEntityWorld().getTopY() + 64) {
				this.remove(RemovalReason.DISCARDED);
			}

			BlockPos start = this.getBlockPos();
			Mutable pos = new Mutable();

			for (int y = 4; y >= 3; --y) {
				pos.setY(start.getY() + y);

				for (int x = -3; x <= 3; ++x) {
					pos.setX(start.getX() + x);

					for (int z = -3; z <= 3; ++z) {
						pos.setZ(start.getZ() + z);
						this.world.breakBlock(pos, false);
					}
				}
			}
		}
	}

	@Override
	public void onStartedTrackingBy(ServerPlayerEntity player) {
		bossBar.addPlayer(player);
	}

	@Override
	public void onStoppedTrackingBy(ServerPlayerEntity player) {
		bossBar.removePlayer(player);
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
					world.syncWorldEvent((PlayerEntity)null, WorldEvents.GHAST_WARNS, TeRaa.this.getBlockPos(), 0);
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
