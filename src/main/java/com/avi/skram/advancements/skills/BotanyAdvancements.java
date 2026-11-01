package com.avi.skram.advancements.skills;

import com.avi.skram.Skram;
import com.avi.skram.advancements.BreakBlockTrigger;
import com.avi.skram.advancements.SkramAdvancements;
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

public class BotanyAdvancements {

  public static final List<Pair<Block, Integer>> FLOWER_LOG = List.of(
    Pair.of(Blocks.ALLIUM, 1),
    Pair.of(Blocks.AZURE_BLUET, 1),
    Pair.of(Blocks.BLUE_ORCHID, 1),
    Pair.of(Blocks.CLOSED_EYEBLOSSOM, 1),
    Pair.of(Blocks.CORNFLOWER, 1),
    Pair.of(Blocks.DANDELION, 1),
    Pair.of(Blocks.LILAC, 2),
    Pair.of(Blocks.LILY_OF_THE_VALLEY, 2),
    Pair.of(Blocks.OPEN_EYEBLOSSOM, 1),
    Pair.of(Blocks.OXEYE_DAISY, 1),
    Pair.of(Blocks.ORANGE_TULIP, 1),
    Pair.of(Blocks.PEONY, 2),
    Pair.of(Blocks.PINK_TULIP, 1),
    Pair.of(Blocks.PITCHER_PLANT, 2),
    Pair.of(Blocks.POPPY, 1),
    Pair.of(Blocks.RED_TULIP, 1),
    Pair.of(Blocks.ROSE_BUSH, 2),
    Pair.of(Blocks.SUNFLOWER, 3),
    Pair.of(Blocks.TORCHFLOWER, 1),
    Pair.of(Blocks.WHITE_TULIP, 1),
    Pair.of(Blocks.WILDFLOWERS, 1)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    HolderGetter<Block> blocks = output.lookup(Registries.BLOCK);
    List<AdvancementHolder> breakAdvancements = new ArrayList<>();

    for (Pair<Block, Integer> blockPair : FLOWER_LOG) {
      Block block = blockPair.getFirst();
      Integer xpMult = blockPair.getSecond();
      breakAdvancements.add(Advancement.Builder.advancement()
        .parent(breakAdvancements.isEmpty() ? skramRoot : breakAdvancements.getLast())
        .display(
          new ItemStackTemplate(block.asItem()),
          Component.literal("Gather: " + block.getName().getString()),
          Component.literal("Gathering this block grants a permanent +" + xpMult + "% boost to all botany xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "botany_xp_mult_up" + xpMult))
        ).addCriterion(
          "gather_block",
          BreakBlockTrigger.TriggerInstance.breaksBlock(blocks, block)
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("gather_" + block.getName().getString()))));
    }
  }
}
