package com.avi.skram.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GenericMessageToast implements Toast {

  public static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/advancement");
  public static final float SCALE_SIZE = 0.7f;

  public final String toastName;
  public final ItemStack iconItem;
  public final String message;
  public final int displayTime;

  private Toast.Visibility wantedVisibility;

  public GenericMessageToast(String toastName, ItemStack iconItem, String message) {
    this(toastName, iconItem, message, 3000);
  }

  public GenericMessageToast(String toastName, ItemStack iconItem, String message, int displayTime) {
    this.toastName = toastName;
    this.iconItem = iconItem;
    this.message = message;
    this.displayTime = displayTime;
    this.wantedVisibility = Visibility.HIDE;


  }

  @Override
  public @NotNull Visibility getWantedVisibility() {
    return this.wantedVisibility;
  }

  @Override
  public void update(@NotNull ToastManager toastManager, long fullyVisibleForMs) {
    this.wantedVisibility = fullyVisibleForMs >= (this.displayTime * toastManager.getNotificationDisplayTimeMultiplier()) ? Visibility.HIDE : Visibility.SHOW;
  }

  @Override
  public void extractRenderState(GuiGraphicsExtractor graphics, @NotNull Font font, long fullyVisibleForMs) {
    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());

    graphics.pose().pushMatrix();
    graphics.pose().scale(0.7f);
    graphics.textWithWordWrap(font, FormattedText.of(this.message), (int) (30.0f / SCALE_SIZE), (int) (7.0f / SCALE_SIZE), (int) ((this.width() - 30.0f) / SCALE_SIZE), -1);
    graphics.pose().popMatrix();

    graphics.fakeItem(this.iconItem, 8, 8);
  }
}
