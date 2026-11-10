package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class SpellRally extends Spell {

  public static String NAME = "rally";
  public static int REQUIRED_MAGIC_LEVEL = 24;
  public static int COST = 3000;
  public static int CASTING_TICKS = 100;

  public static int DURATION_TICKS = 300;
  public static double RADIUS = 5;
  public static int AMPLIFIER = 0;

  public SpellRally() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_GENERIC3);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    SkillMagic magic = player.getData(Skills.MAGIC);
    int duration = (int) ((1 + magic.getSpellPower()) * DURATION_TICKS);

    List<Entity> entities = player.level().getEntities(
      player,
      player.getBoundingBox().inflate(RADIUS),
      entity -> (entity.distanceToSqr(player) < RADIUS * RADIUS) && (entity instanceof Player)
    );
    entities.add(player);

    for (Entity entity : entities) {
      if (!(entity instanceof Player playerToBuff)) continue;

      playerToBuff.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, AMPLIFIER, false, true));
      playerToBuff.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, duration, AMPLIFIER, false, true));
      playerToBuff.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, AMPLIFIER, false, true));
    }


  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Appy Absorption Lvl. 1, Resistance Lvl. 1, and Regeneration Lvl. 1 to all Players within %d blocks of you for %s.",
      (int) RADIUS, MathUtils.formatTicks((int) ((1 + magic.getSpellPower()) * DURATION_TICKS))
    );
  }
}
