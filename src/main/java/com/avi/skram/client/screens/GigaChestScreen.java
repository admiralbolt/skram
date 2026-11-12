package com.avi.skram.client.screens;

import com.avi.skram.Skram;
import com.avi.skram.inventory.GigaChestMenu;
import com.avi.skram.inventory.GigaChestTabs;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;

public class GigaChestScreen extends AbstractContainerScreen<GigaChestMenu> {

  private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(
    Skram.MODID,
    "textures/gui/container/mega_chest.png"
  );
  private static final int BACKGROUND_WIDTH = 428;
  private static final int BACKGROUND_HEIGHT = 254;
  private static final int IMAGE_HEIGHT_OFFSET = 40;
  private static final int TAB_BUTTON_SIZE = 20;
  private static final int TAB_BUTTON_SPACING = 2;
  private static final int TAB_BUTTONS_PER_ROW = 5;

  public GigaChestScreen(GigaChestMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, Component.literal(""), BACKGROUND_WIDTH, BACKGROUND_HEIGHT + IMAGE_HEIGHT_OFFSET);
    this.titleLabelY = 0;
    this.inventoryLabelY = -100;
  }

  @Override
  protected void init() {
    super.init();
    int tabCount = GigaChestTabs.TABS.size();
    int rows = (tabCount + TAB_BUTTONS_PER_ROW - 1) / TAB_BUTTONS_PER_ROW;
    int firstTabButtonY = this.height - 80;

    for (int tabIndex = 0; tabIndex < tabCount; ++tabIndex) {
      int selectedTab = tabIndex;
      int column = tabIndex % TAB_BUTTONS_PER_ROW;
      int row = tabIndex / TAB_BUTTONS_PER_ROW;
      this.addRenderableWidget(new GigaChestTabButton(
        this.leftPos + 12 + column * (TAB_BUTTON_SIZE + TAB_BUTTON_SPACING),
        firstTabButtonY + row * (TAB_BUTTON_SIZE + TAB_BUTTON_SPACING),
        TAB_BUTTON_SIZE,
        GigaChestTabs.TABS.get(tabIndex),
        button -> this.selectTab(selectedTab),
        () -> this.menu.getSelectedTab() == selectedTab
      ));
    }

    this.addRenderableWidget(Button.builder(
      Component.translatable("container.giga_chest.sort"),
      button -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, this.menu.getSortButtonId())
    ).bounds(this.leftPos + BACKGROUND_WIDTH - 98, this.height - 50, 64, 16).build());
  }

  private void selectTab(int tabIndex) {
    this.menu.setSelectedTab(tabIndex);
    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, tabIndex);
  }

  @Override
  public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    super.extractBackground(graphics, mouseX, mouseY, partialTick);
    graphics.blit(
      RenderPipelines.GUI_TEXTURED,
      BACKGROUND,
      this.leftPos,
      this.topPos + (IMAGE_HEIGHT_OFFSET / 2),
      0.0F,
      0.0F,
      BACKGROUND_WIDTH,
      BACKGROUND_HEIGHT,
      512,
      512
    );
  }

  private static class GigaChestTabButton extends Button {
    private final CreativeModeTab tab;

    private GigaChestTabButton(int x, int y, int size, CreativeModeTab tab, OnPress onPress, java.util.function.BooleanSupplier selected) {
      super(x, y, size, size, tab.getDisplayName(), onPress, DEFAULT_NARRATION);
      this.tab = tab;
      this.setTooltip(Tooltip.create(tab.getDisplayName()));
      this.setOverrideRenderHighlightedSprite(() -> selected.getAsBoolean() || this.isHoveredOrFocused());
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
      this.extractDefaultSprite(graphics);
      int iconOffset = Math.max(0, (this.getWidth() - 16) / 2);
      graphics.item(this.tab.getIconItem(), this.getX() + iconOffset, this.getY() + iconOffset);
    }
  }
}
