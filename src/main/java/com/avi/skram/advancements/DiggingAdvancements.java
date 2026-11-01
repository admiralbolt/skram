package com.avi.skram.advancements;

import com.avi.skram.Skram;
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
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class DiggingAdvancements {

  public static final List<Pair<Block, Integer>> DIGGING_LOG = List.of(
    Pair.of(Blocks.CLAY, 1),
    Pair.of(Blocks.COARSE_DIRT, 1),
    Pair.of(Blocks.DIRT, 1),
    Pair.of(Blocks.DIRT_PATH, 1),
    Pair.of(Blocks.FARMLAND, 1),
    Pair.of(Blocks.GRASS_BLOCK, 1),
    Pair.of(Blocks.GRAVEL, 1),
    Pair.of(Blocks.MUD, 2),
    Pair.of(Blocks.MYCELIUM, 3),
    Pair.of(Blocks.PODZOL, 2),
    Pair.of(Blocks.RED_SAND, 2),
    Pair.of(Blocks.ROOTED_DIRT, 2),
    Pair.of(Blocks.SAND, 1),
    Pair.of(Blocks.SUSPICIOUS_GRAVEL, 3),
    Pair.of(Blocks.SUSPICIOUS_SAND, 3)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    HolderGetter<Block> blocks = output.lookup(Registries.BLOCK);
    List<AdvancementHolder> breakAdvancements = new ArrayList<>();

    for (Pair<Block, Integer> blockPair : DIGGING_LOG) {
      Block block = blockPair.getFirst();
      Integer xpMult = blockPair.getSecond();
      breakAdvancements.add(Advancement.Builder.advancement()
        .parent(breakAdvancements.isEmpty() ? skramRoot : breakAdvancements.getLast())
        .display(
          new ItemStackTemplate(block.asItem()),
          Component.literal("Dig: " + block.getName().getString()),
          Component.literal("Digging this block grants a permanent +" + xpMult + "% boost to all digging xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "digging_xp_mult_up" + xpMult))
        ).addCriterion(
          "dig_block",
          BreakBlockTrigger.TriggerInstance.breaksBlock(blocks, block)
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("dig_" + block.getName().getString()))));
    }
  }
}
