package com.avi.skram.mods.infernal_mobs.modifiers;

import atomicstryker.infernalmobs.common.MobModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class MobModifierDeathThroes extends MobModifier {

  public static String[] PREFIX = {"throes"};
  public static String[] SUFFIX = {"lastGasp"};

  public static float EXPLOSION_POWER = 4.0f;

  public MobModifierDeathThroes() {
    super();
  }

  public MobModifierDeathThroes(MobModifier next) {
    super(next);
  }

  @Override
  public String getModName() {
    return "throes";
  }

  @Override
  protected String[] getModNamePrefix() {
    return PREFIX;
  }

  @Override
  public String[] getModNameSuffix() {
    return SUFFIX;
  }

  @Override
  public float onHurt(LivingEntity mob, DamageSource source, float amount) {
    // We handle this in onHurt() instead of onDeath(), because we don't have any references to entities.
    // First we need to check to see if this is a killing blow.
    if (amount >= mob.getHealth()) {
      // Boom.
      mob.level().explode(mob, Explosion.getDefaultDamageSource(mob.level(), mob), null, mob.getX(), mob.getY(), mob.getZ(), EXPLOSION_POWER, false, Level.ExplosionInteraction.MOB);
    }

    return super.onHurt(mob, source, amount);
  }
}
