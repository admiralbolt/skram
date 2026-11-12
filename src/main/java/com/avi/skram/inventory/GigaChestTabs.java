package com.avi.skram.inventory;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class GigaChestTabs {
  public static final int SLOTS_PER_TAB = 207;
  public static final List<CreativeModeTab> TABS = CreativeModeTabs.allTabs().stream()
    .filter(tab -> tab.getType() == CreativeModeTab.Type.CATEGORY && !tab.getDisplayName().getString().equals("Operator Utilities"))
    .toList();
  public static final int CONTAINER_SIZE = SLOTS_PER_TAB * TABS.size();

  private GigaChestTabs() {
  }

  public static void rebuildDisplayItems(Player player) {
    FeatureFlagSet enabledFeatures = player.level().getServer().getWorldData().enabledFeatures();
    CreativeModeTab.ItemDisplayParameters parameters = new CreativeModeTab.ItemDisplayParameters(
      enabledFeatures,
      player.level().getServer().getPlayerList().isOp(player.nameAndId()),
      player.level().registryAccess()
    );
    TABS.forEach(tab -> tab.buildContents(parameters));
  }

  public static int findTabIndex(ItemStack stack, int fallbackIndex) {
    for (int tabIndex = 0; tabIndex < TABS.size(); ++tabIndex) {
      for (ItemStack displayStack : TABS.get(tabIndex).getDisplayItems()) {
        if (ItemStack.isSameItemSameComponents(stack, displayStack)) return tabIndex;
      }
    }

    for (int tabIndex = 0; tabIndex < TABS.size(); ++tabIndex) {
      for (ItemStack displayStack : TABS.get(tabIndex).getDisplayItems()) {
        if (ItemStack.isSameItem(stack, displayStack)) return tabIndex;
      }
    }

    return fallbackIndex;
  }
}
