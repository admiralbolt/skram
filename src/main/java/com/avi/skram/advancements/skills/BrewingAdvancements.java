package com.avi.skram.advancements.skills;

import com.avi.skram.Skram;
import com.avi.skram.advancements.SkramAdvancements;
import com.avi.skram.utils.DataGenerationUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.BrewedPotionTrigger;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.PotionsPredicate;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BrewingAdvancements {

  public static List<Pair<Holder<Potion>, Integer>> BREWING_LOG = List.of(
    Pair.of(Potions.AWKWARD, 1),
    Pair.of(Potions.FIRE_RESISTANCE, 1),
    Pair.of(Potions.HARMING, 1),
    Pair.of(Potions.HEALING, 1),
    Pair.of(Potions.INFESTED, 1),
    Pair.of(Potions.INVISIBILITY, 1),
    Pair.of(Potions.LEAPING, 2),
    Pair.of(Potions.NIGHT_VISION, 2),
    Pair.of(Potions.OOZING, 1),
    Pair.of(Potions.POISON, 1),
    Pair.of(Potions.REGENERATION, 2),
    Pair.of(Potions.SLOW_FALLING, 1),
    Pair.of(Potions.SLOWNESS, 1),
    Pair.of(Potions.STRENGTH, 1),
    Pair.of(Potions.SWIFTNESS, 1),
    Pair.of(Potions.TURTLE_MASTER, 3),
    Pair.of(Potions.WATER_BREATHING, 1),
    Pair.of(Potions.WIND_CHARGED, 2),
    Pair.of(Potions.WEAKNESS, 1),
    Pair.of(Potions.WEAVING, 1)
  );

  public static Set<Holder<Potion>> POTION_NAME_FIRST = Set.of(
    Potions.AWKWARD, Potions.MUNDANE, Potions.THICK
  );


  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    List<AdvancementHolder> itemAdvancements = new ArrayList<>();

    for (Pair<Holder<Potion>, Integer> potionPair : BREWING_LOG) {
      Holder<Potion> potion = potionPair.getFirst();
      Integer xpMult = potionPair.getSecond();

      PotionContents customPotionContents = new PotionContents(
        Optional.empty(), Optional.of(DataGenerationUtils.POTION_COLOR_MAP.get(potion)), List.of(), Optional.empty()
      );
      DataComponentPatch patch = DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, customPotionContents).build();
      ItemStackTemplate iconTemplate = new ItemStackTemplate(Items.POTION, patch);

      // potion.getRegisteredName() looks like minecraft:fire_resistance or minecraft:awkward
      String potionBaseName = potion.getRegisteredName().split(":")[1];
      String potionName;
      if (POTION_NAME_FIRST.contains(potion)) {
        potionName = DataGenerationUtils.titleize(potionBaseName) + " Potion";
      } else {
        potionName = "Potion of " + DataGenerationUtils.titleize(potionBaseName);
      }

      itemAdvancements.add(Advancement.Builder.advancement()
        .parent(itemAdvancements.isEmpty() ? skramRoot : itemAdvancements.getLast())
        .display(
          // new ItemStackTemplate(Items.POTION),
          iconTemplate,
          Component.literal("Brew: " + potionName),
          Component.literal("Brewing this potion grants a +" + xpMult + "% boost to all brewing xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "brewing_xp_mult_up" + xpMult))
        ).addCriterion(
          "brew_thing", CriteriaTriggers.BREWED_POTION.createCriterion(new BrewedPotionTrigger.TriggerInstance(Optional.empty(), Optional.of(PotionsPredicate.ofPotion(potion))))
        )
        .save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("brew_" + potion.getRegisteredName()))));
    }
  }


}
