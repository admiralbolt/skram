package com.avi.skram.entity.projectile;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class SkramSmallFireball extends SmallFireball {

  public float damage = 4.0F;

  public SkramSmallFireball(Level level, LivingEntity mob, Vec3 direction, float damage) {
    super(level, mob, direction);
    this.damage = damage;
  }

  @Override
  protected void onHitEntity(@NonNull EntityHitResult hitResult) {
    Level var3 = this.level();
    if (var3 instanceof ServerLevel serverLevel) {
      Entity var7 = hitResult.getEntity();
      Entity owner = this.getOwner();
      int remainingFireTicks = var7.getRemainingFireTicks();
      var7.igniteForSeconds(5.0F);
      DamageSource damageSource = this.damageSources().fireball(this, owner);
      if (!var7.hurtServer(serverLevel, damageSource, damage)) {
        var7.setRemainingFireTicks(remainingFireTicks);
      } else {
        EnchantmentHelper.doPostAttackEffects(serverLevel, var7, damageSource);
      }
    }
  }

}
