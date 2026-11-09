package com.avi.skram.entity.projectile;

import com.avi.skram.entity.SkramEntities;
import com.avi.skram.utils.MathUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;

public class SkramColoredProjectile extends AbstractHurtingProjectile {

  public static final String NAME = "colored_projectile";
  public static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(SkramColoredProjectile.class, EntityDataSerializers.INT);

  public Consumer<Entity> callback;

  public SkramColoredProjectile(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
    super(type, level);
  }

  public SkramColoredProjectile(Level level, LivingEntity mob, Vec3 direction, Color color, Consumer<Entity> callback) {
    super(SkramEntities.COLORED_PROJECTILE.get(), mob, direction, level);
    this.entityData.set(DATA_COLOR, MathUtils.getColorValue(color));
    this.callback = callback;
  }

  public int getColor() {
    return this.entityData.get(DATA_COLOR);
  }

  @Override
  protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
    super.defineSynchedData(builder);
    builder.define(DATA_COLOR, 0);
  }

  @Override
  protected void onHitEntity(@NonNull EntityHitResult hitResult) {
    // Eventually we should take a supplier function as an arg to make this generic.
    this.callback.accept(hitResult.getEntity());
  }

  @Override
  protected void onHit(@NonNull HitResult hitResult) {
    super.onHit(hitResult);
    if (!this.level().isClientSide()) {
      this.discard();
    }

  }

  @Override
  protected @Nullable ParticleOptions getTrailParticle() {
    return ParticleTypes.GLOW;
  }

  @Override
  protected boolean shouldBurn() {
    return false;
  }

}
