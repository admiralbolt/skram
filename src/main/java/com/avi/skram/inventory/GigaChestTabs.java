package com.avi.skram.inventory;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class GigaChestTabs {
  public static final int SLOTS_PER_TAB = 207;
  private static final CreativeModeTab MISC_TAB = CreativeModeTab.builder()
    .title(Component.translatable("container.giga_chest.misc"))
    .icon(() -> new ItemStack(Items.CHEST))
    .displayItems((parameters, output) -> {
    })
    .build();
  public static final List<CreativeModeTab> TABS = createTabs();
  public static final int CONTAINER_SIZE = SLOTS_PER_TAB * TABS.size();

  private GigaChestTabs() {
  }

  private static List<CreativeModeTab> createTabs() {
    List<CreativeModeTab> tabs = new ArrayList<>();
    tabs.add(MISC_TAB);
    CreativeModeTabs.allTabs().stream()
      .filter(tab -> tab.getType() == CreativeModeTab.Type.CATEGORY && !tab.getDisplayName().getString().equals("Operator Utilities"))
      .forEach(tabs::add);
    return List.copyOf(tabs);
  }

  public static void rebuildDisplayItems(Player player) {
    FeatureFlagSet enabledFeatures = player.level().getServer().getWorldData().enabledFeatures();
    CreativeModeTab.ItemDisplayParameters parameters = new CreativeModeTab.ItemDisplayParameters(
      enabledFeatures,
      player.level().getServer().getPlayerList().isOp(player.nameAndId()),
      player.level().registryAccess()
    );
    TABS.subList(1, TABS.size()).forEach(tab -> tab.buildContents(parameters));
  }

  public static int findTabIndex(ItemStack stack) {
    for (int tabIndex = 1; tabIndex < TABS.size(); ++tabIndex) {
      for (ItemStack displayStack : TABS.get(tabIndex).getDisplayItems()) {
        if (ItemStack.isSameItemSameComponents(stack, displayStack)) return tabIndex;
      }
    }

    for (int tabIndex = 1; tabIndex < TABS.size(); ++tabIndex) {
      for (ItemStack displayStack : TABS.get(tabIndex).getDisplayItems()) {
        if (ItemStack.isSameItem(stack, displayStack)) return tabIndex;
      }
    }

    return 0;
  }
}
