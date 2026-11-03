package com.avi.skram.network;

import com.avi.skram.Skram;
import com.avi.skram.utils.CodecUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record PerkPacket(String name, boolean discovered, boolean researched, boolean enabled) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<@NotNull PerkPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Skram.MODID, "perk_packet"));

  public static final StreamCodec<ByteBuf, PerkPacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.STRING_UTF8,
    PerkPacket::name,
    ByteBufCodecs.BOOL,
    PerkPacket::discovered,
    ByteBufCodecs.BOOL,
    PerkPacket::researched,
    ByteBufCodecs.BOOL,
    PerkPacket::enabled,
    PerkPacket::new
  );

  @Override
  public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
    return TYPE;
  }
}
