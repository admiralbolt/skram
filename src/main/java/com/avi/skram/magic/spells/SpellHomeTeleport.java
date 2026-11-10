package com.avi.skram.magic.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SpellHomeTeleport extends Spell {

  public static String NAME = "home_teleport";
  public static int REQUIRED_MAGIC_LEVEL = 12;
  public static int COST = 2000;
  public static int CASTING_TICKS = 200;

  public SpellHomeTeleport() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, Holder.direct(SoundEvents.PLAYER_TELEPORT));
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);
    ServerPlayer.RespawnConfig config = ((ServerPlayer) player).getRespawnConfig();
    if (config == null) return;

    // Make sure we are in the same dimension!
    if (!player.level().dimension().equals(config.respawnData().dimension())) return;

    BlockPos target = config.respawnData().pos();
    player.teleport(new TeleportTransition((ServerLevel) player.level(), new Vec3(target), Vec3.ZERO, 0.0F, 0.0F, Relative.ROTATION, TeleportTransition.DO_NOTHING));
    player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
  }

  @Override
  public String getDescription(Player player) {
    return "Teleport back to your spawn location. Only works while in the same dimension.";
  }
}
