package com.avi.skram.attachments;

import com.avi.skram.Skram;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class MiscAttachments {

  public static Supplier<AttachmentType<PlayerDoubleJumpTracker>> PLAYER_DOUBLE_JUMP_TRACKER;
  public static Supplier<AttachmentType<PlayerProjectileModifier>> PLAYER_PROJECTILE_MODIFIER;
  public static Supplier<AttachmentType<SkramEntityModifier>> SKRAM_ENTITY_MODIFIER;
  public static Supplier<AttachmentType<SpellItem>> SPELL_ITEM;

  public static void register() {

    PLAYER_DOUBLE_JUMP_TRACKER = Skram.ATTACHMENT_TYPES.register(
      PlayerDoubleJumpTracker.NAME, () -> AttachmentType.serializable(() -> new PlayerDoubleJumpTracker())
        .sync(PlayerDoubleJumpTracker.STREAM_CODEC)
        .build()
    );

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

    SPELL_ITEM = Skram.ATTACHMENT_TYPES.register(
      SpellItem.NAME, () -> AttachmentType.serializable(() -> new SpellItem())
        .sync(SpellItem.STREAM_CODEC)
        .build()
    );
  }

}
