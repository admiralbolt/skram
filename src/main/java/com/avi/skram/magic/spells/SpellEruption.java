package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillDemolition;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class SpellEruption extends Spell {

  public static String NAME = "eruption";
  public static int REQUIRED_MAGIC_LEVEL = 20;
  public static int COST = 1000;
  public static int CASTING_TICKS = 10;

  public static float BASE_EXPLOSION_POWER = 3f;

  public SpellEruption() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_SLAM);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    SkillDemolition demolition = player.getData(Skills.DEMOLITION);
    SkillMagic magic = player.getData(Skills.MAGIC);
    float explosionPower = (float) (BASE_EXPLOSION_POWER * (1 + magic.getSpellPower()) * (1 + demolition.getBlastPower()));
    demolition.addXp(player, 50 * explosionPower);
    player.level().explode(player, Explosion.getDefaultDamageSource(player.level(), player), null, player.getX(), player.getY(0.0625F), player.getZ(), explosionPower, false, Level.ExplosionInteraction.TNT);
  }

  @Override
  public String getDescription(Player player) {
    return "Explode. §c WARNING: Somehow I don't think this warning is necessary.";
  }
}
