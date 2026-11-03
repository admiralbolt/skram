package com.avi.skram.ui.tabs;

import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;

public final class SkillsTab {

  private static final Identifier WHITE_BAR = Identifier.withDefaultNamespace("textures/gui/sprites/boss_bar/white_progress.png");

  private SkillsTab() {}

  public static Layout render() {
    LinearLayout content = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    content.spacing(2).defaultCellSetting().paddingTop(4);

    for (String skillName : Skills.SKILLS_BY_NAME.keySet().stream().sorted().toList()) {
      Skill skill = (Skill) Minecraft.getInstance().player.getData(Skills.SKILLS_BY_NAME.get(skillName));
      content.addChild(skill.renderToLayout());
      content.addChild(new ResizableTextureImageWidget(0, 0, 180, 3, WHITE_BAR, 182, 5));
    }

    return content;
  }
}
