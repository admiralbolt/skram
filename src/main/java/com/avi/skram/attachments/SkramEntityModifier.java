package com.avi.skram.attachments;

import com.avi.skram.blocks.PlayerOwnedData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.UUID;

public class SkramEntityModifier implements ValueIOSerializable {

  public static String NAME = "skram_entity_modifier";
  public static StreamCodec<ByteBuf, SkramEntityModifier> STREAM_CODEC = StreamCodec.ofMember(SkramEntityModifier::encode, SkramEntityModifier::new);

  public boolean canExplode = true;
  public long lastStealTime = 0;
  public UUID flankApplicator = PlayerOwnedData.EMPTY_UUID;

  public SkramEntityModifier() {

  }

  public SkramEntityModifier(ByteBuf buffer) {
    this.canExplode = buffer.readBoolean();
    this.lastStealTime = buffer.readLong();
    this.flankApplicator = new UUID(buffer.readLong(), buffer.readLong());
  }

  public void encode(ByteBuf buffer) {
    buffer.writeBoolean(this.canExplode);
    buffer.writeLong(this.lastStealTime);
    buffer.writeLong((this.flankApplicator == null) ? 0 : this.flankApplicator.getMostSignificantBits());
    buffer.writeLong((this.flankApplicator == null) ? 0 : this.flankApplicator.getLeastSignificantBits());
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putBoolean("canExplode", this.canExplode);
    output.putLong("lastStealTime", this.lastStealTime);
    output.putLong("upperBits", this.flankApplicator.getMostSignificantBits());
    output.putLong("lowerBits", this.flankApplicator.getLeastSignificantBits());
  }

  @Override
  public void deserialize(ValueInput input) {
    this.canExplode = input.getBooleanOr("canExplode", true);
    this.lastStealTime = input.getLongOr("lastStealTime", 0);
    this.flankApplicator = new UUID(input.getLong("upperBits").get(), input.getLong("lowerBits").get());
  }

}
