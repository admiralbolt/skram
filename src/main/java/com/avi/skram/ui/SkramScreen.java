package com.avi.skram.ui;

import com.avi.skram.EventHandler;
import com.avi.skram.ui.tabs.MagicTab;
import com.avi.skram.ui.tabs.MiscTab;
import com.avi.skram.ui.tabs.PerksTab;
import com.avi.skram.ui.tabs.SkillsTab;
import com.avi.skram.utils.ReflectionUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

public class SkramScreen extends Screen {

  private static Tab selectedTab = Tab.SKILLS;
  private static final Map<Tab, Double> SCROLL_AMOUNTS = new EnumMap<>(Tab.class);
  private ScrollableLayout scrollableContent;

  public SkramScreen() {
    super(Component.literal("Skram"));
  }

  @Override
  protected void init() {
    this.saveScrollAmount();
    super.init();

    System.out.println("SkramScreen.init");

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

    GridLayout mainContent = new GridLayout();
    this.scrollableContent = new ScrollableLayout(this.minecraft, this.renderSelectedTab(), 200);
    mainContent.addChild(this.scrollableContent, 0, 0);
    layout.addToContents(mainContent);

    layout.visitWidgets(this::addRenderableWidget);
    layout.arrangeElements();
    this.setScrollAmount(SCROLL_AMOUNTS.getOrDefault(selectedTab, 0.0));
  }

  private Layout renderSelectedTab() {
    return switch (selectedTab) {
      case SKILLS -> SkillsTab.render();
      case PERKS -> PerksTab.render();
      case MAGIC -> MagicTab.render();
      case MISC -> MiscTab.render();
    };
  }

  private void selectTab(Tab tab) {
    if (selectedTab == tab) return;

    this.saveScrollAmount();
    selectedTab = tab;
    this.rebuildWidgets();
  }

  private void saveScrollAmount() {
    if (this.scrollableContent != null) {
      SCROLL_AMOUNTS.put(selectedTab, this.scrollableContent.getScrollAmount());
    }
  }

  private void setScrollAmount(double amount) {
    if (this.scrollableContent == null) return;

    try {
      Class<?> containerClass = Class.forName(ScrollableLayout.class.getName() + "$Container");
      java.lang.reflect.Method method = containerClass.getDeclaredMethod("setScrollAmount", double.class);
      method.setAccessible(true);

      Object container = ReflectionUtils.getFieldValue(this.scrollableContent, "container");
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
