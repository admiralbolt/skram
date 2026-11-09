package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SpellPlanarShift extends Spell {

  public static String NAME = "planar_shift";
  public static int REQUIRED_MAGIC_LEVEL = 35;
  public static int COST = 3000;
  public static int CASTING_TICKS = 200;

  public SpellPlanarShift() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, Holder.direct(SoundEvents.PLAYER_TELEPORT));
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    NetherPortalBlock block = (NetherPortalBlock) Blocks.NETHER_PORTAL;
    BlockPos playerPos = new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ());
    TeleportTransition teleportTransition = block.getPortalDestination((ServerLevel) player.level(), player, playerPos);
    if (teleportTransition == null) return;

    player.teleport(teleportTransition);
  }

  @Override
  public String getDescription(Player player) {
    return "Change dimensions.";
  }
}
