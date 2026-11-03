package com.avi.skram.ui.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class MiscTab {

  private MiscTab() {}

  public static Layout getLayout() {
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    layout.addChild(new MultiLineTextWidget(Component.literal("Miscellaneous content coming soon."), Minecraft.getInstance().font));
    return layout;
  }

  public static List<TabEntry> entries() {
    return List.of(new TabEntry(
      "misc",
      Component.literal("Miscellaneous"),
      Identifier.withDefaultNamespace("textures/item/book.png"),
      16,
      MiscTab::getLayout
    ));
  }
}
