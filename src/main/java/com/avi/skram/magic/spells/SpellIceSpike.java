package com.avi.skram.magic.spells;

import com.avi.skram.entity.projectile.Icicle;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class SpellIceSpike extends Spell {

  public static String NAME = "ice_spike";
  public static int REQUIRED_MAGIC_LEVEL = 14;
  public static int COST = 900;
  public static int CASTING_TICKS = 20;

  public static double BASE_DAMAGE = 6.0;

  public SpellIceSpike() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, Holder.direct(SoundEvents.SNOW_HIT));
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);
    SkillMagic magic = player.getData(Skills.MAGIC);

    float damage = (float) (BASE_DAMAGE * (1 + magic.getSpellPower()));
    Icicle icicle = new Icicle(player.level(), player, damage);
    icicle.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5f, 0);
    player.level().addFreshEntity(icicle);
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Launch a fast moving ice spike in the target direction, dealing %.1f damage. The ice spike can pierce through multiple enemies, dealing less damage each time.", (float) (BASE_DAMAGE * (1 + magic.getSpellPower())));
  }
}
