package com.avi.skram.client;

import com.avi.skram.network.SkillPacket;
import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import com.avi.skram.ui.GenericMessageToast;
import com.avi.skram.ui.ToastUtils;
import com.avi.skram.ui.WidgetUtils;
import com.avi.skram.ui.XPGainGui;
import com.avi.skram.utils.MathUtils;
import com.avi.skram.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ItemDisplayWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SkramClientSkillHooks {

  public static Layout renderSkill(Skill skill) {
    Minecraft minecraft = Minecraft.getInstance();
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
    layout.spacing(2).defaultCellSetting().padding(2);
    layout.addChild(new ResizableTextureImageWidget(0, 0, 32, 32, Skills.SKILL_ICONS.get(skill.name), 16, 16));

    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(3).defaultCellSetting().paddingBottom(2);
    vertical.addChild(new MultiLineTextWidget(Component.literal(StringUtils.capitalize(skill.name) + " Lvl " + skill.level), minecraft.font));

    GridLayout xpBarLayout = new GridLayout();
    int xpBarWidth = (int) MathUtils.linearRescale(skill.xp, Skill.getTotalXpForLevel(skill.level), Skill.getTotalXpForLevel(skill.level + 1), 0, 100);
    xpBarLayout.addChild(new ResizableTextureImageWidget(0, 0, 100, 3, Skill.YELLOW_BAR_BACKGROUND, 182, 5), 0, 0);
    xpBarLayout.addChild(new ResizableTextureImageWidget(0, 0, xpBarWidth, 3, Skill.YELLOW_BAR, 182, 5), 0, 0);
    vertical.addChild(xpBarLayout);

    vertical.addChild(new MultiLineTextWidget(Component.literal("EXP:  " + String.format("%,d", (int) skill.xp)), minecraft.font));
    vertical.addChild(new MultiLineTextWidget(Component.literal("Next: " + String.format("%,d", Skill.getTotalXpForLevel(skill.level + 1))), minecraft.font));
    vertical.addChild(new MultiLineTextWidget(Component.literal("Bonus: +" + (int) ((skill.getTotalXpMultiplier(minecraft.player) - 1) * 100) + "%"), minecraft.font));
    vertical.addChild(renderSkillAbilities(skill));
    vertical.addChild(new ResizableTextureImageWidget(0, 0, 182, 3, Skill.WHITE_BAR, 182, 5));
    vertical.addChild(renderSkillDetails(skill));
    vertical.addChild(renderXpMap(skill));

    layout.addChild(vertical);
    return layout;
  }

  public static Layout renderSkillAbilities(Skill skill) {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);
    for (Skill.AbilityDescription ability : skill.getAbilityDescriptions()) {
      vertical.addChild(Checkbox.builder(Component.literal(ability.text()), Minecraft.getInstance().font)
        .selected(skill.enabledAbilities[ability.enabledIndex()])
        .onValueChange((checkbox, value) -> skill.toggleEnabled(ability.enabledIndex()))
        .build());
    }
    return vertical;
  }

  public static Layout renderSkillDetails(Skill skill) {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(2).defaultCellSetting().paddingTop(2);
    for (String line : skill.getDescription().split("\n")) {
      if (line.isEmpty()) {
        vertical.addChild(new SpacerElement(180, 2));
      }
      vertical.addChild(WidgetUtils.textWidget180(line));
    }
    return vertical;
  }

  private static Layout renderXpMap(Skill skill) {
    if (!skill.getItemXpMap().isEmpty()) return renderItemXpMap(skill.getItemXpMap());
    if (!skill.getBlockXpMap().isEmpty()) return renderBlockXpMap(skill.getBlockXpMap());
    return new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
  }

  private static Layout renderItemXpMap(Map<Item, Integer> xpMap) {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    List<Item> items = new ArrayList<>(xpMap.keySet());
    items.sort(Comparator.comparing(item -> item.getName(new ItemStack(item)).getString()));
    GridLayout grid = new GridLayout();
    GridLayout.RowHelper rowHelper = grid.createRowHelper(3);
    addXpMapColumnSpacers(rowHelper);

    for (int i = 0; i < items.size(); i += 3) {
      for (int j = i; j < i + 3 && j < items.size(); j++) {
        Item item = items.get(j);
        ItemStack stack = new ItemStack(item);
        LinearLayout horizontal = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
        horizontal.spacing(4).defaultCellSetting().alignVerticallyMiddle();
        horizontal.addChild(new ItemDisplayWidget(Minecraft.getInstance(), 0, 0, 16, 16, stack.getHoverName(), stack, false, true));
        horizontal.addChild(new MultiLineTextWidget(Component.literal(xpMap.get(item).toString()), Minecraft.getInstance().font));
        rowHelper.addChild(horizontal);
      }
    }

    vertical.addChild(grid);
    return vertical;
  }

  private static Layout renderBlockXpMap(Map<Block, Integer> xpMap) {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    List<Block> blocks = new ArrayList<>(xpMap.keySet());
    blocks.sort(Comparator.comparing(block -> block.getName().getString()));
    GridLayout grid = new GridLayout();
    GridLayout.RowHelper rowHelper = grid.createRowHelper(3);
    addXpMapColumnSpacers(rowHelper);

    for (int i = 0; i < blocks.size(); i += 3) {
      for (int j = i; j < i + 3 && j < blocks.size(); j++) {
        Block block = blocks.get(j);
        ItemStack stack = new ItemStack(block);
        LinearLayout horizontal = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
        horizontal.spacing(4).defaultCellSetting().alignVerticallyMiddle();
        horizontal.addChild(new ItemDisplayWidget(Minecraft.getInstance(), 0, 0, 16, 16, stack.getHoverName(), stack, false, true));
        horizontal.addChild(new MultiLineTextWidget(Component.literal(xpMap.get(block).toString()), Minecraft.getInstance().font));
        rowHelper.addChild(horizontal);
      }
    }

    vertical.addChild(grid);
    return vertical;
  }

  private static void addXpMapColumnSpacers(GridLayout.RowHelper rowHelper) {
    rowHelper.addChild(SpacerElement.width(50));
    rowHelper.addChild(SpacerElement.width(50));
    rowHelper.addChild(SpacerElement.width(50));
  }

  public static void sendSkillToServer(Skill skill) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.level != null && minecraft.level.isClientSide()) {
      ClientPacketDistributor.sendToServer(new SkillPacket(skill.name, skill.level, skill.xp, skill.xpMultiplier, skill.enabledAbilities));
    }
  }

  public static void notifyXpGain(String skillName, double xp) {
    XPGainGui.notify(skillName, xp);
  }

  public static void showLevelUpToast(Skill skill) {
    Minecraft.getInstance().gui.toastManager().addToast(new GenericMessageToast(
      skill.name + ":level_up",
      Skills.SKILL_ICONS.getOrDefault(skill.name, Skills.DEFAULT_ICON),
      StringUtils.capitalize(skill.name) + " leveled up to Lvl " + skill.level + "."
    ));
  }

  public static void showRequiredLevelToast(Skill skill, String message) {
    ToastManager toastManager = Minecraft.getInstance().gui.toastManager();
    if (!ToastUtils.hasGenericMessageToast(toastManager, skill.getRequiredLevelToastName())) {
      toastManager.addToast(new GenericMessageToast(
        skill.getRequiredLevelToastName(),
        Skills.SKILL_ICONS.getOrDefault(skill.name, Skills.DEFAULT_ICON),
        message
      ));
    }
  }
}
