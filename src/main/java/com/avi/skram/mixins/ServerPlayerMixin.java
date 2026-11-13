package com.avi.skram.mixins;

import com.avi.skram.network.CastingInfoFromClientPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {ServerPlayer.class})
public class ServerPlayerMixin {

  @Inject(
    method = {"teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getLevelData()Lnet/minecraft/world/level/storage/LevelData;", shift = At.Shift.AFTER)}
  )
  private void saveDataBeforeChangeDimension(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
    // This is extra annoying, because we need to get data from the client.
    // We send a packet to the client, that then sends a packet back to the server.
    ServerPlayer player = (ServerPlayer) (Object) this;
    PacketDistributor.sendToPlayer(player, new CastingInfoFromClientPacket());
    // DON'T DO THIS. We should wait on a server reply before continuing processing, but I'm LAZY.
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
