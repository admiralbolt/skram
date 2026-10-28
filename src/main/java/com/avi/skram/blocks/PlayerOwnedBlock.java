package com.avi.skram.blocks;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.fml.startup.Server;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class PlayerOwnedBlock implements ValueIOSerializable {

  public static StreamCodec<ByteBuf, PlayerOwnedBlock> STREAM_CODEC = StreamCodec.ofMember(PlayerOwnedBlock::encode, PlayerOwnedBlock::new);

  public UUID uuid = null;

  public static PlayerOwnedBlock fromPlayer(Player player) {
    PlayerOwnedBlock ppb = new PlayerOwnedBlock();
    ppb.uuid = player.getUUID();
    return ppb;
  }

  public PlayerOwnedBlock() {

  }

  public PlayerOwnedBlock(ByteBuf buffer) {
    this.uuid = new UUID(buffer.readLong(), buffer.readLong());
  }

  public void encode(ByteBuf buffer) {
    buffer.writeLong(this.uuid.getMostSignificantBits());
    buffer.writeLong(this.uuid.getLeastSignificantBits());
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putLong("upperBits", this.uuid.getMostSignificantBits());
    output.putLong("lowerBits", this.uuid.getLeastSignificantBits());
  }

  @Override
  public void deserialize(ValueInput input) {
    this.uuid = new UUID(input.getLong("upperBits").get(), input.getLong("lowerBits").get());
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