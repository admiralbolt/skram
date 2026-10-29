package com.avi.skram.blocks;

import com.avi.skram.Skram;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class BlockData {

  public static Supplier<AttachmentType<PlayerOwnedBlocks>> PLAYER_OWNED;

  public static void register() {
    PLAYER_OWNED = Skram.ATTACHMENT_TYPES.register(
      "player_owned_blocks", () -> AttachmentType.serializable(() -> new PlayerOwnedBlocks())
        .sync(PlayerOwnedBlocks.STREAM_CODEC)
        .build()
    );
  }

}
