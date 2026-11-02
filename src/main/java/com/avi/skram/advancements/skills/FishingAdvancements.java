package com.avi.skram.advancements.skills;

import com.avi.skram.Skram;
import com.avi.skram.advancements.SkramAdvancements;
import com.avi.skram.utils.DataGenerationUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.FishingRodHookedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FishingAdvancements {

  public static List<Pair<Item, Integer>> FISHING_LOG = List.of(
    Pair.of(Items.BONE, 1),
    Pair.of(Items.BOW, 1),
    Pair.of(Items.BOWL, 1),
    Pair.of(Items.COD, 1),
    Pair.of(Items.ENCHANTED_BOOK, 1),
    Pair.of(Items.FISHING_ROD, 1),
    Pair.of(Items.LEATHER, 1),
    Pair.of(Items.LEATHER_BOOTS, 1),
    Pair.of(Items.INK_SAC, 1),
    Pair.of(Items.NAME_TAG, 1),
    Pair.of(Items.LILY_PAD, 1),
    Pair.of(Items.NAUTILUS_SHELL, 1),
    Pair.of(Items.PUFFERFISH, 1),
    Pair.of(Items.ROTTEN_FLESH, 1),
    Pair.of(Items.SADDLE, 1),
    Pair.of(Items.SALMON, 1),
    Pair.of(Items.STICK, 1),
    Pair.of(Items.STRING, 1),
    Pair.of(Items.TRIPWIRE_HOOK, 1),
    Pair.of(Items.TROPICAL_FISH, 1),
    Pair.of(Items.POTION, 1)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    HolderGetter<Item> items = output.lookup(Registries.ITEM);
    HolderGetter<Recipe<?>> recipes = output.lookup(Registries.RECIPE);
    List<AdvancementHolder> itemAdvancements = new ArrayList<>();

    for (Pair<Item, Integer> itemPair : FISHING_LOG) {
      Item item = itemPair.getFirst();
      Integer xpMult = itemPair.getSecond();

      // item.getDescriptionId() looks like item.minecraft.name
      String itemBaseName = Arrays.stream(item.getDescriptionId().split("\\.")).skip(2).collect(Collectors.joining("_"));
      String itemName = DataGenerationUtils.titleize(itemBaseName);
      itemAdvancements.add(Advancement.Builder.advancement()
        .parent(itemAdvancements.isEmpty() ? skramRoot : itemAdvancements.getLast())
        .display(
          new ItemStackTemplate(item),
          Component.literal("Fish: " + itemName),
          Component.literal("Fishing this item grants a +" + xpMult + "% boost to all fishing xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "fishing_xp_mult_up" + xpMult))
        ).addCriterion(
          "fish_thing", FishingRodHookedTrigger.TriggerInstance.fishedItem(
            Optional.empty(), Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(items, item).build())
          )
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("fish_" + item.getDescriptionId()))));
    }
  }
}
