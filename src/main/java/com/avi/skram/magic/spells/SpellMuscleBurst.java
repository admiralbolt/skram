package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class SpellMuscleBurst extends Spell {

  public static String NAME = "muscle burst";
  public static int REQUIRED_MAGIC_LEVEL = 6;
  public static int COST = 800;
  public static int CASTING_TICKS = 20;

  public static double BASE_HEAL = 3.0;

  public SpellMuscleBurst() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void finishCast(Player player) {
    super.finishCast(player);

    player.addEffect(new MobEffectInstance(MobEffects.HASTE, 240, 1, false, true));
    player.addEffect(new MobEffectInstance(MobEffects.SPEED, 240, 1, false, true));
    player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 240, 1, false, true));
  }
}
