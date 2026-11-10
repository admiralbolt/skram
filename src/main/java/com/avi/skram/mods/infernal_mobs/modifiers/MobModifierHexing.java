package com.avi.skram.mods.infernal_mobs.modifiers;

import atomicstryker.infernalmobs.common.MobModifier;
import com.avi.skram.effect.SkramEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class MobModifierHexing extends MobModifier {

  public static int DURATION_TICKS = 200;
  public static int AMPLIFIER = 2;

  public static String[] PREFIX = {"hexing"};
  public static String[] SUFFIX = {"theDamned"};

  public MobModifierHexing() {
    super();
  }

  public MobModifierHexing(MobModifier next) {
    super(next);
  }

  @Override
  public String getModName() {
    return "hexing";
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
  public float onAttack(LivingEntity entity, DamageSource source, float amount) {
    if (entity != null) {
      entity.addEffect(new MobEffectInstance(SkramEffects.HEXED, DURATION_TICKS, AMPLIFIER, false, true));
    }

    return super.onAttack(entity, source, amount);
  }
}
