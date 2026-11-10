package com.avi.skram.perks;

import com.avi.skram.Skram;
import com.avi.skram.network.PerkPacket;
import com.avi.skram.network.PerkResearchPacket;
import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.StringUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Perk implements ValueIOSerializable {

  private static final FontDescription ALT_FONT = new FontDescription.Resource(Identifier.withDefaultNamespace("alt"));
  private static final Style BLURRED_TEXT = Style.EMPTY.withFont(ALT_FONT);

  // As far as serialization goes, the only values that can change are really the discovered/researched values.
  public String name;
  public String description;
  // Perks are unlocked via a two-step process, first discovery, then research.
  public boolean discovered;
  public boolean researched;
  public boolean enabled;

  // Each perk has governing skills. These skills have required minimum levels before the perk can be researched.
  public Map<String, Integer> requiredSkills;
  // Each perk also has a required research cost. These rely on ItemStacks being available, so they are unfortunately
  // tracked separately. See cost info in Perks.java.

  public Perk(String name, String description, Map<String, Integer> requiredSkills) {
    this.name = name;
    this.description = description;
    this.discovered = false;
    this.researched = false;
    this.enabled = true;
    this.requiredSkills = requiredSkills;
  }

  public Perk(ByteBuf buffer) {
    int nameLength = buffer.readInt();
    this.name = buffer.readString(nameLength, Charset.defaultCharset());
    this.discovered = buffer.readBoolean();
    this.researched = buffer.readBoolean();
    this.enabled = buffer.readBoolean();
  }

  public Perk(ByteBuf buffer, String description, Map<String, Integer> requiredSkills) {
    this(buffer);
    this.description = description;
    this.requiredSkills = requiredSkills;
  }

  public void encode(ByteBuf buffer) {
    buffer.writeInt(this.name.length());
    buffer.writeCharSequence(this.name, Charset.defaultCharset());
    buffer.writeBoolean(this.discovered);
    buffer.writeBoolean(this.researched);
    buffer.writeBoolean(this.enabled);
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putString("name", this.name);
    output.putBoolean("discovered", this.discovered);
    output.putBoolean("researched", this.researched);
    output.putBoolean("enabled", this.enabled);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.name = input.getStringOr("name", "DIE");
    this.discovered = input.getBooleanOr("discovered", false);
    this.researched = input.getBooleanOr("researched", false);
    this.enabled = input.getBooleanOr("enabled", false);
  }

  // Override this if the perk applies any static ability modifiers to the player.
  public void applyModifiers(Player player) {
    return;
  }

  public boolean isFullyEnabled() {
    return this.discovered && this.researched && this.enabled;
  }

  public Identifier getIcon() {
    return Identifier.fromNamespaceAndPath(Skram.MODID, "textures/perks/" + this.name + ".png");
  }

  public Component getDisplayName() {
    return styledText(StringUtils.snakeToTitle(this.name));
  }

  public boolean canResearch(Player player) {
    for (Map.Entry<String, Integer> entry : requiredSkills.entrySet()) {
      Skill skill = (Skill) player.getData(Skills.SKILLS_BY_NAME.get(entry.getKey()));
      if (skill.level < entry.getValue()) return false;
    }

    return true;
  }

  public boolean research(Player player) {
    if (player.level().isClientSide() || !this.discovered || this.researched || !this.canResearch(player)) {
      return false;
    }

    List<ItemStack> researchCost = Perks.PERK_RESEARCH_COST_BY_NAME.get(this.name).get();
    Inventory inventory = player.getInventory();
    Map<Integer, Integer> itemsToConsume = new LinkedHashMap<>();

    for (ItemStack cost : researchCost) {
      int remaining = cost.getCount();
      for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; ++slot) {
        ItemStack item = inventory.getItem(slot);
        if (item.isEmpty() || !ItemStack.isSameItemSameComponents(item, cost)) continue;

        int available = item.getCount() - itemsToConsume.getOrDefault(slot, 0);
        int toConsume = Math.min(available, remaining);
        if (toConsume > 0) {
          itemsToConsume.merge(slot, toConsume, Integer::sum);
          remaining -= toConsume;
        }
      }

      if (remaining > 0) return false;
    }

    itemsToConsume.forEach(inventory::removeItem);
    this.researched = true;
    this.applyModifiers(player);
    player.setData(Perks.PERKS_BY_NAME.get(this.name), this);
    return true;
  }

  // This method is giant and disgusting and mostly AI generated, but god damn it, it works.
  public Layout renderToLayout(Player player, List<ItemStack> inventoryResources) {
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    layout.spacing(2).defaultCellSetting().padding(2);

    LinearLayout heading = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
    heading.spacing(4).defaultCellSetting();
    heading.addChild(new ResizableTextureImageWidget(0, 0, 32, 32, (this.discovered) ? this.getIcon() : Perks.UNKNOWN_ICON, 32, 32));
    heading.addChild(new MultiLineTextWidget(this.getDisplayName(), Minecraft.getInstance().font));
    layout.addChild(heading);

    layout.addChild(textWidget(this.description, 180));

    if (this.researched && this.discovered) {
      Component enabledLabel = styledText("Enabled");
      layout.addChild(Checkbox.builder(enabledLabel, Minecraft.getInstance().font)
        .selected(this.enabled)
        .onValueChange((checkbox, value) -> this.setEnabled(player, value))
        .build());
    }
    layout.addChild(SpacerElement.height(10));

    layout.addChild(textWidget("Required levels:", 180));
    List<Map.Entry<String, Integer>> requiredSkills = this.requiredSkills.entrySet().stream()
      .sorted(Map.Entry.comparingByKey())
      .toList();
    for (int i = 0; i < requiredSkills.size(); i += 2) {
      LinearLayout skillRequirement = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
      skillRequirement.spacing(3).defaultCellSetting().alignVerticallyMiddle();

      for (Map.Entry<String, Integer> entry : requiredSkills.subList(i, Math.min(i + 2, requiredSkills.size()))) {
        Skill skill = (Skill) player.getData(Skills.SKILLS_BY_NAME.get(entry.getKey()));

        if (this.discovered) {
          skillRequirement.addChild(new ResizableTextureImageWidget(
            0, 0, 16, 16,
            Skills.SKILL_ICONS.getOrDefault(entry.getKey(), Skills.DEFAULT_ICON),
            16, 16
          ));
        }

        Component levelText = Component.literal(StringUtils.snakeToTitle(entry.getKey()) + " ")
          .append(Component.literal(String.valueOf(entry.getValue()))
            .withStyle(skill.level >= entry.getValue() ? ChatFormatting.GREEN : ChatFormatting.RED));
        MultiLineTextWidget levelWidget = new MultiLineTextWidget((this.discovered) ? levelText : styledText(entry.getKey() + " level 99"), Minecraft.getInstance().font);
        levelWidget.setMaxWidth(160);
        skillRequirement.addChild(levelWidget);
      }

      layout.addChild(skillRequirement);
    }

    if (this.discovered && !this.researched) {
      layout.addChild(textWidget("Required materials:", 180));
      List<ItemStack> researchCost = Perks.PERK_RESEARCH_COST_BY_NAME.get(this.name).get();
      boolean hasAllResearchMaterials = researchCost.stream().allMatch(cost -> inventoryResources.stream()
        .filter(resource -> ItemStack.isSameItemSameComponents(resource, cost))
        .mapToInt(ItemStack::getCount)
        .sum() >= cost.getCount());

      LinearLayout researchMaterials = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
      researchMaterials.spacing(4).defaultCellSetting().alignVerticallyMiddle();

      for (var stack : researchCost) {
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
        button -> ClientPacketDistributor.sendToServer(new PerkResearchPacket(this.name))
      )
        .size(100, 20)
        .tooltip(Tooltip.create(Component.literal("Researching this perk costs the listed resources. Resources must be in your inventory.")))
        .build();
      researchButton.active = hasAllResearchMaterials && this.canResearch(player);
      layout.addChild(researchButton);
    }

    return layout;
  }

  private MultiLineTextWidget textWidget(String text, int maxWidth) {
    MultiLineTextWidget widget = new MultiLineTextWidget(styledText(text), Minecraft.getInstance().font);
    widget.setMaxWidth(maxWidth);
    return widget;
  }

  private Component styledText(String text) {
    if (!this.discovered) return Component.literal(text).withStyle(BLURRED_TEXT).withStyle(ChatFormatting.GRAY);
    if (!this.researched) return Component.literal(text).withStyle(ChatFormatting.GRAY);

    return Component.literal(text);
  }

  private void setEnabled(Player player, boolean enabled) {
    this.enabled = enabled;
    if (player.level().isClientSide()) {
      ClientPacketDistributor.sendToServer(new PerkPacket(this.name, this.discovered, this.researched, this.enabled));
    }
  }

}
