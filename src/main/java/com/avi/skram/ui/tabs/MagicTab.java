package com.avi.skram.ui.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;

import java.util.List;

public final class MagicTab {

  private MagicTab() {}

  public static Layout getLayout() {
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    layout.addChild(new MultiLineTextWidget(Component.literal("Magic coming soon."), Minecraft.getInstance().font));
    return layout;
  }

  public static List<TabEntry> entries() {
    LinearLayout indexContent = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
    indexContent.spacing(4).defaultCellSetting().alignVerticallyMiddle();
    indexContent.addChild(new ResizableTextureImageWidget(
      0, 0, 20, 20, Identifier.withDefaultNamespace("textures/item/enchanted_book.png"), 16, 16
    ));
    indexContent.addChild(new MultiLineTextWidget(Component.literal("Magic"), Minecraft.getInstance().font));
    return List.of(new TabEntry(
      "magic",
      indexContent,
      145,
      28,
      MagicTab::getLayout
    ));
  }
}
