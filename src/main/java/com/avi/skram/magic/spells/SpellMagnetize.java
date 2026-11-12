package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MinecraftUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class SpellMagnetize extends Spell {

  public static String NAME = "magnetize";
  public static int REQUIRED_MAGIC_LEVEL = 11;
  public static int COST = 800;
  public static int CASTING_TICKS = 30;

  public static double BASE_RADIUS = 10;

  public SpellMagnetize() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_GENERIC1);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);
    SkillMagic magic = player.getData(Skills.MAGIC);

    double radius = BASE_RADIUS * (1 + magic.getSpellPower());
    AABB hitBox = player.getHitbox().inflate(radius);
    List<ItemEntity> entities = player.level().getEntitiesOfClass(ItemEntity.class, hitBox);

    for (ItemEntity entity : entities) {
      MinecraftUtils.applyPull(entity, player.position(), entity.distanceTo(player) / 2);
    }

  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Pull all items in a %.1f block radius to you.", (BASE_RADIUS * (1 + magic.getSpellPower())));
  }
}
