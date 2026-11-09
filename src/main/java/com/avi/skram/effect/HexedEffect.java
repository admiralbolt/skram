package com.avi.skram.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

// Also like 90% stolen from Iron n' Spells.
public class HexedEffect extends MobEffect {

  public static final String NAME = "hexed";

  protected HexedEffect(MobEffectCategory category, int color) {
    super(category, color);
  }

  public static void reduceHealing(LivingHealEvent event) {
    MobEffectInstance effect = event.getEntity().getEffect(SkramEffects.HEXED);
    if (effect == null) return;

    float healingMult = (effect.getAmplifier() + 1) * 0.25f;
    event.setAmount(Math.max(0, event.getAmount() * healingMult));
  }

  public static void increaseDamage(LivingDamageEvent.Pre event) {
    MobEffectInstance effect = event.getEntity().getEffect(SkramEffects.HEXED);
    if (effect == null) return;

    float damageMult = 1 + (effect.getAmplifier() + 1) * 0.25f;
    event.setNewDamage(event.getNewDamage() * damageMult);
  }
}
