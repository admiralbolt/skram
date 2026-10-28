package com.avi.skram.blocks;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.nio.charset.Charset;
import java.util.UUID;

public class PlayerOwnedBlock implements ValueIOSerializable {

  public static StreamCodec<ByteBuf, PlayerOwnedBlock> STREAM_CODEC = StreamCodec.ofMember(PlayerOwnedBlock::encode, PlayerOwnedBlock::new);

  public UUID uuid = null;
  public String displayName = "";

  public static PlayerOwnedBlock fromPlayer(Player player) {
    PlayerOwnedBlock ppb = new PlayerOwnedBlock();
    ppb.uuid = player.getUUID();
    ppb.displayName = player.getDisplayName().getString();
    return ppb;
  }

  public PlayerOwnedBlock() {

  }

  public PlayerOwnedBlock(ByteBuf buffer) {
    this.uuid = new UUID(buffer.readLong(), buffer.readLong());
    this.displayName = buffer.readString(buffer.readInt(), Charset.defaultCharset());
  }

  public void encode(ByteBuf buffer) {
    if (this.uuid == null) return;

    buffer.writeLong(this.uuid.getMostSignificantBits());
    buffer.writeLong(this.uuid.getLeastSignificantBits());
    buffer.writeInt(this.displayName.length());
    buffer.writeCharSequence(this.displayName, Charset.defaultCharset());
  }

  @Override
  public void serialize(ValueOutput output) {
    if (this.uuid == null) return;

    output.putLong("upperBits", this.uuid.getMostSignificantBits());
    output.putLong("lowerBits", this.uuid.getLeastSignificantBits());
    output.putString("displayName", this.displayName);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.uuid = new UUID(input.getLong("upperBits").get(), input.getLong("lowerBits").get());
    this.displayName = input.getString("displayName").get();
  }

  public Player getOwner(ServerLevel level) {
    if (this.uuid == null) return null;

    return level.getServer().getPlayerList().getPlayer(this.uuid);
  }

  // This overload exists for when we don't have a level context.
  // This _WILL_ throw an error if it is run on the client side.
  // User discretion is advised.
  public Player getOwner() {
    if (this.uuid == null) return null;

    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    if (server == null) return null;

    return server.getPlayerList().getPlayer(this.uuid);
  }

}