package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.entity.Decoy;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class SpellDecoy extends Spell {

  public static String NAME = "decoy";
  public static int REQUIRED_MAGIC_LEVEL = 14;
  public static int COST = 1400;
  public static int CASTING_TICKS = 15;

  public static int ALIVE_FOR_TICKS = 160;
  public static int BASE_RADIUS = 8;

  public SpellDecoy() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_GENERIC7);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    if (hitResult == null) return;

    Decoy decoy = new Decoy(player.level(), ALIVE_FOR_TICKS);
    decoy.setPos(hitResult.getLocation());
    player.level().addFreshEntity(decoy);

    // Then taunt!
    SkillMagic magic = player.getData(Skills.MAGIC);
    double radius = BASE_RADIUS * (1 + magic.getSpellPower());
    List<Entity> entitiesToTaunt = player.level().getEntities(
      decoy,
      decoy.getBoundingBox().inflate(radius),
      entity -> (entity.distanceToSqr(decoy) < radius * radius) && (entity instanceof Mob)
    );

    for (Entity entity : entitiesToTaunt) {
      if (!(entity instanceof Mob mob)) continue;
      ((ServerLevel) player.level()).sendParticles(
        ParticleTypes.ANGRY_VILLAGER,
        mob.getX(),
        mob.getY() + mob.getBbHeight() + 0.5,
        mob.getZ(),
        5,
        0.5, 0.5, 0.5,
        0.05
      );
      mob.setTarget(decoy);
    }

  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Summon a decoy at the target location for %s, and taunt all enemies within %d blocks.", MathUtils.formatTicks(ALIVE_FOR_TICKS), (int) (BASE_RADIUS * (1 + magic.getSpellPower())));
  }
}
