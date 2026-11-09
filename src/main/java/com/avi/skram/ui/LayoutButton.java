package com.avi.skram.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class LayoutButton extends AbstractWidget {

  private static final int PADDING = 3;

  private final Layout content;
  private final Runnable onPress;
  private final boolean selected;

  public LayoutButton(int width, int height, Layout content, boolean selected, Runnable onPress, Component narration) {
    super(0, 0, width, height, narration);
    this.content = content;
    this.selected = selected;
    this.onPress = onPress;
    this.arrangeContent();
  }

  @Override
  protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    if (this.selected) {
      graphics.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFFFFFFFF);
    }

    this.content.visitWidgets(widget -> widget.extractRenderState(graphics, mouseX, mouseY, partialTick));
  }

  @Override
  public void onClick(MouseButtonEvent event, boolean doubleClick) {
    this.onPress.run();
  }

  @Override
  public void setX(int x) {
    super.setX(x);
    this.arrangeContent();
  }

  @Override
  public void setY(int y) {
    super.setY(y);
    this.arrangeContent();
  }

  @Override
  public void setSize(int width, int height) {
    super.setSize(width, height);
    this.arrangeContent();
  }

  private void arrangeContent() {
    this.content.arrangeElements();
    int verticalOffset = Math.max(0, (this.getHeight() - 2 * PADDING - this.content.getHeight()) / 2);
    this.content.setPosition(this.getX() + PADDING, this.getY() + PADDING + verticalOffset);
    this.content.arrangeElements();
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput output) {
    this.defaultButtonNarrationText(output);
  }

}
