package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.effect.SkramEffects;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SpellBurningRush extends Spell {

  public static String NAME = "burning_rush";
  public static int REQUIRED_MAGIC_LEVEL = 21;
  public static int COST = 100;
  public static int CASTING_TICKS = 10;

  public static double BASE_SPEED = 2.0;

  public SpellBurningRush() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_FIRE);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    SkillMagic magic = player.getData(Skills.MAGIC);
    double speedMultiplier = BASE_SPEED * (1 + magic.getSpellPower());

    Vec3 forward = player.getLookAngle();

    Vec3 vec = forward.multiply(3, 1, 3).normalize().add(0, .1, 0).scale(speedMultiplier);
    if (player.onGround()) {
      player.setPos(player.getPosition(1f).add(0, 1.5, 0));
      vec.add(0, 0.1, 0);
    }

    player.setDeltaMovement(new Vec3(
      Mth.lerp(0.75f, player.getDeltaMovement().x, vec.x),
      Mth.lerp(0.75f, player.getDeltaMovement().y, vec.y),
      Mth.lerp(0.75f, player.getDeltaMovement().z, vec.z)
    ));
    player.syncVelocity = true;

    player.addEffect(new MobEffectInstance(SkramEffects.BURNING_RUSH, 15, 0, false, true));
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Launch forward at high speeds, turning your body into a projectile. Launch speed: %.1f.", BASE_SPEED * (1 + magic.getSpellPower()));
  }
}
