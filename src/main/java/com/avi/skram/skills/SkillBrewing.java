package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.HashMap;
import java.util.Map;

public class SkillBrewing extends Skill {

  public static final String NAME = "brewing";
  public static final StreamCodec<ByteBuf, SkillBrewing> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillBrewing::new);
  public static Map<String, Integer> XP_MAP;

  public static final int BREWING_SPEED_INDEX = 0;

  public SkillBrewing() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true};
  }

  public SkillBrewing(ByteBuf buffer) {
    super(buffer);
  }

  public static void registerXPMap() {
    XP_MAP = new HashMap<>();
    XP_MAP.put("item.minecraft.potion.effect.awkward", 50);
    XP_MAP.put("item.minecraft.potion.effect.empty", 50);
    XP_MAP.put("item.minecraft.potion.effect.fire_resistance", 50);
    XP_MAP.put("item.minecraft.potion.effect.harming", 50);
    XP_MAP.put("item.minecraft.potion.effect.healing", 50);
    XP_MAP.put("item.minecraft.potion.effect.infested", 50);
    XP_MAP.put("item.minecraft.potion.effect.invisibility", 50);
    XP_MAP.put("item.minecraft.potion.effect.leaping", 50);
    XP_MAP.put("item.minecraft.potion.effect.levitation", 50);
    XP_MAP.put("item.minecraft.potion.effect.luck", 50);
    XP_MAP.put("item.minecraft.potion.effect.mundane", 50);
    XP_MAP.put("item.minecraft.potion.effect.night_vision", 50);
    XP_MAP.put("item.minecraft.potion.effect.oozing", 50);
    XP_MAP.put("item.minecraft.potion.effect.poison", 50);
    XP_MAP.put("item.minecraft.potion.effect.regeneration", 50);
    XP_MAP.put("item.minecraft.potion.effect.slow_falling", 50);
    XP_MAP.put("item.minecraft.potion.effect.slowness", 50);
    XP_MAP.put("item.minecraft.potion.effect.strength", 50);
    XP_MAP.put("item.minecraft.potion.effect.swiftness", 50);
    XP_MAP.put("item.minecraft.potion.effect.thick", 50);
    XP_MAP.put("item.minecraft.potion.effect.turtle_master", 50);
    XP_MAP.put("item.minecraft.potion.effect.water", 50);
    XP_MAP.put("item.minecraft.potion.effect.water_breathing", 50);
    XP_MAP.put("item.minecraft.potion.effect.weakness", 50);
    XP_MAP.put("item.minecraft.potion.effect.weaving", 50);
    XP_MAP.put("item.minecraft.potion.effect.wind_charged", 50);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.BREWING, player.getData(Skills.BREWING));
  }

  public void addXpForItems(Player player, NonNullList<ItemStack> items) {
    int totalXp = 0;
    for (ItemStack stack : items) {
      PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
      if (contents == null) continue;

      PotionItem potionItem = (PotionItem) stack.getItem();
      Component name = potionItem.getName(stack);
      if (!(name.getContents() instanceof TranslatableContents translatableContents)) continue;

      totalXp += XP_MAP.getOrDefault(translatableContents.getKey(), 0);
    }
    this.addXp(player, totalXp);
  }

  public float getBrewingSpeed() {
    return 0.02f * this.level;
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Brewing Speed: +" + (int) (100 * this.getBrewingSpeed()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[BREWING_SPEED_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(BREWING_SPEED_INDEX))
        .build()
    );

    return vertical;
  }
}