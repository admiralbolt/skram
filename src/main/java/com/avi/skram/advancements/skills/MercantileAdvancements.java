package com.avi.skram.advancements.skills;

import com.avi.skram.Skram;
import com.avi.skram.advancements.ArbitraryStringTrigger;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class MercantileAdvancements {


  public static List<Pair<ResourceKey<VillagerProfession>, Integer>> TRADE_LOG = List.of(
    Pair.of(VillagerProfession.ARMORER, 1),
    Pair.of(VillagerProfession.BUTCHER, 1),
    Pair.of(VillagerProfession.CARTOGRAPHER, 1),
    Pair.of(VillagerProfession.CLERIC, 1),
    Pair.of(VillagerProfession.FARMER, 1),
    Pair.of(VillagerProfession.FISHERMAN, 1),
    Pair.of(VillagerProfession.FLETCHER, 1),
    Pair.of(VillagerProfession.LEATHERWORKER, 1),
    Pair.of(VillagerProfession.LIBRARIAN, 1),
    Pair.of(VillagerProfession.MASON, 1),
    Pair.of(VillagerProfession.SHEPHERD, 1),
    Pair.of(VillagerProfession.TOOLSMITH, 1),
    Pair.of(VillagerProfession.WEAPONSMITH, 1)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    List<AdvancementHolder> mobAdvancements = new ArrayList<>();

    for (Pair<ResourceKey<VillagerProfession>, Integer> entityPair : TRADE_LOG) {
      ResourceKey<VillagerProfession> villagerProfession = entityPair.getFirst();
      Integer xpMult = entityPair.getSecond();
      mobAdvancements.add(Advancement.Builder.advancement()
        .parent(mobAdvancements.isEmpty() ? skramRoot : mobAdvancements.getLast())
        .display(
          new ItemStackTemplate(Items.VILLAGER_SPAWN_EGG),
          Component.literal("Trade With: " + DataGenerationUtils.titleize(villagerProfession.identifier().toShortString())),
          Component.literal("Trading with this villager grants a +" + xpMult + "% boost to all mercantile xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "mercantile_xp_mult_up" + xpMult))
        ).addCriterion(
          "arbitrary_string",
          ArbitraryStringTrigger.TriggerInstance.make("traded_with_" + villagerProfession.identifier().toShortString())
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("mercantile_" + villagerProfession.identifier().toShortString()))));
    }
  }
}
