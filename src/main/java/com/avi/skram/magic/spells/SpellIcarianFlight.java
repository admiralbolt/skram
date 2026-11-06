package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class SpellIcarianFlight extends Spell {

  public static String NAME = "icarian_flight";
  public static int REQUIRED_MAGIC_LEVEL = 30;
  public static int COST = 3000;
  public static int CASTING_TICKS = 80;

  public static int DURATION_TICKS = 140;
  public static int AMPLIFIER = 100;

  public SpellIcarianFlight() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void finishCast(Player player) {
    super.finishCast(player);

    player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, DURATION_TICKS, AMPLIFIER, false, true));
  }
}
