package com.avi.skram.ui;

import com.avi.skram.EventHandler;
import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.ReflectionUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class SkillScreen extends Screen {

  public HeaderAndFooterLayout layout;
  public ScrollableLayout scrollableLayout;

  public static double scrollAmount = 0;

  public static Identifier WHITE_BAR = Identifier.withDefaultNamespace("textures/gui/sprites/boss_bar/white_progress.png");

  public SkillScreen() {
    super(Component.literal("SkillScreen"));
  }

  @Override
  protected void init() {
    super.init();

    this.layout = new HeaderAndFooterLayout(this, 20, 8);

    // Header!
    LinearLayout header = this.layout.addToHeader(new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL));
    header.spacing(1).defaultCellSetting().paddingTop(5);
    header.addChild(Button.builder(Component.literal("Close Screen"), button -> this.onClose()).build());

    // Main content.
    GridLayout actualMainContentLayout = new GridLayout();

    LinearLayout contentLayout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    contentLayout.spacing(1).defaultCellSetting().paddingTop(2);

    for (String skillName : Skills.SKILLS_BY_NAME.keySet().stream().sorted().toList()) {
      Skill skill = (Skill) Minecraft.getInstance().player.getData(Skills.SKILLS_BY_NAME.get(skillName));
      contentLayout.addChild(skill.renderToLayout());
      contentLayout.addChild(new ResizableTextureImageWidget(0, 0, 180, 3, WHITE_BAR, 182, 5));
    }

    this.scrollableLayout = new ScrollableLayout(this.minecraft, contentLayout, 200);

    actualMainContentLayout.addChild(this.scrollableLayout, 0, 0);

    this.layout.addToContents(actualMainContentLayout);
    this.layout.visitWidgets(this::addRenderableWidget);
    this.layout.arrangeElements();

    // Apparently we can't set scrollBar position without Reflection. That seems like an oversight.
    // The actual method is accessed through an inner private class.
    try {
      Class<?> containerClass = Class.forName(ScrollableLayout.class.getName() + "$Container");
      java.lang.reflect.Method method = containerClass.getDeclaredMethod("setScrollAmount", double.class);
      method.setAccessible(true);

      // Get the sub container defined in ScrollableLayout.
      Object container = ReflectionUtils.getFieldValue(this.scrollableLayout, "container");
      method.invoke(container, scrollAmount);
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
             NoSuchFieldException e) {
      System.out.println(e);
    }
  }

  @Override
  public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    // Submit things on the background stratum
    this.extractTransparentBackground(graphics);
  }

  @Override
  public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
    // Submit things before widgets

    // Then the widgets if this is a direct child of the Screen
    super.extractRenderState(graphics, mouseX, mouseY, a);

    // Submit things after widgets

    // Set the tooltip to be added above everything in this method
    // graphics.setTooltipForNextFrame(...);
  }

  @Override
  public void onClose() {
    scrollAmount = this.scrollableLayout.getScrollAmount();
    super.onClose();
  }

  @Override
  public boolean isPauseScreen() {
    return false;
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
