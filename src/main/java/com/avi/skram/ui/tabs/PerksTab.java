package com.avi.skram.ui.tabs;

import com.avi.skram.perks.Perk;
import com.avi.skram.perks.Perks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;

import java.util.ArrayList;
import java.util.List;

public final class PerksTab {

  private PerksTab() {}

  public static List<TabEntry> entries(List<ItemStack> inventoryResources) {
    List<TabEntry> entries = new ArrayList<>();
    for (String perkName : Perks.PERKS_BY_NAME.keySet().stream().sorted().toList()) {
      Perk perk = (Perk) Minecraft.getInstance().player.getData(Perks.PERKS_BY_NAME.get(perkName));
      LinearLayout indexContent = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
      indexContent.spacing(4).defaultCellSetting().alignVerticallyMiddle();
      indexContent.addChild(new ResizableTextureImageWidget(0, 0, 22, 22, perk.getIcon(), 32, 32));
      indexContent.addChild(new MultiLineTextWidget(perk.getDisplayName(), Minecraft.getInstance().font));
      entries.add(new TabEntry(
        perkName,
        indexContent,
        145,
        28,
        () -> perk.renderToLayout(Minecraft.getInstance().player, inventoryResources)
      ));
    }

    return entries;
  }

}
