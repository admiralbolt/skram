package com.avi.skram.ui.tabs;

import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;

import java.util.ArrayList;
import java.util.List;

public final class SkillsTab {

  private SkillsTab() {}

  public static List<TabEntry> entries() {
    List<TabEntry> entries = new ArrayList<>();
    for (String skillName : Skills.SKILLS_BY_NAME.keySet().stream().sorted().toList()) {
      Skill skill = (Skill) Minecraft.getInstance().player.getData(Skills.SKILLS_BY_NAME.get(skillName));
      entries.add(new TabEntry(
        skillName,
        Component.literal(StringUtils.snakeToTitle(skillName) + " " + skill.level),
        Skills.SKILL_ICONS.getOrDefault(skillName, Skills.DEFAULT_ICON),
        16,
        skill::renderToLayout
      ));
    }

    return entries;
  }
}
