package com.avi.skram.network;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record CastingInfoFromClientPacket() implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<@NotNull CastingInfoFromClientPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Skram.MODID, "casting_info_from_client_packet"));

  public static final StreamCodec<ByteBuf, CastingInfoFromClientPacket> STREAM_CODEC = StreamCodec.unit(new CastingInfoFromClientPacket());

  @Override
  public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
    return TYPE;
  }
}

