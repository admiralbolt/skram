package com.avi.skram.ui.tabs;

import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import com.avi.skram.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;

import java.util.ArrayList;
import java.util.List;

public final class SkillsTab {

  private SkillsTab() {}

  public static List<TabEntry> entries() {
    List<TabEntry> entries = new ArrayList<>();
    for (String skillName : Skills.SKILLS_BY_NAME.keySet().stream().sorted().toList()) {
      Skill skill = (Skill) Minecraft.getInstance().player.getData(Skills.SKILLS_BY_NAME.get(skillName));
      LinearLayout indexContent = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
      indexContent.spacing(4).defaultCellSetting().alignVerticallyMiddle();
      indexContent.addChild(new ResizableTextureImageWidget(
        0, 0, 16, 16,
        Skills.SKILL_ICONS.getOrDefault(skillName, Skills.DEFAULT_ICON),
        16, 16
      ));

      LinearLayout skillInfo = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
      skillInfo.spacing(2).defaultCellSetting();
      skillInfo.addChild(new MultiLineTextWidget(
        Component.literal(StringUtils.snakeToTitle(skillName) + " " + skill.level),
        Minecraft.getInstance().font
      ));
      int xpBarWidth = (int) MathUtils.linearRescale(
        skill.xp, Skill.getTotalXpForLevel(skill.level), Skill.getTotalXpForLevel(skill.level + 1), 0, 100
      );
      GridLayout xpBar = new GridLayout();
      xpBar.addChild(new ResizableTextureImageWidget(
        0, 0, 100, 3, Skill.YELLOW_BAR_BACKGROUND, 182, 5
      ), 0, 0);
      xpBar.addChild(new ResizableTextureImageWidget(
        0, 0, xpBarWidth, 3, Skill.YELLOW_BAR, 182, 5
      ), 0, 0);
      skillInfo.addChild(xpBar);
      indexContent.addChild(skillInfo);

      entries.add(new TabEntry(
        skillName,
        indexContent,
        145,
        32,
        skill::renderToLayout
      ));
    }

    return entries;
  }
}
