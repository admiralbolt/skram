package com.avi.skram.skills;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SkillPacket(String name, int level, double xp, boolean enabled) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<SkillPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Skram.MODID, "skill_packet"));

  public static final StreamCodec<ByteBuf, SkillPacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.STRING_UTF8,
    SkillPacket::name,
    ByteBufCodecs.VAR_INT,
    SkillPacket::level,
    ByteBufCodecs.DOUBLE,
    SkillPacket::xp,
    ByteBufCodecs.BOOL,
    SkillPacket::enabled,
    SkillPacket::new
  );

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
