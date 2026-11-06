package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class SpellFeatherFall extends Spell {

  public static String NAME = "feather_fall";
  public static int REQUIRED_MAGIC_LEVEL = 9;
  public static int COST = 800;
  public static int CASTING_TICKS = 6;

  public static int DURATION_TICKS = 200;
  public static int AMPLIFIER = 2;

  public SpellFeatherFall() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void finishCast(Player player) {
    super.finishCast(player);

    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, DURATION_TICKS, AMPLIFIER, false, true));
  }
}
