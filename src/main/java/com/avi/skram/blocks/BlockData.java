package com.avi.skram.blocks;

import com.avi.skram.Skram;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class BlockData {

  public static Supplier<AttachmentType<PlayerOwnedBlock>> PLAYER_OWNED;

  public static void register() {
    PLAYER_OWNED = Skram.ATTACHMENT_TYPES.register(
      "player_placed", () -> AttachmentType.serializable(() -> new PlayerOwnedBlock())
        .sync(PlayerOwnedBlock.STREAM_CODEC)
        .build()
    );
  }
}
