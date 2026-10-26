package com.avi.skram.ui;

import com.avi.skram.skills.SkillAcrobatics;
import com.avi.skram.skills.SkillAthletics;
import com.avi.skram.skills.SkillDigging;
import com.avi.skram.skills.SkillMining;
import com.avi.skram.skills.Skills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SkillScreen extends Screen {

  public SkillScreen() {
    super(Component.literal("SkillScreen"));
  }

  @Override
  protected void init() {
    super.init();

    int centerX = this.width / 2;
    int centerY = this.height / 2;

    this.addRenderableWidget(Button.builder(Component.literal("Close Screen"), button -> this.onClose()).bounds(centerX - 100, 20, 200, 20).build());

    SkillDigging digging = Minecraft.getInstance().player.getData(Skills.DIGGING);
    this.addRenderableWidget(new MultiLineTextWidget(
      centerX - 100,
      50,
      Component.literal(digging.toString()),
      this.font
    ));

    SkillMining mining = Minecraft.getInstance().player.getData(Skills.MINING);
    this.addRenderableWidget(new MultiLineTextWidget(
      centerX - 100,
      70,
      Component.literal(mining.toString()),
      this.font
    ));

    SkillAthletics athletics = Minecraft.getInstance().player.getData(Skills.ATHLETICS);
    this.addRenderableWidget(new MultiLineTextWidget(
      centerX - 100,
      90,
      Component.literal(athletics.toString()),
      this.font
    ));

    SkillAcrobatics acrobatics = Minecraft.getInstance().player.getData(Skills.ACROBATICS);
    this.addRenderableWidget(new MultiLineTextWidget(
      centerX - 100,
      110,
      Component.literal(acrobatics.toString()),
      this.font
    ));
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
