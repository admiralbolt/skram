package com.avi.skram.utils;

import com.avi.skram.item.SkramItems;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Map;

public class DataGenerationUtils {

  // The ONLY reason I'm doing this is because I can't lookup spawn eggs during datagen. Cries.
  public static final Map<EntityType<?>, Item> ENTITY_TO_SPAWN_EGG = ImmutableMap.<EntityType<?>, Item>builder()
    .put(EntityTypes.BLAZE, Items.BLAZE_SPAWN_EGG)
    .put(EntityTypes.BOGGED, Items.BOGGED_SPAWN_EGG)
    .put(EntityTypes.BREEZE, Items.BREEZE_SPAWN_EGG)
    .put(EntityTypes.CAMEL_HUSK, Items.CAMEL_HUSK_SPAWN_EGG)
    .put(EntityTypes.CAVE_SPIDER, Items.CAVE_SPIDER_SPAWN_EGG)
    .put(EntityTypes.CREAKING, Items.CREAKING_SPAWN_EGG)
    .put(EntityTypes.CREEPER, Items.CREEPER_SPAWN_EGG)
    .put(EntityTypes.DROWNED, Items.DROWNED_SPAWN_EGG)
    .put(EntityTypes.ELDER_GUARDIAN, Items.ELDER_GUARDIAN_SPAWN_EGG)
    .put(EntityTypes.ENDER_DRAGON, Items.ENDER_DRAGON_SPAWN_EGG)
    .put(EntityTypes.ENDERMAN, Items.ENDERMAN_SPAWN_EGG)
    .put(EntityTypes.ENDERMITE, Items.ENDERMITE_SPAWN_EGG)
    .put(EntityTypes.EVOKER, Items.EVOKER_SPAWN_EGG)
    .put(EntityTypes.GHAST, Items.GHAST_SPAWN_EGG)
    .put(EntityTypes.GUARDIAN, Items.GUARDIAN_SPAWN_EGG)
    .put(EntityTypes.HOGLIN, Items.HOGLIN_SPAWN_EGG)
    .put(EntityTypes.HUSK, Items.HUSK_SPAWN_EGG)
    .put(EntityTypes.MAGMA_CUBE, Items.MAGMA_CUBE_SPAWN_EGG)
    .put(EntityTypes.PARCHED, Items.PARCHED_SPAWN_EGG)
    .put(EntityTypes.PHANTOM, Items.PHANTOM_SPAWN_EGG)
    .put(EntityTypes.PIGLIN, Items.PIGLIN_SPAWN_EGG)
    .put(EntityTypes.PIGLIN_BRUTE, Items.PIGLIN_BRUTE_SPAWN_EGG)
    .put(EntityTypes.PILLAGER, Items.PILLAGER_SPAWN_EGG)
    .put(EntityTypes.RAVAGER, Items.RAVAGER_SPAWN_EGG)
    .put(EntityTypes.SHULKER, Items.SHULKER_SPAWN_EGG)
    .put(EntityTypes.SILVERFISH, Items.SILVERFISH_SPAWN_EGG)
    .put(EntityTypes.SKELETON, Items.SKELETON_SPAWN_EGG)
    .put(EntityTypes.SLIME, Items.SLIME_SPAWN_EGG)
    .put(EntityTypes.SPIDER, Items.SPIDER_SPAWN_EGG)
    .put(EntityTypes.STRAY, Items.STRAY_SPAWN_EGG)
    .put(EntityTypes.VEX, Items.VEX_SPAWN_EGG)
    .put(EntityTypes.VINDICATOR, Items.VINDICATOR_SPAWN_EGG)
    .put(EntityTypes.WITCH, Items.WITCH_SPAWN_EGG)
    .put(EntityTypes.WITHER_SKELETON, Items.WITHER_SKELETON_SPAWN_EGG)
    .put(EntityTypes.WITHER, Items.WITHER_SPAWN_EGG)
    .put(EntityTypes.ZOGLIN, Items.ZOGLIN_SPAWN_EGG)
    .put(EntityTypes.ZOMBIE_VILLAGER, Items.ZOMBIE_VILLAGER_SPAWN_EGG)
    .put(EntityTypes.ZOMBIE, Items.ZOMBIE_SPAWN_EGG)
    .put(EntityTypes.ZOMBIE_HORSE, Items.ZOMBIE_HORSE_SPAWN_EGG)
    .put(EntityTypes.ZOMBIFIED_PIGLIN, Items.ZOMBIFIED_PIGLIN_SPAWN_EGG)
    .put(EntityTypes.ZOMBIE_NAUTILUS, Items.ZOMBIE_NAUTILUS_SPAWN_EGG)
    .build();

