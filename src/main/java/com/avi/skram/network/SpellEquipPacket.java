package com.avi.skram.network;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record SpellEquipPacket(String name) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<@NotNull SpellEquipPacket> TYPE =
    new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Skram.MODID, "spell_equip_packet"));

  public static final StreamCodec<ByteBuf, SpellEquipPacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.STRING_UTF8,
    SpellEquipPacket::name,
    SpellEquipPacket::new
  );

  @Override
  public @NotNull CustomPacketPayload.Type<? extends @NotNull CustomPacketPayload> type() {
    return TYPE;
  }
}
