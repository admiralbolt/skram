package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MinecraftUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class SpellRepulsion extends Spell {

  public static String NAME = "repulsion";
  public static int REQUIRED_MAGIC_LEVEL = 15;
  public static int COST = 1900;
  public static int CASTING_TICKS = 6;

  public static int RADIUS = 7;
  public static double BASE_KNOCKBACK = 2.0;

  public SpellRepulsion() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_FIRE);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);
    SkillMagic magic = player.getData(Skills.MAGIC);

    double knockback = BASE_KNOCKBACK * (1 + magic.getSpellPower());
    AABB hitBox = player.getHitbox().inflate(RADIUS);
    List<Entity> entities = player.level().getEntities(player, hitBox);

    for (Entity entity : entities) {
      MinecraftUtils.applyKnockback(entity, player.position(), knockback);
    }
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Launch a wave of force around you, knocking back all entities in a %d block radius with force: %.1f", RADIUS, (BASE_KNOCKBACK * (1 + magic.getSpellPower())));
  }
}
