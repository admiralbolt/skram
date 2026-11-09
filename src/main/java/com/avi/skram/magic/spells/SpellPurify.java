package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class SpellPurify extends Spell {

  public static String NAME = "purify";
  public static int REQUIRED_MAGIC_LEVEL = 25;
  public static int COST = 2000;
  public static int CASTING_TICKS = 10;

  public SpellPurify() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    player.removeAllEffects();
  }

  @Override
  public String getDescription(Player player) {
    return "Remove ALL effects currently applied to you.";
  }
}
