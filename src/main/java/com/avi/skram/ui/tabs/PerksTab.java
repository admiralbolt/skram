package com.avi.skram.ui.tabs;

import com.avi.skram.perks.Perk;
import com.avi.skram.perks.Perks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class PerksTab {

  private PerksTab() {}

  public static List<TabEntry> entries(List<ItemStack> inventoryResources) {
    List<TabEntry> entries = new ArrayList<>();
    for (String perkName : Perks.PERKS_BY_NAME.keySet().stream().sorted().toList()) {
      Perk perk = (Perk) Minecraft.getInstance().player.getData(Perks.PERKS_BY_NAME.get(perkName));
      entries.add(new TabEntry(
        perkName,
        perk.getDisplayName(),
        perk.getIcon(),
        16,
        () -> perk.renderToLayout(Minecraft.getInstance().player, inventoryResources)
      ));
    }

    return entries;
  }

}
