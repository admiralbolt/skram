package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SpellConjureSupplies extends Spell {

  public static String NAME = "conjure_supplies";
  public static int REQUIRED_MAGIC_LEVEL = 11;
  public static int COST = 1600;
  public static int CASTING_TICKS = 120;

  public SpellConjureSupplies() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    player.getInventory().add(new ItemStack(Items.TORCH, 3));
    player.getInventory().add(new ItemStack(Items.BREAD, 2));
    player.getInventory().add(new ItemStack(Items.POTION, 1));
  }

  @Override
  public String getDescription(Player player) {
    return "Conjures some basic supplies: 3x Torch, 2x Bread, 1x Water Bottle.";
  }
}
