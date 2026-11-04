package com.avi.skram.attachments;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class SkramEntityModifier implements ValueIOSerializable {

  public static String NAME = "skram_entity_modifier";
  public static StreamCodec<ByteBuf, SkramEntityModifier> STREAM_CODEC = StreamCodec.ofMember(SkramEntityModifier::encode, SkramEntityModifier::new);

  public boolean canExplode = true;
  public long lastStealTime = 0;

  public SkramEntityModifier() {

  }

  public SkramEntityModifier(ByteBuf buffer) {
    this.canExplode = buffer.readBoolean();
    this.lastStealTime = buffer.readLong();
  }

  public void encode(ByteBuf buffer) {
    buffer.writeBoolean(this.canExplode);
    buffer.writeLong(this.lastStealTime);
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putBoolean("canExplode", this.canExplode);
    output.putLong("lastStealTime", this.lastStealTime);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.canExplode = input.getBooleanOr("canExplode", true);
    this.lastStealTime = input.getLongOr("lastStealTime", 0);
  }

}
