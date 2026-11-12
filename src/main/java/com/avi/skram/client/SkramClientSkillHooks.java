package com.avi.skram.client;

import com.avi.skram.network.PerkPacket;
import com.avi.skram.network.PerkResearchPacket;
import com.avi.skram.network.SkillPacket;
import com.avi.skram.perks.Perk;
import com.avi.skram.perks.Perks;
import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import com.avi.skram.ui.GenericMessageToast;
import com.avi.skram.ui.ToastUtils;
import com.avi.skram.ui.WidgetUtils;
import com.avi.skram.ui.XPGainGui;
import com.avi.skram.utils.MathUtils;
import com.avi.skram.utils.StringUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
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
    Minecraft.getInstance().gui.toastManager().addToast(GenericMessageToast.withDefaultDisplayTime(
      skill.name + ":level_up",
      Skills.SKILL_ICONS.getOrDefault(skill.name, Skills.DEFAULT_ICON),
      StringUtils.capitalize(skill.name) + " leveled up to Lvl " + skill.level + "."
    ));
  }

  public static void showRequiredLevelToast(Skill skill, String message) {
    ToastManager toastManager = Minecraft.getInstance().gui.toastManager();
    if (!ToastUtils.hasGenericMessageToast(toastManager, skill.getRequiredLevelToastName())) {
      toastManager.addToast(GenericMessageToast.withDefaultDisplayTime(
        skill.getRequiredLevelToastName(),
        Skills.SKILL_ICONS.getOrDefault(skill.name, Skills.DEFAULT_ICON),
        message
      ));
    }
  }

  public static Layout renderPerk(Perk perk, Player player, List<ItemStack> inventoryResources) {
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    layout.spacing(2).defaultCellSetting().padding(2);

    LinearLayout heading = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
    heading.spacing(4).defaultCellSetting();
    heading.addChild(new ResizableTextureImageWidget(0, 0, 32, 32, perk.discovered ? perk.getIcon() : Perks.UNKNOWN_ICON, 32, 32));
    heading.addChild(new MultiLineTextWidget(perk.getDisplayName(), Minecraft.getInstance().font));
    layout.addChild(heading);
    layout.addChild(perkTextWidget(perk, perk.description, 180));

    if (perk.researched && perk.discovered) {
      layout.addChild(Checkbox.builder(perk.getStyledText("Enabled"), Minecraft.getInstance().font)
        .selected(perk.enabled)
        .onValueChange((checkbox, value) -> setPerkEnabled(perk, value))
        .build());
    }
    layout.addChild(SpacerElement.height(10));
    layout.addChild(perkTextWidget(perk, "Required levels:", 180));

    List<Map.Entry<String, Integer>> requiredSkills = perk.requiredSkills.entrySet().stream()
      .sorted(Map.Entry.comparingByKey())
      .toList();
    for (int i = 0; i < requiredSkills.size(); i += 2) {
      LinearLayout skillRequirement = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
      skillRequirement.spacing(3).defaultCellSetting().alignVerticallyMiddle();
      for (Map.Entry<String, Integer> entry : requiredSkills.subList(i, Math.min(i + 2, requiredSkills.size()))) {
        Skill skill = (Skill) player.getData(Skills.SKILLS_BY_NAME.get(entry.getKey()));
        if (perk.discovered) {
          skillRequirement.addChild(new ResizableTextureImageWidget(
            0, 0, 16, 16,
            Skills.SKILL_ICONS.getOrDefault(entry.getKey(), Skills.DEFAULT_ICON),
            16, 16
          ));
        }

        Component levelText = Component.literal(StringUtils.snakeToTitle(entry.getKey()) + " ")
          .append(Component.literal(String.valueOf(entry.getValue()))
            .withStyle(skill.level >= entry.getValue() ? ChatFormatting.GREEN : ChatFormatting.RED));
        MultiLineTextWidget levelWidget = new MultiLineTextWidget(
          perk.discovered ? levelText : perk.getStyledText(entry.getKey() + " level 99"),
          Minecraft.getInstance().font
        );
        levelWidget.setMaxWidth(160);
        skillRequirement.addChild(levelWidget);
      }
      layout.addChild(skillRequirement);
    }

    if (perk.discovered && !perk.researched) {
      layout.addChild(perkTextWidget(perk, "Required materials:", 180));
      List<ItemStack> researchCost = perk.getResearchCost(player);
      boolean hasAllResearchMaterials = researchCost.stream().allMatch(cost -> inventoryResources.stream()
        .filter(resource -> ItemStack.isSameItemSameComponents(resource, cost))
        .mapToInt(ItemStack::getCount)
        .sum() >= cost.getCount());

      LinearLayout researchMaterials = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
      researchMaterials.spacing(4).defaultCellSetting().alignVerticallyMiddle();
      for (ItemStack stack : researchCost) {
        int available = inventoryResources.stream()
          .filter(resource -> ItemStack.isSameItemSameComponents(resource, stack))
          .mapToInt(ItemStack::getCount)
          .sum();
        researchMaterials.addChild(new ItemDisplayWidget(
          Minecraft.getInstance(), 0, 0, 16, 16, stack.getHoverName(), stack, false, true
        ));
        Component countText = Component.literal(String.valueOf(stack.getCount()))
          .withStyle(available >= stack.getCount() ? ChatFormatting.GREEN : ChatFormatting.RED);
        MultiLineTextWidget countWidget = new MultiLineTextWidget(countText, Minecraft.getInstance().font);
        countWidget.setMaxWidth(32);
        researchMaterials.addChild(countWidget);
      }
      layout.addChild(researchMaterials);

      Button researchButton = Button.builder(
        Component.literal("Research"),
        button -> sendPerkResearchToServer(perk.name)
      )
        .size(100, 20)
        .tooltip(Tooltip.create(Component.literal("Researching this perk costs the listed resources. Resources must be in your inventory.")))
        .build();
      researchButton.active = hasAllResearchMaterials && perk.canResearch(player);
      layout.addChild(researchButton);
    }

    return layout;
  }

  private static MultiLineTextWidget perkTextWidget(Perk perk, String text, int maxWidth) {
    MultiLineTextWidget widget = new MultiLineTextWidget(perk.getStyledText(text), Minecraft.getInstance().font);
    widget.setMaxWidth(maxWidth);
    return widget;
  }

  public static void setPerkEnabled(Perk perk, boolean enabled) {
    perk.enabled = enabled;
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.level != null && minecraft.level.isClientSide()) {
      ClientPacketDistributor.sendToServer(new PerkPacket(perk.name, perk.discovered, perk.researched, perk.enabled));
    }
  }

  public static void sendPerkResearchToServer(String perkName) {
    ClientPacketDistributor.sendToServer(new PerkResearchPacket(perkName));
  }

  public static void discoverPerk(Perk perk) {
    ClientPacketDistributor.sendToServer(new PerkPacket(perk.name, true, false, true));
    showPerkDiscoveredToast(perk);
  }

  public static void showPerkDiscoveredToast(Perk perk) {
    ToastManager toastManager = Minecraft.getInstance().gui.toastManager();
    String toastId = perk.name + ":discovered";
    if (!ToastUtils.hasGenericMessageToast(toastManager, toastId)) {
      toastManager.addToast(GenericMessageToast.withDefaultDisplayTime(
        toastId,
        perk.getIcon(),
        "Perk discovered: " + StringUtils.snakeToTitle(perk.name)
      ));
    }
  }
}
