package com.avi.skram.mods.infernal_mobs.modifiers;

import atomicstryker.infernalmobs.common.MobModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MobModifierMighty extends MobModifier {

  public static String[] PREFIX = {"mighty"};
  public static String[] SUFFIX = {"theMighty"};

  public MobModifierMighty() {
    super();
  }

  public MobModifierMighty(MobModifier next) {
    super(next);
  }

  @Override
  public String getModName() {
    return "mighty";
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
  public boolean onUpdate(LivingEntity mob) {
    // There's no onEntitySpawned() hook, so we attempt to achieve the same thing here.
    if (!mob.hasEffect(MobEffects.STRENGTH)) {
      mob.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 60000, 2, false, true));
    }
    return super.onUpdate(mob);
  }
}
