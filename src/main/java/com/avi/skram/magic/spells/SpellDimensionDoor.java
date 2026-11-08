package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import net.minecraft.world.entity.player.Player;

public class SpellDimensionDoor extends Spell {

  public static String NAME = "dimension_door";
  public static int REQUIRED_MAGIC_LEVEL = 20;
  public static int COST = 1500;
  public static int CASTING_TICKS = 20;

  public SpellDimensionDoor() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void finishCast(Player player) {
    super.finishCast(player);
    // Even though the cast finishes on the server side, we want to get the looked at block from the client side.
    // We will do this with a ping pong network call.

  }

  @Override
  public String getDescription(Player player) {
    return "Teleport to the targeted location.";
  }
}
