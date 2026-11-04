package com.avi.skram.network;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record BoatJumpPacket(int entityId) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<@NotNull BoatJumpPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Skram.MODID, "boat_jump_packet"));

  public static final StreamCodec<ByteBuf, BoatJumpPacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.INT,
    BoatJumpPacket::entityId,
    BoatJumpPacket::new
  );

  @Override
  public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
    return TYPE;
  }
}

