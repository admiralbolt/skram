package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
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
  public static final String DESCRIPTION = """
    Cooking makes your food better!
    
    While each level of cooking says it gives +1% food boost, to avoid cluttering up inventories with many unstackable foods of the same type, foods are bucketed at certain bonus thresholds i.e. +1, +2, +3.
    All Bread +1 items are the same, and can stack, regardless of who cooked it.
    
    XP values for cooking:
    """;
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

    XP_MAP.put(Items.BAKED_POTATO, 40);
    XP_MAP.put(Items.BEETROOT_SOUP, 50);
    XP_MAP.put(Items.BREAD, 20);
    XP_MAP.put(Items.CAKE, 200);
    XP_MAP.put(Items.COOKED_BEEF, 70);
    XP_MAP.put(Items.COOKED_CHICKEN, 70);
    XP_MAP.put(Items.COOKED_COD, 70);
    XP_MAP.put(Items.COOKED_MUTTON, 70);
    XP_MAP.put(Items.COOKED_RABBIT, 100);
    XP_MAP.put(Items.COOKED_SALMON, 70);
    XP_MAP.put(Items.COOKIE, 2);
    XP_MAP.put(Items.DRIED_KELP, 30);
    XP_MAP.put(Items.GOLDEN_APPLE, 100);
    XP_MAP.put(Items.GOLDEN_CARROT, 80);
    XP_MAP.put(Items.PUMPKIN_PIE, 90);
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
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(new AbilityDescription(
      "Food Boost: +" + (int) (100 * this.getFoodBoost()) + "%.",
      FOOD_BOOST_INDEX
    ));
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

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }

  @Override
  public Map<Item, Integer> getItemXpMap() {
    return XP_MAP;
  }

}
