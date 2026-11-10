package com.avi.skram.mods.infernal_mobs.modifiers;

import atomicstryker.infernalmobs.common.MobModifier;
import com.avi.skram.audio.SkramSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MobModifierUnholyPriest extends MobModifier {

  public static String[] PREFIX = {"unholyPriest"};
  public static String[] SUFFIX = {"unholyPriest"};

  public static int COOLDOWN_SECONDS = 10;
  public static long COOLDOWN_MILLISECONDS = COOLDOWN_SECONDS * 1000;
  public static int COOLDOWN_TICKS = COOLDOWN_SECONDS * 20;
  public static double RADIUS = 10;

  public long lastUsedTime = 0L;


  public MobModifierUnholyPriest() {
    super();
  }

  public MobModifierUnholyPriest(MobModifier next) {
    super(next);
  }

  @Override
  public String getModName() {
    return "unholyPriest";
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
        if (!(entity instanceof Mob enemy)) continue;

        // Mimic the rally spell.
        enemy.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, COOLDOWN_TICKS + 5, 0, false, true));
        enemy.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, COOLDOWN_TICKS + 5, 0, false, true));
        enemy.addEffect(new MobEffectInstance(MobEffects.SPEED, COOLDOWN_TICKS + 5, 1, false, true));
        // Many undead are immune to regeneration effect, so we heal directly instead.
        enemy.heal(4.0f);
      }

      mob.level().playSound(null, mob.getX(), mob.getY(), mob.getZ(), SkramSounds.SPELL_CAST_GENERIC3, SoundSource.HOSTILE, 0.45f, 1.0f);
      lastUsedTime = time;
    }

    return super.onUpdate(mob);
  }
}
