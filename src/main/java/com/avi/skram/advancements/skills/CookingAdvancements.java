package com.avi.skram.advancements.skills;

import com.avi.skram.Skram;
import com.avi.skram.advancements.ItemSmeltedTrigger;
import com.avi.skram.advancements.SkramAdvancements;
import com.avi.skram.utils.DataGenerationUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.*;
import net.minecraft.advancements.triggers.RecipeCraftedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CookingAdvancements {

  public static final List<Pair<Item, Integer>> COOKING_LOG = List.of(
    Pair.of(Items.BAKED_POTATO, 1),
    Pair.of(Items.BEETROOT_SOUP, 2),
    Pair.of(Items.BREAD, 1),
    Pair.of(Items.CAKE, 2),
    Pair.of(Items.COOKIE, 1),
    Pair.of(Items.DRIED_KELP, 1),
    Pair.of(Items.COOKED_BEEF, 1),
    Pair.of(Items.COOKED_PORKCHOP, 1),
    Pair.of(Items.COOKED_MUTTON, 1),
    Pair.of(Items.COOKED_CHICKEN, 1),
    Pair.of(Items.COOKED_RABBIT, 2),
    Pair.of(Items.COOKED_COD, 1),
    Pair.of(Items.COOKED_SALMON, 1),
    Pair.of(Items.GOLDEN_APPLE, 3),
    Pair.of(Items.GOLDEN_CARROT, 1),
    Pair.of(Items.MUSHROOM_STEW, 2),
    Pair.of(Items.PUMPKIN_PIE, 2)
    // Pair.of(Items.SUSPICIOUS_STEW, 2)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    HolderGetter<Item> items = output.lookup(Registries.ITEM);
    HolderGetter<Recipe<?>> recipes = output.lookup(Registries.RECIPE);
    List<AdvancementHolder> itemAdvancements = new ArrayList<>();

    // this.recipes.getOrThrow(ResourceKey.create(Registries.RECIPE, recipeId)));

    for (Pair<Item, Integer> itemPair : COOKING_LOG) {
      Item item = itemPair.getFirst();
      Integer xpMult = itemPair.getSecond();

      // item.getDescriptionId() looks like item.minecraft.name
      String itemBaseName = Arrays.stream(item.getDescriptionId().split("\\.")).skip(2).collect(Collectors.joining("_"));
      String itemName = DataGenerationUtils.titleize(itemBaseName);
      itemAdvancements.add(Advancement.Builder.advancement()
        .parent(itemAdvancements.isEmpty() ? skramRoot : itemAdvancements.getLast())
        .display(
          new ItemStackTemplate(item),
          Component.literal("Cook: " + itemName),
          Component.literal("Cooking this item grants a +" + xpMult + "% boost to all cooking xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "cooking_xp_mult_up" + xpMult))
        ).addCriterion(
          "smelt_thing", ItemSmeltedTrigger.TriggerInstance.isItem(items, item)
        ).addCriterion(
          "craft_thing", RecipeCraftedTrigger.TriggerInstance.craftedItem(
            HolderSet.direct(recipes.getOrThrow(ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace(itemBaseName)))))
        ).requirements(AdvancementRequirements.anyOf(List.of("smelt_thing", "craft_thing")))
        .save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("cook_" + item.getDescriptionId()))));
    }
  }

}
