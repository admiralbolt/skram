package com.avi.skram.skills;

import com.avi.skram.Skram;
import com.avi.skram.utils.CodecUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record SkillPacket(String name, int level, double xp, boolean[] enabledAbilities) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<@NotNull SkillPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Skram.MODID, "skill_packet"));

  public static final StreamCodec<ByteBuf, SkillPacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.STRING_UTF8,
    SkillPacket::name,
    ByteBufCodecs.VAR_INT,
    SkillPacket::level,
    ByteBufCodecs.DOUBLE,
    SkillPacket::xp,
    CodecUtils.BOOLEAN_ARRAY_STREAM_CODEC,
    SkillPacket::enabledAbilities,
    SkillPacket::new
  );

  @Override
  public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
    return TYPE;
  }
}
