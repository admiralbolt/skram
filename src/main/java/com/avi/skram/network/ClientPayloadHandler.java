package com.avi.skram.network;

import com.avi.skram.magic.SkramMagic;
import com.avi.skram.perks.Perk;
import com.avi.skram.perks.Perks;
import com.avi.skram.ui.SkramScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

  public static void handlePerkResearchPacket(final PerkResearchPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      var perkAttachment = Perks.PERKS_BY_NAME.get(packet.name());
      if (perkAttachment == null) return;

      Perk perk = (Perk) context.player().getData(perkAttachment);
      perk.researched = true;

      if (Minecraft.getInstance().gui.screen() instanceof SkramScreen screen) {
        screen.init(screen.width, screen.height);
      }
    });
  }

  public static void handleGetCastingInfoFromClient(final CastingInfoFromClientPacket packet, final IPayloadContext context) {
    ClientPacketDistributor.sendToServer(new CastingSyncServerPacket(context.player().getData(SkramMagic.SPELL_CASTING_INFO)));
  }

}
