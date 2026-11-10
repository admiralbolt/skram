package com.avi.skram.mods.infernal_mobs.modifiers;

import atomicstryker.infernalmobs.common.MobModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MobModifierInfested extends MobModifier {

  public static String[] PREFIX = {"infested"};
  public static String[] SUFFIX = {"theInfested"};

  public static int COOLDOWN_SECONDS = 10;
  public static long COOLDOWN_MILLISECONDS = COOLDOWN_SECONDS * 1000;
  public static int COOLDOWN_TICKS = COOLDOWN_SECONDS * 20;
  public static double RADIUS = 10;

  public long lastUsedTime = 0L;

  public MobModifierInfested() {
    super();
  }

  public MobModifierInfested(MobModifier next) {
    super(next);
  }

  @Override
  public String getModName() {
    return "infested";
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
    long time = System.currentTimeMillis();
    if (time - lastUsedTime > COOLDOWN_MILLISECONDS) {
      AABB newBox = mob.getBoundingBox().inflate(RADIUS);
      List<Entity> entities = mob.level().getEntities(null, newBox);

      for (Entity entity : entities) {
        if (!(entity instanceof LivingEntity live)) continue;

        if (!live.hasEffect(MobEffects.INFESTED)) {
          live.addEffect(new MobEffectInstance(MobEffects.INFESTED, COOLDOWN_TICKS + 5, 2, false, true));
        }
      }
      lastUsedTime = time;
    }

    return super.onUpdate(mob);
  }


}
