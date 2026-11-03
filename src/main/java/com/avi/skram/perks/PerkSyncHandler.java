package com.avi.skram.perks;

import com.avi.skram.ui.XPGainGui;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class PerkSyncHandler<T extends Perk> implements AttachmentSyncHandler<T> {

  public StreamCodec<ByteBuf, T> codec;

  public PerkSyncHandler(StreamCodec<ByteBuf, T> codec) {
    this.codec = codec;
  }

  @Override
  public void write(@NotNull RegistryFriendlyByteBuf buf, @NotNull T attachment, boolean initialSync) {
    codec.encode(buf, attachment);
  }

  @Override
  public @Nullable T read(@NotNull IAttachmentHolder holder, @NotNull RegistryFriendlyByteBuf buf, @Nullable T previousValue) {
    T newValue = codec.decode(buf);
    if (previousValue == null) return newValue;

    Player player = (Player) holder;

    if (newValue.discovered != previousValue.discovered) {
      System.out.println("Discovered perk...");
    }

    if (newValue.researched != previousValue.researched) {
      newValue.applyModifiers(player);
    }

    if (newValue.enabled != previousValue.enabled) {
      newValue.applyModifiers(player);
    }

    return newValue;
  }

  @Override
  public boolean sendToPlayer(@NotNull IAttachmentHolder holder, @NotNull ServerPlayer to) {
    return holder == to;
  }
}
