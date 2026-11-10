package com.avi.skram.network;

import com.avi.skram.Skram;
import com.avi.skram.magic.SpellCastingInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record CastingSyncServerPacket(SpellCastingInfo castingInfo) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<@NotNull CastingSyncServerPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Skram.MODID, "casting_sync_server_packet"));

  public static final StreamCodec<ByteBuf, CastingSyncServerPacket> STREAM_CODEC = StreamCodec.composite(
    SpellCastingInfo.STREAM_CODEC,
    CastingSyncServerPacket::castingInfo,
    CastingSyncServerPacket::new
  );

  @Override
  public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
    return TYPE;
  }
}

