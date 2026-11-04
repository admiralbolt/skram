package com.avi.skram.attachments;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class PlayerProjectileModifier implements ValueIOSerializable {

  public static String NAME = "player_projectile_modifier";
  public static StreamCodec<ByteBuf, PlayerProjectileModifier> STREAM_CODEC = StreamCodec.ofMember(PlayerProjectileModifier::encode, PlayerProjectileModifier::new);

  public double areaMultiplier = 1.0;

  public PlayerProjectileModifier() {

  }

  public PlayerProjectileModifier(double areaMultiplier) {
    this.areaMultiplier = areaMultiplier;
  }

  public PlayerProjectileModifier(ByteBuf buffer) {
    this.areaMultiplier = buffer.readDouble();
  }

  public void encode(ByteBuf buffer) {
    buffer.writeDouble(this.areaMultiplier);
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putDouble("areaMultiplier", areaMultiplier);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.areaMultiplier = input.getDoubleOr("areaMultiplier", 1.0);
  }


}
