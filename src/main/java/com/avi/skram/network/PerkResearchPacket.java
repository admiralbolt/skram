package com.avi.skram.network;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record PerkResearchPacket(String name) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<@NotNull PerkResearchPacket> TYPE =
    new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Skram.MODID, "perk_research_packet"));

  public static final StreamCodec<ByteBuf, PerkResearchPacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.STRING_UTF8,
    PerkResearchPacket::name,
    PerkResearchPacket::new
  );

  @Override
  public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
    return TYPE;
  }
}
