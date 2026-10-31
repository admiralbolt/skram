package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public class SkillCooking extends Skill {

  public static final String NAME = "cooking";
  public static final StreamCodec<ByteBuf, SkillCooking> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillCooking::new);
  public static Map<Item, Integer> XP_MAP;

  public static final int FOOD_BOOST_INDEX = 0;

  public SkillCooking() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true};
  }

  public SkillCooking(ByteBuf buffer) {
    super(buffer);
  }

  public static void setupXPMap() {
    XP_MAP = new HashMap<>();

    XP_MAP.put(Items.BAKED_POTATO, 20);
    XP_MAP.put(Items.BEETROOT_SOUP, 40);
    XP_MAP.put(Items.BREAD, 25);
    XP_MAP.put(Items.CAKE, 200);
    XP_MAP.put(Items.COOKED_BEEF, 50);
    XP_MAP.put(Items.COOKED_CHICKEN, 50);
    XP_MAP.put(Items.COOKED_COD, 50);
    XP_MAP.put(Items.COOKED_MUTTON, 50);
    XP_MAP.put(Items.COOKED_RABBIT, 50);
    XP_MAP.put(Items.COOKED_SALMON, 50);
    XP_MAP.put(Items.COOKIE, 2);
    XP_MAP.put(Items.DRIED_KELP, 30);
    XP_MAP.put(Items.GOLDEN_CARROT, 60);
    XP_MAP.put(Items.HONEY_BLOCK, 40);
    XP_MAP.put(Items.PUMPKIN_PIE, 160);
    XP_MAP.put(Items.RABBIT_STEW, 250);
    XP_MAP.put(Items.SUSPICIOUS_STEW, 200);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.COOKING, player.getData(Skills.COOKING));
  }

  public double getFoodBoost() {
    return 0.01 * this.level;
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Food Boost: +" + (int) (100 * this.getFoodBoost()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[FOOD_BOOST_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(FOOD_BOOST_INDEX))
        .build()
    );

    return vertical;
  }

  public static void handleCrafting(Player player, ItemStack itemStack) {
    if (!SkillCooking.XP_MAP.containsKey(itemStack.getItem())) return;

    SkillCooking.overrideFood(player, itemStack);
    SkillCooking cooking = player.getData(Skills.COOKING);
    cooking.addXp(player, SkillCooking.XP_MAP.get(itemStack.getItem()) * itemStack.getCount());
  }

  public static void overrideFood(Player player, ItemStack stack) {
    SkillCooking cooking = player.getData(Skills.COOKING);
    if (!cooking.enabledAbilities[FOOD_BOOST_INDEX]) return;

    FoodProperties foodBase = stack.get(DataComponents.FOOD);
    if (foodBase == null) return;

    // Technically speaking, saturation is a float which means every increased level of cooking can improve the efficacy
    // of cooked food. This makes inventory management pretty annoying though, so we only want to override food in
    // cases where the amount of hunger bar aka nutrition restored _actually_ increases.
    int newNutrition = (int) (foodBase.nutrition() * (1 + cooking.getFoodBoost()));
    if (newNutrition == foodBase.nutrition()) return;

    // We can't use cooking.getFoodBoost() directly on the saturation for the same reason.
    // Instead, we use a fixed value based on the ratio between new and old nutrition.
    float newSaturation = foodBase.saturation() * ((float) newNutrition) / foodBase.nutrition();

    FoodProperties newProperties = new FoodProperties.Builder().nutrition(newNutrition).saturationModifier(newSaturation).build();
    stack.set(DataComponents.FOOD, newProperties);

    // Also update the name so that it's obvious that this is a modified food.
    MutableComponent newName = Component.empty();
    newName.append(stack.getItemName());
    newName.append(" +" + (newNutrition - foodBase.nutrition()));
    stack.set(DataComponents.ITEM_NAME, newName);
  }

}
