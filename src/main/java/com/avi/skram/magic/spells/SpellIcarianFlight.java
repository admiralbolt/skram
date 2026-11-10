package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class SpellIcarianFlight extends Spell {

  public static String NAME = "icarian_flight";
  public static int REQUIRED_MAGIC_LEVEL = 22;
  public static int COST = 3000;
  public static int CASTING_TICKS = 80;

  public static int DURATION_TICKS = 140;
  public static int AMPLIFIER = 100;

  public SpellIcarianFlight() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_GENERIC5);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    SkillMagic magic = player.getData(Skills.MAGIC);
    player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, DURATION_TICKS, (int) ((1 + magic.getSpellPower()) * AMPLIFIER), false, true));
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);

    return "Soar to new heights. Applies Jump Boost Lvl. " + (1 + (int) ((1 + magic.getSpellPower()) * AMPLIFIER)) + " for " + MathUtils.formatTicks(DURATION_TICKS) + ".";
  }
}
