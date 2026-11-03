package com.avi.skram.ui.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

public final class MiscTab {

  private MiscTab() {}

  public static Layout render() {
    LinearLayout content = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    content.addChild(new MultiLineTextWidget(Component.literal("Miscellaneous content coming soon."), Minecraft.getInstance().font));
    return content;
  }
}
