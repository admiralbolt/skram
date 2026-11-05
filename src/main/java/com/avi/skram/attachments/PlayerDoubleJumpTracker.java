package com.avi.skram.attachments;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class PlayerDoubleJumpTracker implements ValueIOSerializable {

  public static String NAME = "player_double_jump_tracker";
  public static StreamCodec<ByteBuf, PlayerDoubleJumpTracker> STREAM_CODEC = StreamCodec.ofMember(PlayerDoubleJumpTracker::encode, PlayerDoubleJumpTracker::new);

  public boolean jumpAvailable = true;

  public PlayerDoubleJumpTracker() {

  }

  public PlayerDoubleJumpTracker(boolean jumpAvailable) {
    this.jumpAvailable = jumpAvailable;
  }

  public PlayerDoubleJumpTracker(ByteBuf buffer) {
    this.jumpAvailable = buffer.readBoolean();
  }

  public void encode(ByteBuf buffer) {
    buffer.writeBoolean(this.jumpAvailable);
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putBoolean("jumpAvailable", this.jumpAvailable);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.jumpAvailable = input.getBooleanOr("jumpAvailable", true);
  }


}
