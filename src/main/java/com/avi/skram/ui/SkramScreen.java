package com.avi.skram.ui;

import com.avi.skram.EventHandler;
import com.avi.skram.ui.tabs.MagicTab;
import com.avi.skram.ui.tabs.MiscTab;
import com.avi.skram.ui.tabs.PerksTab;
import com.avi.skram.ui.tabs.SkillsTab;
import com.avi.skram.ui.tabs.TabEntry;
import com.avi.skram.utils.ReflectionUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SkramScreen extends Screen {

  private static Tab selectedTab = Tab.SKILLS;
  private static final Map<Tab, Double> INDEX_SCROLL_AMOUNTS = new EnumMap<>(Tab.class);
  private static final Map<Tab, Double> DETAILS_SCROLL_AMOUNTS = new EnumMap<>(Tab.class);
  private static final Map<Tab, String> SELECTED_ENTRIES = new EnumMap<>(Tab.class);
  private ScrollableLayout indexContent;
  private ScrollableLayout detailsContent;
  private List<ItemStack> inventoryResources = List.of();

  public SkramScreen() {
    super(Component.literal("Skram"));
  }

  @Override
  protected void init() {
    this.saveScrollAmount();
    super.init();
    this.inventoryResources = this.gatherInventoryResources();

    HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 10, 8);

    LinearLayout header = layout.addToHeader(new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL));
    header.spacing(4).defaultCellSetting().paddingTop(4);
    for (Tab tab : Tab.values()) {
      header.addChild(Button.builder(
        Component.literal(tab == selectedTab ? "[" + tab.label + "]" : tab.label),
        button -> this.selectTab(tab)
      ).size(55, 20).build());
    }
    header.addChild(Button.builder(Component.literal("Close Screen"), button -> this.onClose()).size(80, 20).build());

    List<TabEntry> entries = this.getSelectedTabEntries();
    String selectedEntry = SELECTED_ENTRIES.get(selectedTab);
    String finalSelectedEntry = selectedEntry;
    if (selectedEntry == null || entries.stream().noneMatch(entry -> entry.key().equals(finalSelectedEntry))) {
      selectedEntry = entries.isEmpty() ? null : entries.getFirst().key();
      if (selectedEntry != null) SELECTED_ENTRIES.put(selectedTab, selectedEntry);
    }
    String selectedEntryKey = selectedEntry;

    LinearLayout index = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    index.spacing(2).defaultCellSetting().paddingTop(4);
    for (TabEntry entry : entries) {
      LinearLayout row = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
      row.spacing(3).defaultCellSetting().alignVerticallyMiddle();
      if (entry.icon() != null) {
        row.addChild(new ResizableTextureImageWidget(
          0, 0, 16, 16, entry.icon(), entry.iconTextureSize(), entry.iconTextureSize()
        ));
      }

      Component label = entry.key().equals(selectedEntryKey)
        ? Component.literal("> ").append(entry.label())
        : entry.label();
      row.addChild(Button.builder(label, button -> this.selectEntry(entry.key()))
        .size(112, 20)
        .build());
      index.addChild(row);
    }

    this.indexContent = new ScrollableLayout(this.minecraft, index, 200);
    this.indexContent.setMinWidth(140);
    this.indexContent.setMinHeight(200);
    this.indexContent.setMaxHeight(200);

    LinearLayout details = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    details.spacing(2).defaultCellSetting().paddingTop(4);
    if (selectedEntryKey != null) {
      TabEntry entry = entries.stream().filter(candidate -> candidate.key().equals(selectedEntryKey)).findFirst().orElseThrow();
      details.addChild(entry.details().get());
    }
    this.detailsContent = new ScrollableLayout(this.minecraft, details, 200);
    this.detailsContent.setMinWidth(200);
    this.detailsContent.setMinHeight(200);
    this.detailsContent.setMaxHeight(200);

    GridLayout mainContent = new GridLayout().columnSpacing(6);
    mainContent.defaultCellSetting().alignHorizontallyLeft();
    mainContent.addChild(this.indexContent, 0, 0);
    mainContent.addChild(this.detailsContent, 0, 1);
    layout.addToContents(mainContent);

    layout.arrangeElements();
    layout.visitWidgets(this::addRenderableWidget);
    this.setScrollAmount(this.indexContent, INDEX_SCROLL_AMOUNTS.getOrDefault(selectedTab, 0.0));
    this.setScrollAmount(this.detailsContent, DETAILS_SCROLL_AMOUNTS.getOrDefault(selectedTab, 0.0));
  }

  private List<TabEntry> getSelectedTabEntries() {
    return switch (selectedTab) {
      case SKILLS -> SkillsTab.entries();
      case PERKS -> PerksTab.entries(this.inventoryResources);
      case MAGIC -> MagicTab.entries();
      case MISC -> MiscTab.entries();
    };
  }

  private List<ItemStack> gatherInventoryResources() {
    List<ItemStack> resources = new ArrayList<>();
    var inventory = this.minecraft.player.getInventory();
    for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
      ItemStack item = inventory.getItem(slot);
      if (item.isEmpty()) continue;

      ItemStack matchingResource = resources.stream()
        .filter(resource -> ItemStack.isSameItemSameComponents(resource, item))
        .findFirst()
        .orElse(null);
      if (matchingResource == null) {
        resources.add(item.copy());
      } else {
        matchingResource.grow(item.getCount());
      }
    }

    return resources;
  }

  private void selectTab(Tab tab) {
    if (selectedTab == tab) return;

    this.saveScrollAmount();
    selectedTab = tab;
    this.indexContent = null;
    this.detailsContent = null;
    this.rebuildWidgets();
  }

  private void selectEntry(String entryKey) {
    if (entryKey.equals(SELECTED_ENTRIES.get(selectedTab))) return;

    this.saveScrollAmount();
    SELECTED_ENTRIES.put(selectedTab, entryKey);
    DETAILS_SCROLL_AMOUNTS.put(selectedTab, 0.0);
    this.detailsContent = null;
    this.rebuildWidgets();
  }

  private void saveScrollAmount() {
    if (this.indexContent != null) {
      INDEX_SCROLL_AMOUNTS.put(selectedTab, this.indexContent.getScrollAmount());
    }
    if (this.detailsContent != null) {
      DETAILS_SCROLL_AMOUNTS.put(selectedTab, this.detailsContent.getScrollAmount());
    }
  }

  private void setScrollAmount(ScrollableLayout scrollableLayout, double amount) {
    if (scrollableLayout == null) return;

    try {
      Class<?> containerClass = Class.forName(ScrollableLayout.class.getName() + "$Container");
      java.lang.reflect.Method method = containerClass.getDeclaredMethod("setScrollAmount", double.class);
      method.setAccessible(true);

      Object container = ReflectionUtils.getFieldValue(scrollableLayout, "container");
      method.invoke(container, amount);
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
             NoSuchFieldException e) {
      System.out.println(e);
    }
  }

  @Override
  public void onClose() {
    this.saveScrollAmount();
    super.onClose();
  }

  @Override
  public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    this.extractTransparentBackground(graphics);
    graphics.fill(0, 0, this.width, this.height, 0x80000000);
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }

  private enum Tab {
    SKILLS("Skills"),
    PERKS("Perks"),
    MAGIC("Magic"),
    MISC("Misc");

    private final String label;

    Tab(String label) {
      this.label = label;
    }
  }

  @Override
  public boolean keyPressed(@NotNull KeyEvent event) {
    if (EventHandler.OPEN_SKILLS_UI.get().isActiveAndMatches(InputConstants.getKey(event))) {
      this.onClose();
      return true;
    }

    return super.keyPressed(event);
  }
}
