package com.avi.skram.blocks;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// A data attachment for tracking player owned blocks, saved to the chunk.
// This will be a mapping of (x, y, z) -> data.
// The data itself is primarily the UUID of the player, but will also include some extra bits of info we want to store.
// This *might* end up being very memory expensive. If it ends up being a bottleneck, this can be reworked into two
// separate classes, one for PlayerPlacedBlocks (simply storing (x, y, z) -> bool), and one for richer data if needed.
public class PlayerOwnedBlocks implements ValueIOSerializable {

  public static StreamCodec<ByteBuf, PlayerOwnedBlocks> STREAM_CODEC = StreamCodec.ofMember(PlayerOwnedBlocks::encode, PlayerOwnedBlocks::new);

  public Map<BlockPos, PlayerOwnedData> playerOwnedBlocks;

  public PlayerOwnedBlocks() {
    this.playerOwnedBlocks = new HashMap<>();
  }

  public PlayerOwnedBlocks(ByteBuf buffer) {
    int totalSize = buffer.readInt();
    this.playerOwnedBlocks = new HashMap<>(totalSize);

    for (int i = 0; i < totalSize; ++i) {
      BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
      this.playerOwnedBlocks.put(pos, new PlayerOwnedData(buffer));
    }
  }

  public void encode(ByteBuf buffer) {
    // Write the total number of entries.
    buffer.writeInt(this.playerOwnedBlocks.size());

    // For each entry, we write in order: X -> Y -> Z -> PlayerData. We can use the existing encode/decode methods of
    // the PlayerData class for that object.
    for (Map.Entry<BlockPos, PlayerOwnedData> entry : this.playerOwnedBlocks.entrySet()) {
      BlockPos pos = entry.getKey();
      PlayerOwnedData data = entry.getValue();
      buffer.writeInt(pos.getX());
      buffer.writeInt(pos.getY());
      buffer.writeInt(pos.getZ());
      data.encode(buffer);
    }
  }

  @Override
  public void serialize(@NonNull ValueOutput output) {
    output.putInt("entryLength", this.playerOwnedBlocks.size());

    int i = 0;
    for (Map.Entry<BlockPos, PlayerOwnedData> entry : this.playerOwnedBlocks.entrySet()) {
      BlockPos pos = entry.getKey();
      PlayerOwnedData data = entry.getValue();
      output.putInt(String.format("x_%d", i), pos.getX());
      output.putInt(String.format("y_%d", i), pos.getY());
      output.putInt(String.format("z_%d", i), pos.getZ());

      // In this case we can't reuse PlayerOwnedData.serialize() because we need unique key names.
      output.putLong(String.format("upperBits_%d", i), data.uuid.getMostSignificantBits());
      output.putLong(String.format("lowerBits_%d", i), data.uuid.getLeastSignificantBits());
      output.putString(String.format("displayName_%d", i), data.displayName);
      output.putFloat(String.format("growthChance_%d", i), data.growthChance);
      output.putFloat(String.format("smithingSpeed_%d", i), data.smithingSpeed);
      i++;
    }
  }

  @Override
  public void deserialize(@NonNull ValueInput input) {
    int totalSize = input.getInt("entryLength").get();
    this.playerOwnedBlocks = new HashMap<>(totalSize);
    for (int i = 0; i < totalSize; ++i) {
      BlockPos pos = new BlockPos(
        input.getInt(String.format("x_%d", i)).get(),
        input.getInt(String.format("y_%d", i)).get(),
        input.getInt(String.format("z_%d", i)).get()
      );

      PlayerOwnedData data = new PlayerOwnedData();
      data.uuid = new UUID(input.getLong(String.format("upperBits_%d", i)).get(), input.getLong(String.format("lowerBits_%d", i)).get());
      data.displayName = input.getString(String.format("displayName_%d", i)).get();
      data.growthChance = input.getFloatOr(String.format("growthChance_%d", i), 0);
      data.smithingSpeed = input.getFloatOr(String.format("smithingSpeed_%d", i), 0);
      this.playerOwnedBlocks.put(pos, data);
    }
  }

}