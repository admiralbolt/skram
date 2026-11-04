package com.avi.skram.attachments;

import com.avi.skram.Skram;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class MiscAttachments {

  public static Supplier<AttachmentType<PlayerProjectileModifier>> PLAYER_PROJECTILE_MODIFIER;
  public static Supplier<AttachmentType<SkramEntityModifier>> SKRAM_ENTITY_MODIFIER;

  public static void register() {
    PLAYER_PROJECTILE_MODIFIER = Skram.ATTACHMENT_TYPES.register(
      PlayerProjectileModifier.NAME, () -> AttachmentType.serializable(() -> new PlayerProjectileModifier())
        .sync(PlayerProjectileModifier.STREAM_CODEC)
        .build()
    );

    SKRAM_ENTITY_MODIFIER = Skram.ATTACHMENT_TYPES.register(
      SkramEntityModifier.NAME, () -> AttachmentType.serializable(() -> new SkramEntityModifier())
        .sync(SkramEntityModifier.STREAM_CODEC)
        .build()
    );
  }

}
