package com.avi.skram.magic.spells;

import com.avi.skram.utils.MinecraftUtils;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class SpellPlanarShift extends Spell {

  public static String NAME = "planar_shift";
  public static int REQUIRED_MAGIC_LEVEL = 35;
  public static int COST = 2600;
  public static int CASTING_TICKS = 200;

  public SpellPlanarShift() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, Holder.direct(SoundEvents.PLAYER_TELEPORT));
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);
    MinecraftUtils.teleportBetweenDimensions(player);
  }

  @Override
  public String getDescription(Player player) {
    return "Shift between dimensions. §c WARNING: This spells is incredibly powerful and dangerous. Use at your own risk.";
  }
}
