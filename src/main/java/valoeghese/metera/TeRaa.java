package valoeghese.metera;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class TeRaa extends FlyingEntity {
	public TeRaa(EntityType<? extends FlyingEntity> entityType, World world) {
		super(entityType, world);
		this.setNoGravity(true);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(7, new ShootFireballGoal());
		this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, (livingEntity) -> {
			return Math.abs(livingEntity.getY() - this.getY()) <= 4.0D; // todo only shoot those who have roped them
		}));
	}

	@Override
	public void tick() {
		this.setVelocity(0.0, 1.0, 0.0); // todo the sun rises
		super.tick();
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
