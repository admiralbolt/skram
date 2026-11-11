package com.avi.skram.client.screens;

import com.avi.skram.Skram;
import com.avi.skram.inventory.MegaChestMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class MegaChestScreen extends AbstractContainerScreen<MegaChestMenu> {

  private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(
    Skram.MODID,
    "textures/gui/container/mega_chest.png"
  );
  private static final int BACKGROUND_WIDTH = 428;
  private static final int BACKGROUND_HEIGHT = 254;
  // The math here is really weird / opaque. I don't even know, but it works, so just leave it.
  public static final int IMAGE_HEIGHT_OFFSET = 40;

  public MegaChestScreen(MegaChestMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, Component.literal(""), BACKGROUND_WIDTH, BACKGROUND_HEIGHT + IMAGE_HEIGHT_OFFSET);
    this.titleLabelY = 0;
    this.inventoryLabelY = -100;
  }

  @Override
  protected void init() {
    super.init();
    this.addRenderableWidget(Button.builder(
      Component.translatable("container.mega_chest.sort"),
      button -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0)
    ).bounds(this.leftPos + BACKGROUND_WIDTH - 98, this.height - 50, 64, 16).build());
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
}
