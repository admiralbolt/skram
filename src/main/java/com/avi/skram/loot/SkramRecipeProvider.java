package com.avi.skram.loot;

import com.avi.skram.Skram;
import com.avi.skram.advancements.SkramAdvancements;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Map;

public class SkramRecipeProvider extends RecipeProvider {

  public static ResourceKey<Recipe<?>> skramKey(String path) {
    return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify(path)));
  }

  public static ResourceKey<Recipe<?>> minecraftKey(String path) {
    return ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace(path));
  }


  protected SkramRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
    super(recipeOutput, advancementOutput);
  }

  @Override
  protected void buildRecipes() {
    final Map<Item, Pair<Item, Integer>> SMELTING_RECIPES = ImmutableMap.<Item, Pair<Item, Integer>>builder()
      .put(Items.IRON_PICKAXE, Pair.of(Items.IRON_NUGGET, 9))
      .put(Items.IRON_SHOVEL, Pair.of(Items.IRON_NUGGET, 3))
      .put(Items.IRON_AXE, Pair.of(Items.IRON_NUGGET, 6))
      .put(Items.IRON_HOE, Pair.of(Items.IRON_NUGGET, 6))
      .put(Items.IRON_SWORD, Pair.of(Items.IRON_NUGGET, 6))
      .put(Items.IRON_SPEAR, Pair.of(Items.IRON_NUGGET, 3))
      .put(Items.IRON_HELMET, Pair.of(Items.IRON_NUGGET, 15))
      .put(Items.IRON_CHESTPLATE, Pair.of(Items.IRON_NUGGET, 24))
      .put(Items.IRON_LEGGINGS, Pair.of(Items.IRON_NUGGET, 21))
      .put(Items.IRON_BOOTS, Pair.of(Items.IRON_NUGGET, 12))
      .put(Items.IRON_HORSE_ARMOR, Pair.of(Items.IRON_NUGGET, 24))
      .put(Items.IRON_NAUTILUS_ARMOR, Pair.of(Items.IRON_NUGGET, 24))
      .put(Items.CHAINMAIL_HELMET, Pair.of(Items.IRON_NUGGET, 9))
      .put(Items.CHAINMAIL_CHESTPLATE, Pair.of(Items.IRON_NUGGET, 15))
      .put(Items.CHAINMAIL_LEGGINGS, Pair.of(Items.IRON_NUGGET, 12))
      .put(Items.CHAINMAIL_BOOTS, Pair.of(Items.IRON_NUGGET, 6))
      .build();

    for (Map.Entry<Item, Pair<Item, Integer>> entry : SMELTING_RECIPES.entrySet()) {
      Pair<Item, Integer> pair = entry.getValue();
      SimpleCookingRecipeBuilder.smelting(
        Ingredient.of(entry.getKey()),
        RecipeCategory.MISC,
        CookingBookCategory.MISC,
        new ItemStackTemplate(BuiltInRegistries.ITEM.wrapAsHolder(pair.getFirst()), pair.getSecond()),
        0.1f,
        200
      )
        .unlockedBy("has_item", has(pair.getFirst()))
        .save(this.output, skramKey("smelt_iron_nugget_from_" + entry.getKey().getDescriptionId()));

      SimpleCookingRecipeBuilder.blasting(
          Ingredient.of(entry.getKey()),
          RecipeCategory.MISC,
          CookingBookCategory.MISC,
          new ItemStackTemplate(BuiltInRegistries.ITEM.wrapAsHolder(pair.getFirst()), pair.getSecond()),
          0.1f,
          200
        )
        .unlockedBy("has_item", has(pair.getFirst()))
        .save(this.output, skramKey("blast_iron_nugget_from_" + entry.getKey().getDescriptionId()));
    }

    // We also need to override the base recipe.
    SimpleCookingRecipeBuilder.smelting(
        Ingredient.of(Items.IRON_PICKAXE),
        RecipeCategory.MISC,
        CookingBookCategory.MISC,
        new ItemStackTemplate(BuiltInRegistries.ITEM.wrapAsHolder(Items.IRON_NUGGET), 9),
        0.1f,
        200
      )
      .unlockedBy("has_item", has(Items.IRON_PICKAXE))
      .save(this.output, minecraftKey("iron_nugget_from_smelting"));

    SimpleCookingRecipeBuilder.blasting(
        Ingredient.of(Items.IRON_PICKAXE),
        RecipeCategory.MISC,
        CookingBookCategory.MISC,
        new ItemStackTemplate(BuiltInRegistries.ITEM.wrapAsHolder(Items.IRON_NUGGET), 9),
        0.1f,
        200
      )
      .unlockedBy("has_item", has(Items.IRON_PICKAXE))
      .save(this.output, minecraftKey("iron_nugget_from_blasting"));

    final Map<Item, Pair<Item, Integer>> GOLD_RECIPES = ImmutableMap.<Item, Pair<Item, Integer>>builder()
      .put(Items.GOLDEN_PICKAXE, Pair.of(Items.GOLD_NUGGET, 9))
      .put(Items.GOLDEN_SHOVEL, Pair.of(Items.GOLD_NUGGET, 3))
      .put(Items.GOLDEN_AXE, Pair.of(Items.GOLD_NUGGET, 6))
      .put(Items.GOLDEN_HOE, Pair.of(Items.GOLD_NUGGET, 6))
      .put(Items.GOLDEN_SWORD, Pair.of(Items.GOLD_NUGGET, 6))
      .put(Items.GOLDEN_SPEAR, Pair.of(Items.GOLD_NUGGET, 3))
      .put(Items.GOLDEN_HELMET, Pair.of(Items.GOLD_NUGGET, 15))
      .put(Items.GOLDEN_CHESTPLATE, Pair.of(Items.GOLD_NUGGET, 24))
      .put(Items.GOLDEN_LEGGINGS, Pair.of(Items.GOLD_NUGGET, 21))
      .put(Items.GOLDEN_BOOTS, Pair.of(Items.GOLD_NUGGET, 12))
      .put(Items.GOLDEN_HORSE_ARMOR, Pair.of(Items.GOLD_NUGGET, 24))
      .put(Items.GOLDEN_NAUTILUS_ARMOR, Pair.of(Items.GOLD_NUGGET, 24))
      .build();

    for (Map.Entry<Item, Pair<Item, Integer>> entry : GOLD_RECIPES.entrySet()) {
      Pair<Item, Integer> pair = entry.getValue();
      SimpleCookingRecipeBuilder.smelting(
        Ingredient.of(entry.getKey()),
        RecipeCategory.MISC,
        CookingBookCategory.MISC,
        new ItemStackTemplate(BuiltInRegistries.ITEM.wrapAsHolder(pair.getFirst()), pair.getSecond()),
        0.1f,
        200
      )
        .unlockedBy("has_item", has(pair.getFirst()))
        .save(this.output, skramKey("smelt_gold_nugget_from_" + entry.getKey().getDescriptionId()));

      SimpleCookingRecipeBuilder.blasting(
          Ingredient.of(entry.getKey()),
          RecipeCategory.MISC,
          CookingBookCategory.MISC,
          new ItemStackTemplate(BuiltInRegistries.ITEM.wrapAsHolder(pair.getFirst()), pair.getSecond()),
          0.1f,
          200
        )
        .unlockedBy("has_item", has(pair.getFirst()))
        .save(this.output, skramKey("blast_gold_nugget_from_" + entry.getKey().getDescriptionId()));
    }

    // We also need to override the base recipe.
    SimpleCookingRecipeBuilder.smelting(
        Ingredient.of(Items.GOLDEN_PICKAXE),
        RecipeCategory.MISC,
        CookingBookCategory.MISC,
        new ItemStackTemplate(BuiltInRegistries.ITEM.wrapAsHolder(Items.GOLD_NUGGET), 9),
        0.1f,
        200
      )
      .unlockedBy("has_item", has(Items.GOLDEN_PICKAXE))
      .save(this.output, minecraftKey("gold_nugget_from_smelting"));

    SimpleCookingRecipeBuilder.blasting(
        Ingredient.of(Items.GOLDEN_PICKAXE),
        RecipeCategory.MISC,
        CookingBookCategory.MISC,
        new ItemStackTemplate(BuiltInRegistries.ITEM.wrapAsHolder(Items.GOLD_NUGGET), 9),
        0.1f,
        200
      )
      .unlockedBy("has_item", has(Items.GOLDEN_PICKAXE))
      .save(this.output, minecraftKey("gold_nugget_from_blasting"));
  }


//  public static MultiRegistryBootstrap create() {
//    return new MultiRegistryBootstrap() {
//      @Override
//      public @NonNull Set<ResourceKey<? extends Registry<?>>> requestedRegistries() {
//        // Return the registries we are adding entries to.
//        return Set.of(Registries.RECIPE, Registries.ADVANCEMENT);
//      }
//
//      @Override
//      public void run(MultiRegistryBootstrap.@NonNull BootstrapGetter registries) {
//        // Run the recipe provider.
//        new SkramRecipeProvider(registries.get(Registries.RECIPE), registries.get(Registries.ADVANCEMENT)).buildRecipes();
//      }
//    };
//  }

}
