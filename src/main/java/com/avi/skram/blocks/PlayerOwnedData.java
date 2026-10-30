package com.avi.skram.blocks;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.nio.charset.Charset;
import java.util.UUID;

public class PlayerOwnedData implements ValueIOSerializable {

  public static UUID EMPTY_UUID = new UUID(0, 0);
  // The UUID, used to look up the player. We use "0" as a way to
  public UUID uuid = EMPTY_UUID;

  // All other fields are fallbacks in case we can't look up the player.
  public String displayName = "";
  public float growthChance = 0;
  public float smithingSpeed = 0;

  public PlayerOwnedData() {

  }

  public PlayerOwnedData(ByteBuf buffer) {
    this.uuid = new UUID(buffer.readLong(), buffer.readLong());
    this.displayName = buffer.readString(buffer.readInt(), Charset.defaultCharset());
    this.growthChance = buffer.readFloat();
    this.smithingSpeed = buffer.readFloat();
  }

  public void encode(ByteBuf buffer) {
    buffer.writeLong((this.uuid == null) ? 0 : this.uuid.getMostSignificantBits());
    buffer.writeLong((this.uuid == null) ? 0 : this.uuid.getLeastSignificantBits());
    buffer.writeInt(this.displayName.length());
    buffer.writeCharSequence(this.displayName, Charset.defaultCharset());
    buffer.writeFloat(this.growthChance);
    buffer.writeFloat(this.smithingSpeed);
  }

  @Override
  public void serialize(ValueOutput output) {
    // If you update this, make sure you update the PlayerOwnedBlocks.serialize() method.
    output.putLong("upperBits", this.uuid.getMostSignificantBits());
    output.putLong("lowerBits", this.uuid.getLeastSignificantBits());
    output.putString("displayName", this.displayName);
    output.putFloat("growthChance", this.growthChance);
    output.putFloat("smithingSpeed", this.smithingSpeed);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.uuid = new UUID(input.getLong("upperBits").get(), input.getLong("lowerBits").get());
    this.displayName = input.getString("displayName").get();
    this.growthChance = input.getFloatOr("growthChance", 0);
    this.smithingSpeed = input.getFloatOr("smithingSpeed", 0);
  }

  public Player getOwner(ServerLevel level) {
    if (this.uuid.equals(EMPTY_UUID)) return null;

    return level.getServer().getPlayerList().getPlayer(this.uuid);
  }

  // This overload exists for when we don't have a level context.
  // This _WILL_ throw an error if it is run on the client side.
  // User discretion is advised.
  public Player getOwner() {
    if (this.uuid.equals(EMPTY_UUID)) return null;

    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    if (server == null) return null;

    return server.getPlayerList().getPlayer(this.uuid);
  }
}
