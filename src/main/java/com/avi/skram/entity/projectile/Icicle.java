package com.avi.skram.entity.projectile;

import com.avi.skram.entity.SkramEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.NonNull;

public class Icicle extends AbstractArrow {

  public static final String NAME = "icicle";

  public float damage = 4.0f;
  public Player playerOwner = null;

  public Icicle(EntityType<? extends Icicle> entityType, Level world) {
    super(entityType, world);
  }

  public Icicle(Level level, Player owner, float damage) {
    super(SkramEntities.ICICLE.get(), owner, level, new ItemStack(Items.ICE, 1), null);

    this.damage = damage;
    this.playerOwner = owner;
  }


  @Override
  public void tick() {
    super.tick();
    Level world = level();
    if (world.isClientSide() && !this.isInGround()) {
      world.addParticle(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
    }
  }

  @Override
  protected void onHitEntity(@NonNull EntityHitResult entityHitResult) {
    if (this.level().isClientSide()) return;

    DamageSource damageSource = this.damageSources().mobProjectile(this, this.playerOwner);
    entityHitResult.getEntity().hurtServer((ServerLevel) this.level(), damageSource, this.damage);

    if (entityHitResult.getEntity() instanceof LivingEntity live) {
      live.addEffect(new MobEffectInstance(
        MobEffects.SLOWNESS, (int) (3 * this.damage), 1, false, true
      ));
    }

    this.damage -= (float) Math.max(1.0f, 0.25 * this.damage);
    if (this.damage <= 0.0f) {
      this.discard();
    }
  }

  @Override
  protected double getDefaultGravity() {
    return 0;
  }

  @Override
  public boolean isNoGravity() {
    return true;
  }

  @Override
  protected SoundEvent getDefaultHitGroundSoundEvent() {
    return SoundEvents.SNOW_BREAK;
  }

  @Override
  protected ItemStack getDefaultPickupItem() {
    return null;
  }

}

