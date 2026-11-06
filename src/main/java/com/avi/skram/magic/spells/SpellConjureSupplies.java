package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SpellConjureSupplies extends Spell {

  public static String NAME = "conjure_supplies";
  public static int REQUIRED_MAGIC_LEVEL = 11;
  public static int COST = 1300;
  public static int CASTING_TICKS = 120;

  public SpellConjureSupplies() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void finishCast(Player player) {
    super.finishCast(player);

    player.getInventory().add(new ItemStack(Items.TORCH, 3));
    player.getInventory().add(new ItemStack(Items.BREAD, 2));
    player.getInventory().add(new ItemStack(Items.POTION, 1));
  }
}
