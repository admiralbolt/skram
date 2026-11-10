package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class SpellFaerieFire extends Spell {

  public static String NAME = "faerie_fire";
  public static int REQUIRED_MAGIC_LEVEL = 8;
  public static int COST = 500;
  public static int CASTING_TICKS = 30;

  public static double BASE_DISTANCE = 20;
  public static int DURATION_TICKS = 600;

  public SpellFaerieFire() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_GENERIC1);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    SkillMagic magic = player.getData(Skills.MAGIC);
    AABB box = player.getBoundingBox().inflate(BASE_DISTANCE * (1 + magic.getSpellPower()));

    List<LivingEntity> entities = player.level().getEntitiesOfClass(
      LivingEntity.class,
      box
    );

    for (LivingEntity entity : entities) {
      entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, DURATION_TICKS, 0, false, true));
    }
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Make all living entities in a %d block radius glow for %s.", (int) (BASE_DISTANCE * (1 + magic.getSpellPower())), MathUtils.formatTicks(DURATION_TICKS));
  }
}
