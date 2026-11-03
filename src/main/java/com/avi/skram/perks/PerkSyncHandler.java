package com.avi.skram.perks;

import com.avi.skram.skills.Skills;
import com.avi.skram.ui.GenericMessageToast;
import com.avi.skram.ui.ToastUtils;
import com.avi.skram.ui.XPGainGui;
import com.avi.skram.utils.StringUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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

    if (newValue.discovered && !previousValue.discovered) {
      if (!ToastUtils.hasGenericMessageToast(Minecraft.getInstance().gui.toastManager(), newValue.name + ":discovered")) {
        Minecraft.getInstance().gui.toastManager().addToast(new GenericMessageToast(
          newValue.name + ":discovered",
          newValue.getIcon(),
          "Perk discovered: " + StringUtils.snakeToTitle(newValue.name)
        ));
      }
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
