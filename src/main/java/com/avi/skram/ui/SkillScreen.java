package com.avi.skram.ui;

import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;

public class SkillScreen extends Screen {

  public HeaderAndFooterLayout layout;

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

    ScrollableLayout mainContent = actualMainContentLayout.addChild(new ScrollableLayout(this.minecraft, contentLayout, 200), 0, 0);

    this.layout.addToContents(actualMainContentLayout);

    this.layout.visitWidgets(this::addRenderableWidget);
    this.layout.arrangeElements();
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
}
