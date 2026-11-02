package com.avi.skram.advancements.skills;

import com.avi.skram.Skram;
import com.avi.skram.advancements.ItemSmeltedTrigger;
import com.avi.skram.advancements.SkramAdvancements;
import com.avi.skram.utils.DataGenerationUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SmithingAdvancements {

  public static final List<Pair<Item, Integer>> SMITHING_LOG = List.of(
    Pair.of(Items.BRICK, 1),
    Pair.of(Items.CHARCOAL, 1),
    Pair.of(Items.COPPER_INGOT, 1),
    Pair.of(Items.CRACKED_DEEPSLATE_BRICKS, 1),
    Pair.of(Items.CRACKED_DEEPSLATE_TILES, 1),
    Pair.of(Items.CRACKED_NETHER_BRICKS, 1),
    Pair.of(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS, 1),
    Pair.of(Items.CRACKED_STONE_BRICKS, 1),
    Pair.of(Items.DEEPSLATE, 1),
    Pair.of(Items.GLASS, 1),
    Pair.of(Items.IRON_INGOT, 1),
    Pair.of(Items.IRON_NUGGET, 1),
    Pair.of(Items.GOLD_INGOT, 1),
    Pair.of(Items.GOLD_NUGGET, 1),
    Pair.of(Items.NETHER_BRICK, 2),
    Pair.of(Items.NETHERITE_SCRAP, 3),
    Pair.of(Items.SMOOTH_BASALT, 1),
    Pair.of(Items.SMOOTH_QUARTZ, 1),
    Pair.of(Items.SMOOTH_RED_SANDSTONE, 2),
    Pair.of(Items.SMOOTH_SANDSTONE, 1),
    Pair.of(Items.SMOOTH_STONE, 1),
    Pair.of(Items.STONE, 1),
    Pair.of(Items.TERRACOTTA, 1)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    HolderGetter<Item> items = output.lookup(Registries.ITEM);
    List<AdvancementHolder> itemAdvancements = new ArrayList<>();

    for (Pair<Item, Integer> itemPair : SMITHING_LOG) {
      Item item = itemPair.getFirst();
      Integer xpMult = itemPair.getSecond();

      String itemName = DataGenerationUtils.titleize(Arrays.stream(item.getDescriptionId().split("\\.")).skip(2).collect(Collectors.joining(" ")));
      itemAdvancements.add(Advancement.Builder.advancement()
        .parent(itemAdvancements.isEmpty() ? skramRoot : itemAdvancements.getLast())
        .display(
          new ItemStackTemplate(item),
          Component.literal("Smelt: " + itemName),
          Component.literal("Smelting this item grants a permanent +" + xpMult + "% boost to all smithing xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "smithing_xp_mult_up" + xpMult))
        ).addCriterion(
          "smelt_thing",
          ItemSmeltedTrigger.TriggerInstance.isItem(items, item)
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("smelt_" + item.getDescriptionId()))));
    }
  }
}
