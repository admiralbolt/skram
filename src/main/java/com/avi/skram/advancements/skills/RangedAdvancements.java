package com.avi.skram.advancements.skills;

import com.avi.skram.Skram;
import com.avi.skram.advancements.SkramAdvancements;
import com.avi.skram.utils.DataGenerationUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.DamageSourcePredicate;
import net.minecraft.advancements.predicates.TagPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.KilledTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;

public class RangedAdvancements {

  public static List<Pair<EntityType<?>, Integer>> MOB_LOG = List.of(
    Pair.of(EntityTypes.BLAZE, 1),
    Pair.of(EntityTypes.BOGGED, 1),
    Pair.of(EntityTypes.BREEZE, 1),
    Pair.of(EntityTypes.CAMEL_HUSK, 1),
    Pair.of(EntityTypes.CAVE_SPIDER, 1),
    Pair.of(EntityTypes.CREAKING, 1),
    Pair.of(EntityTypes.CREEPER, 1),
    Pair.of(EntityTypes.DROWNED, 1),
    Pair.of(EntityTypes.ELDER_GUARDIAN, 2),
    Pair.of(EntityTypes.ENDER_DRAGON, 3),
    Pair.of(EntityTypes.ENDERMAN, 1),
    Pair.of(EntityTypes.ENDERMITE, 1),
    Pair.of(EntityTypes.EVOKER, 1),
    Pair.of(EntityTypes.GHAST, 1),
    Pair.of(EntityTypes.GUARDIAN, 1),
    Pair.of(EntityTypes.HOGLIN, 1),
    Pair.of(EntityTypes.HUSK, 1),
    Pair.of(EntityTypes.MAGMA_CUBE, 1),
    Pair.of(EntityTypes.PARCHED, 1),
    Pair.of(EntityTypes.PHANTOM, 1),
    Pair.of(EntityTypes.PIGLIN, 1),
    Pair.of(EntityTypes.PIGLIN_BRUTE, 1),
    Pair.of(EntityTypes.PILLAGER, 1),
    Pair.of(EntityTypes.RAVAGER, 1),
    Pair.of(EntityTypes.SHULKER, 1),
    Pair.of(EntityTypes.SILVERFISH, 1),
    Pair.of(EntityTypes.SKELETON, 1),
    Pair.of(EntityTypes.SLIME, 1),
    Pair.of(EntityTypes.SPIDER, 1),
    Pair.of(EntityTypes.STRAY, 1),
    Pair.of(EntityTypes.VEX, 1),
    Pair.of(EntityTypes.VINDICATOR, 1),
    Pair.of(EntityTypes.WITCH, 1),
    Pair.of(EntityTypes.WITHER_SKELETON, 1),
    Pair.of(EntityTypes.WITHER, 3),
    Pair.of(EntityTypes.ZOGLIN, 1),
    Pair.of(EntityTypes.ZOMBIE_VILLAGER, 1),
    Pair.of(EntityTypes.ZOMBIE, 1),
    Pair.of(EntityTypes.ZOMBIE_HORSE, 1),
    Pair.of(EntityTypes.ZOMBIFIED_PIGLIN, 1),
    Pair.of(EntityTypes.ZOMBIE_NAUTILUS, 1)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    List<AdvancementHolder> mobAdvancements = new ArrayList<>();
    HolderGetter<EntityType<?>> entityTypes = output.lookup(Registries.ENTITY_TYPE);
    HolderGetter<DamageType> damageTypes = output.lookup(Registries.DAMAGE_TYPE);

    for (Pair<EntityType<?>, Integer> entityPair : MOB_LOG) {
      EntityType<?> entityType = entityPair.getFirst();
      Integer xpMult = entityPair.getSecond();
      mobAdvancements.add(Advancement.Builder.advancement()
        .parent(mobAdvancements.isEmpty() ? skramRoot : mobAdvancements.getLast())
        .display(
          new ItemStackTemplate(DataGenerationUtils.ENTITY_TO_SPAWN_EGG.get(entityType)),
          Component.literal("Snipe: " + entityType.getDescription().getString()),
          Component.literal("Slaying this entity (with a projectile) grants a permanent +" + xpMult + "% boost to all ranged xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "ranged_xp_mult_up" + xpMult))
        ).addCriterion(
          "ranged_entity",
          KilledTrigger.TriggerInstance.playerKilledEntity(
            EntityPredicate.Builder.entity().of(entityTypes, entityType),
            DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(damageTypes, DamageTypeTags.IS_PROJECTILE))
          )
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("ranged_" + entityType.getDescriptionId()))));
    }
  }
}
