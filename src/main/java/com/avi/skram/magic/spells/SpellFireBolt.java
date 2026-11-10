package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.entity.projectile.SkramSmallFireball;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SpellFireBolt extends Spell {

  public static String NAME = "fire_bolt";
  public static int REQUIRED_MAGIC_LEVEL = 5;
  public static int COST = 500;
  public static int CASTING_TICKS = 20;

  public static double BASE_DAMAGE = 4.0;

  public SpellFireBolt() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_FIRE);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);
    SkillMagic magic = player.getData(Skills.MAGIC);

    Vec3 angle = player.getLookAngle();
    float damage = (float) (BASE_DAMAGE * (1 + magic.getSpellPower()));
    SkramSmallFireball entity = new SkramSmallFireball(player.level(), player, angle.normalize(), damage);
    entity.setPos(entity.getX(), player.getY(0.5) + 0.5, entity.getZ());
    player.level().addFreshEntity(entity);
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Launch a small firebolt in the target direction, dealing %.1f damage and setting ablaze for 5 seconds.", (float) (BASE_DAMAGE * (1 + magic.getSpellPower())));
  }
}
