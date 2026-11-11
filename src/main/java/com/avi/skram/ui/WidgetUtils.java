package com.avi.skram.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.network.chat.Component;

public class WidgetUtils {

  public static MultiLineTextWidget textWidget(String text, int maxWidth) {
    MultiLineTextWidget widget = new MultiLineTextWidget(Component.literal(text), Minecraft.getInstance().font);
    widget.setMaxWidth(maxWidth);
    return widget;
  }

  public static MultiLineTextWidget textWidget180(String text) {
    return textWidget(text, 180);
  }

}
