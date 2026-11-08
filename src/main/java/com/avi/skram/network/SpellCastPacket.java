package com.avi.skram.network;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record SpellCastPacket(String name, Vec3 targetBlock) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<@NotNull SpellCastPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Skram.MODID, "spell_cast_packet"));

  public static final StreamCodec<ByteBuf, SpellCastPacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.STRING_UTF8,
    SpellCastPacket::name,
    ByteBufCodecs.fromCodec(Vec3.CODEC),
    SpellCastPacket::targetBlock,
    SpellCastPacket::new
  );

  @Override
  public @NotNull CustomPacketPayload.Type<? extends @NotNull CustomPacketPayload> type() {
    return TYPE;
  }

}