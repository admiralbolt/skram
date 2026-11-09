package com.avi.skram.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

import java.util.List;

// 90% stolen from Iron n' Spells. Applies a constant hurtbox to the player.
public class BurningRushEffect extends MobEffect {

  public static final String NAME = "burning_rush";

  public BurningRushEffect(MobEffectCategory category, int color) {
    super(category, color);
  }

  @Override
  public boolean applyEffectTick(@NonNull ServerLevel serverLevel, @NonNull LivingEntity mob, int amplification) {
    if (mob.horizontalCollision) return false;

    List<Entity> entities = serverLevel.getEntities(mob, mob.getBoundingBox().inflate(0.25, 0.5, 0.25));
    if (entities.isEmpty()) return true;

    for (Entity entity : entities) {
      if (!(entity instanceof LivingEntity live)) continue;

      live.hurtServer(serverLevel, new DamageSource(serverLevel.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.PLAYER_ATTACK), mob), 4.0f);
      // Give i-frames since we are applying damage every tick.
      live.setInvulnerableTime(10);
    }

    return true;
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
    return true;
  }
}
