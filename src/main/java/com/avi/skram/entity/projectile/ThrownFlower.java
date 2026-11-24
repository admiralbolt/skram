package com.avi.skram.entity.projectile;

import com.avi.skram.entity.SkramEntities;
import com.avi.skram.utils.MathUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.function.Consumer;

public class ThrownFlower extends ThrowableItemProjectile {

  public static final String NAME = "thrown_flower";

  private static final float DAMAGE = 2.0f;

  public ThrownFlower(EntityType<? extends ThrowableItemProjectile> type, Level level) {
    super(type, level);
  }

  public ThrownFlower(Level level, LivingEntity mob, ItemStack stack) {
    super(SkramEntities.THROWN_FLOWER.get(), mob, level, stack);
  }

  @Override
  protected @NonNull Item getDefaultItem() {
    return Items.POPPY;
  }

  @Override
  protected void onHitEntity(@NonNull EntityHitResult result) {
    super.onHitEntity(result);
    if (this.level().isClientSide()) return;

    result.getEntity().hurtServer((ServerLevel) this.level(), damageSources().thrown(this, getOwner()), DAMAGE);
    this.level().playSound(null, blockPosition(),
      SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.6f, 2.0f);
  }

  @Override
  protected void onHit(@NonNull HitResult result) {
    super.onHit(result);

    this.discard();
  }


}
