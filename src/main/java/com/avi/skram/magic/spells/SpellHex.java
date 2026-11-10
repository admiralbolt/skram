package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.effect.SkramEffects;
import com.avi.skram.entity.projectile.SkramColoredProjectile;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import com.avi.skram.utils.Palette;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SpellHex extends Spell {

  public static String NAME = "hex";
  public static int REQUIRED_MAGIC_LEVEL = 24;
  public static int COST = 1200;
  public static int CASTING_TICKS = 45;

  public static final int AMPLIFIER = 1;
  public static final int DURATION_TICKS = 200;

  public SpellHex() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_OMINOUS);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);
    SkillMagic magic = player.getData(Skills.MAGIC);

    Vec3 angle = player.getLookAngle();
    SkramColoredProjectile entity = new SkramColoredProjectile(player.level(), player, angle.normalize(), Palette.OBSIDIAN, (Entity hitEntity) -> {
      if (!(hitEntity instanceof LivingEntity thing)) return;

      int duration = (int) ((1 + magic.getSpellPower()) * DURATION_TICKS);

      thing.addEffect(new MobEffectInstance(SkramEffects.HEXED, duration, AMPLIFIER, false, true));
    });
    entity.setPos(entity.getX(), player.getY(0.5) + 0.5, entity.getZ());
    player.level().addFreshEntity(entity);
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Fire a projectile that inflicts Hexed Lvl. 2 for %s. Hexed Lvl. 2 reduces healing by 50%% and increase ALL incoming damage by 50%%.", MathUtils.formatTicks((int) ((1 + magic.getSpellPower()) * DURATION_TICKS)));
  }
}