  // Suffering continues.
  public static final Map<ResourceKey<Biome>, DeferredItem<Item>> BIOME_TO_ITEM = ImmutableMap.<ResourceKey<Biome>, DeferredItem<Item>>builder()
    .put(Biomes.BADLANDS, SkramItems.BADLANDS)
    .put(Biomes.BAMBOO_JUNGLE, SkramItems.BAMBOO_JUNGLE)
    .put(Biomes.BASALT_DELTAS, SkramItems.BASALT_DELTAS)
    .put(Biomes.BEACH, SkramItems.BEACH)
    .put(Biomes.BIRCH_FOREST, SkramItems.BIRCH_FOREST)
    .put(Biomes.CHERRY_GROVE, SkramItems.CHERRY_GROVE)
    .put(Biomes.COLD_OCEAN, SkramItems.COLD_OCEAN)
    .put(Biomes.CRIMSON_FOREST, SkramItems.CRIMSON_FOREST)
    .put(Biomes.DAPPLED_FOREST, SkramItems.DAPPLED_FOREST)
    .put(Biomes.DARK_FOREST, SkramItems.DARK_FOREST)
    .put(Biomes.DEEP_COLD_OCEAN, SkramItems.DEEP_COLD_OCEAN)
    .put(Biomes.DEEP_DARK, SkramItems.DEEP_DARK)
    .put(Biomes.DEEP_FROZEN_OCEAN, SkramItems.DEEP_FROZEN_OCEAN)
    .put(Biomes.DEEP_LUKEWARM_OCEAN, SkramItems.DEEP_LUKEWARM_OCEAN)
    .put(Biomes.DEEP_OCEAN, SkramItems.DEEP_OCEAN)
    .put(Biomes.DESERT, SkramItems.DESERT)
    .put(Biomes.DRIPSTONE_CAVES, SkramItems.DRIPSTONE_CAVES)
    .put(Biomes.END_BARRENS, SkramItems.END_BARRENS)
    .put(Biomes.END_HIGHLANDS, SkramItems.END_HIGHLANDS)
    .put(Biomes.END_MIDLANDS, SkramItems.END_MIDLANDS)
    .put(Biomes.ERODED_BADLANDS, SkramItems.ERODED_BADLANDS)
    .put(Biomes.FLOWER_FOREST, SkramItems.FLOWER_FOREST)
    .put(Biomes.FOREST, SkramItems.FOREST)
    .put(Biomes.FROZEN_OCEAN, SkramItems.FROZEN_OCEAN)
    .put(Biomes.FROZEN_PEAKS, SkramItems.FROZEN_PEAKS)
    .put(Biomes.FROZEN_RIVER, SkramItems.FROZEN_RIVER)
    .put(Biomes.GROVE, SkramItems.GROVE)
    .put(Biomes.ICE_SPIKES, SkramItems.ICE_SPIKES)
    .put(Biomes.JAGGED_PEAKS, SkramItems.JAGGED_PEAKS)
    .put(Biomes.JUNGLE, SkramItems.JUNGLE)
    .put(Biomes.LUKEWARM_OCEAN, SkramItems.LUKEWARM_OCEAN)
    .put(Biomes.LUSH_CAVES, SkramItems.LUSH_CAVES)
    .put(Biomes.MANGROVE_SWAMP, SkramItems.MANGROVE_SWAMP)
    .put(Biomes.MEADOW, SkramItems.MEADOW)
    .put(Biomes.MUSHROOM_FIELDS, SkramItems.MUSHROOM_FIELDS)
    .put(Biomes.NETHER_WASTES, SkramItems.NETHER_WASTES)
    .put(Biomes.OCEAN, SkramItems.OCEAN)
    .put(Biomes.OLD_GROWTH_BIRCH_FOREST, SkramItems.OLD_GROWTH_BIRCH_FOREST)
    .put(Biomes.OLD_GROWTH_PINE_TAIGA, SkramItems.OLD_GROWTH_PINE_TAIGA)
    .put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, SkramItems.OLD_GROWTH_SPRUCE_TAIGA)
    .put(Biomes.PALE_GARDEN, SkramItems.PALE_GARDEN)
    .put(Biomes.PLAINS, SkramItems.PLAINS)
    .put(Biomes.RIVER, SkramItems.RIVER)
    .put(Biomes.SAVANNA, SkramItems.SAVANNA)
    .put(Biomes.SAVANNA_PLATEAU, SkramItems.SAVANNA_PLATEAU)
    .put(Biomes.SMALL_END_ISLANDS, SkramItems.SMALL_END_ISLANDS)
    .put(Biomes.SNOWY_BEACH, SkramItems.SNOWY_BEACH)
    .put(Biomes.SNOWY_PLAINS, SkramItems.SNOWY_PLAINS)
    .put(Biomes.SNOWY_SLOPES, SkramItems.SNOWY_SLOPES)
    .put(Biomes.SNOWY_TAIGA, SkramItems.SNOWY_TAIGA)
    .put(Biomes.SOUL_SAND_VALLEY, SkramItems.SOUL_SAND_VALLEY)
    .put(Biomes.SPARSE_JUNGLE, SkramItems.SPARSE_JUNGLE)
    .put(Biomes.STONY_PEAKS, SkramItems.STONY_PEAKS)
    .put(Biomes.STONY_SHORE, SkramItems.STONY_SHORE)
    .put(Biomes.SULFUR_CAVES, SkramItems.SULFUR_CAVES)
    .put(Biomes.SUNFLOWER_PLAINS, SkramItems.SUNFLOWER_PLAINS)
    .put(Biomes.SWAMP, SkramItems.SWAMP)
    .put(Biomes.TAIGA, SkramItems.TAIGA)
    .put(Biomes.THE_END, SkramItems.THE_END)
    .put(Biomes.THE_VOID, SkramItems.THE_VOID)
    .put(Biomes.WARM_OCEAN, SkramItems.WARM_OCEAN)
    .put(Biomes.WARPED_FOREST, SkramItems.WARPED_FOREST)
    .put(Biomes.WINDSWEPT_FOREST, SkramItems.WINDSWEPT_FOREST)
    .put(Biomes.WINDSWEPT_GRAVELLY_HILLS, SkramItems.WINDSWEPT_GRAVELLY_HILLS)
    .put(Biomes.WINDSWEPT_HILLS, SkramItems.WINDSWEPT_HILLS)
    .put(Biomes.WINDSWEPT_SAVANNA, SkramItems.WINDSWEPT_SAVANNA)
    .put(Biomes.WOODED_BADLANDS, SkramItems.WOODED_BADLANDS)
    .build();

  public static String titleize(String input) {
    String output = input;
    if (input.startsWith("minecraft")) {
      output = input.split(":")[0];
    }

    StringBuilder builder = new StringBuilder();
    String[] words = output.split("_");
    for (int i = 0; i < words.length; ++i) {
      String word = words[i];
      builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
      if (i < words.length - 1) {
        builder.append(" ");
      }
    }

    return builder.toString();
  }

}
