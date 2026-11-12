package com.avi.skram.effect;

import com.avi.skram.attachments.MiscAttachments;
import com.avi.skram.attachments.SkramEntityModifier;
import com.avi.skram.blocks.PlayerOwnedData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class FlankedEffect extends MobEffect {

  public static final String NAME = "flanked";

  protected FlankedEffect(MobEffectCategory category, int color) {
    super(category, color);
  }

  public static void increaseDamage(LivingDamageEvent.Pre event) {
    MobEffectInstance effect = event.getEntity().getEffect(SkramEffects.FLANKED);
    if (effect == null) return;

    // Our entity needs to have a "flanker" set in its data attachment.
    SkramEntityModifier modifier = event.getEntity().getData(MiscAttachments.SKRAM_ENTITY_MODIFIER);
    if (modifier.flankApplicator == PlayerOwnedData.EMPTY_UUID) return;

    Player player = null;
    if (event.getSource().getEntity() instanceof Player) {
      player = (Player) event.getSource().getEntity();
    } else if (event.getSource().getDirectEntity() instanceof Player) {
      player = (Player) event.getSource().getDirectEntity();
    } else {
      return;
    }

    if (player.getUUID().equals(modifier.flankApplicator)) return;

    float damageMult = 1 + (effect.getAmplifier() + 1) * 0.25f;
    event.setNewDamage(event.getNewDamage() * damageMult);
  }
}
