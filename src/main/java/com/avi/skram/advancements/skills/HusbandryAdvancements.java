package com.avi.skram.advancements.skills;

import com.avi.skram.Skram;
import com.avi.skram.advancements.SkramAdvancements;
import com.avi.skram.utils.DataGenerationUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.BredAnimalsTrigger;
import net.minecraft.advancements.triggers.TameAnimalTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;

public class HusbandryAdvancements {

  public static List<Pair<EntityType<?>, Integer>> BREED_LOG = List.of(
    Pair.of(EntityTypes.ARMADILLO, 1),
    Pair.of(EntityTypes.AXOLOTL, 1),
    Pair.of(EntityTypes.BEE, 1),
    Pair.of(EntityTypes.CAMEL, 1),
    Pair.of(EntityTypes.CAT, 1),
    Pair.of(EntityTypes.CHICKEN, 1),
    Pair.of(EntityTypes.COW, 1),
    Pair.of(EntityTypes.DONKEY, 1),
    Pair.of(EntityTypes.FOX, 1),
    Pair.of(EntityTypes.FROG, 1),
    Pair.of(EntityTypes.GOAT, 1),
    Pair.of(EntityTypes.HOGLIN, 2),
    Pair.of(EntityTypes.HORSE, 1),
    Pair.of(EntityTypes.LLAMA, 1),
    Pair.of(EntityTypes.MOOSHROOM, 3),
    Pair.of(EntityTypes.MULE, 1),
    Pair.of(EntityTypes.OCELOT, 1),
    Pair.of(EntityTypes.PANDA, 1),
    Pair.of(EntityTypes.PIG, 1),
    Pair.of(EntityTypes.RABBIT, 1),
    Pair.of(EntityTypes.SHEEP, 1),
    Pair.of(EntityTypes.SNIFFER, 1),
    Pair.of(EntityTypes.STRIDER, 1),
    Pair.of(EntityTypes.TRADER_LLAMA, 2),
    Pair.of(EntityTypes.TURTLE, 1),
    Pair.of(EntityTypes.WOLF, 1)
  );

  public static List<Pair<EntityType<?>, Integer>> TAME_LOG = List.of(
    Pair.of(EntityTypes.ALLAY, 1),
    Pair.of(EntityTypes.AXOLOTL, 1),
    Pair.of(EntityTypes.CAMEL, 1),
    Pair.of(EntityTypes.CAT, 1),
    Pair.of(EntityTypes.DONKEY, 1),
    Pair.of(EntityTypes.FOX, 1),
    Pair.of(EntityTypes.HORSE, 1),
    Pair.of(EntityTypes.LLAMA, 1),
    Pair.of(EntityTypes.TRADER_LLAMA, 1),
    Pair.of(EntityTypes.MULE, 1),
    Pair.of(EntityTypes.OCELOT, 1),
    Pair.of(EntityTypes.PARROT, 1),
    Pair.of(EntityTypes.SKELETON_HORSE, 1),
    Pair.of(EntityTypes.WOLF, 1)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    List<AdvancementHolder> breedAdvancements = new ArrayList<>();
    List<AdvancementHolder> tameAdvancements = new ArrayList<>();

    HolderGetter<EntityType<?>> entityTypes = output.lookup(Registries.ENTITY_TYPE);

    for (Pair<EntityType<?>, Integer> entityPair : BREED_LOG) {
      EntityType<?> entityType = entityPair.getFirst();
      Integer xpMult = entityPair.getSecond();

      breedAdvancements.add(Advancement.Builder.advancement()
        .parent(breedAdvancements.isEmpty() ? skramRoot : breedAdvancements.getLast())
        .display(
          new ItemStackTemplate(DataGenerationUtils.ENTITY_TO_SPAWN_EGG.get(entityType)),
          Component.literal("Breed: " + entityType.getDescription().getString()),
          Component.literal("Breeding this entity grants a permanent +" + xpMult + "% boost to all husbandry xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "husbandry_xp_mult_up" + xpMult))
        ).addCriterion(
          "breed_entity",
          BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(entityTypes, entityType))
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("breed_" + entityType.getDescriptionId()))));
    }

    for (Pair<EntityType<?>, Integer> entityPair : TAME_LOG) {
      EntityType<?> entityType = entityPair.getFirst();
      Integer xpMult = entityPair.getSecond();

      tameAdvancements.add(Advancement.Builder.advancement()
        .parent(tameAdvancements.isEmpty() ? breedAdvancements.getLast() : tameAdvancements.getLast())
        .display(
          new ItemStackTemplate(DataGenerationUtils.ENTITY_TO_SPAWN_EGG.get(entityType)),
          Component.literal("Tame: " + entityType.getDescription().getString()),
          Component.literal("Taming this entity grants a permanent +" + xpMult + "% boost to all husbandry xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "husbandry_xp_mult_up" + xpMult))
        ).addCriterion(
          "tame_entity",
          TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(entityTypes, entityType))
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("tame_" + entityType.getDescriptionId()))));
    }
  }

}