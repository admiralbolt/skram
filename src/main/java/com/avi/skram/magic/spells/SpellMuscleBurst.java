package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SpellMuscleBurst extends Spell {

  public static String NAME = "muscle_burst";
  public static int REQUIRED_MAGIC_LEVEL = 6;
  public static int COST = 2000;
  public static int CASTING_TICKS = 60;

  public static int DURATION_TICKS = 540;
  public static int AMPLIFIER = 1;

  public SpellMuscleBurst() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void doEffect(Player player, Vec3 targetBlock, Direction direction) {
    super.doEffect(player, targetBlock, direction);

    player.addEffect(new MobEffectInstance(MobEffects.HASTE, DURATION_TICKS, AMPLIFIER, false, true));
    player.addEffect(new MobEffectInstance(MobEffects.SPEED, DURATION_TICKS, AMPLIFIER, false, true));
    player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, DURATION_TICKS, AMPLIFIER, false, true));
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Gain a burst of speed and strength. Applies Haste Lvl. %d, Speed Lvl. %d, and Strength Lvl. %d for %s.",
      (AMPLIFIER + 1),  (AMPLIFIER + 1), (AMPLIFIER + 1), MathUtils.formatTicks((int) ((1 + magic.getSpellPower()) * DURATION_TICKS))
    );
  }
}
