package com.avi.skram.mixins.client;

import com.avi.skram.magic.SkramMagic;
import com.avi.skram.network.CastingSyncServerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {Minecraft.class})
public class MinecraftMixin {

  @Inject(
    method = {"disconnectFromWorld(Lnet/minecraft/network/chat/Component;)V"},
    at = {@At(value = "HEAD")}
  )
  public void saveDataBeforeDisconnect(Component message, CallbackInfo ci) {
    // Persist current casting info to the server.
    LocalPlayer player = Minecraft.getInstance().player;
    if (player == null) return;

    ClientPacketDistributor.sendToServer(new CastingSyncServerPacket(player.getData(SkramMagic.SPELL_CASTING_INFO)));
    // DON'T DO THIS. We should wait on a server reply before continuing processing, but I'm LAZY.
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
