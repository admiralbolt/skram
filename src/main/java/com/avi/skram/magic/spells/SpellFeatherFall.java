package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SpellFeatherFall extends Spell {

  public static String NAME = "feather_fall";
  public static int REQUIRED_MAGIC_LEVEL = 9;
  public static int COST = 1000;
  public static int CASTING_TICKS = 6;

  public static int DURATION_TICKS = 140;
  public static int AMPLIFIER = 2;

  public SpellFeatherFall() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_GENERIC2);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    SkillMagic magic = player.getData(Skills.MAGIC);
    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, (int) ((1 + magic.getSpellPower()) * DURATION_TICKS), AMPLIFIER, false, true));
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);

    return "Quickly applies slow fall to yourself. Applies slow fall Lvl. " + (AMPLIFIER + 1) + " for " + MathUtils.formatTicks((int) ((1 + magic.getSpellPower()) * DURATION_TICKS)) + ".";
  }
}
