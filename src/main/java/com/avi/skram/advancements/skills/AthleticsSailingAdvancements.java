package com.avi.skram.advancements.skills;

import com.avi.skram.Skram;
import com.avi.skram.advancements.SkramAdvancements;
import com.avi.skram.utils.DataGenerationUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AthleticsSailingAdvancements {

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    HolderGetter<Biome> biomes = output.lookup(Registries.BIOME);
    List<AdvancementHolder> biomeAdvancements = new ArrayList<>();


    List<ResourceKey<Biome>> allBiomes = Stream.concat(MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD.usedBiomes(), MultiNoiseBiomeSourceParameterList.Preset.NETHER.usedBiomes()).toList();
    for (ResourceKey<Biome> biome : allBiomes.stream().sorted().toList()) {
      biomeAdvancements.add(Advancement.Builder.advancement()
        .parent(biomeAdvancements.isEmpty() ? skramRoot : biomeAdvancements.getLast())
        .display(
          new ItemStackTemplate(DataGenerationUtils.BIOME_TO_ITEM.get(biome)),
          Component.literal("Discover: " + DataGenerationUtils.titleize(biome.identifier().toShortString())),
          Component.literal("Discovering this biome grants a permanent +1% boost to all athletics & sailing xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "athletics_and_sailing_xp_mult_up1"))
        ).addCriterion(
          "discover_biome",
          PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inBiome(biomes.getOrThrow(biome)))
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("discover_" + biome.identifier().toShortString()))));
    }
  }
}
