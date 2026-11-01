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

public class WoodcuttingAdvancements {

  public static final List<Pair<Block, Integer>> WOODCUTTING_LOG = List.of(
    Pair.of(Blocks.ACACIA_LOG, 2),
    Pair.of(Blocks.BIRCH_LOG, 2),
    Pair.of(Blocks.CHERRY_LOG, 2),
    Pair.of(Blocks.CRIMSON_STEM, 3),
    Pair.of(Blocks.DARK_OAK_LOG, 2),
    Pair.of(Blocks.JUNGLE_LOG, 2),
    Pair.of(Blocks.MANGROVE_LOG, 2),
    Pair.of(Blocks.OAK_LOG, 1),
    Pair.of(Blocks.PALE_OAK_LOG, 2),
    Pair.of(Blocks.POPLAR_LOG, 2),
    Pair.of(Blocks.SPRUCE_LOG, 2),
    Pair.of(Blocks.WARPED_STEM, 3)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    HolderGetter<Block> blocks = output.lookup(Registries.BLOCK);
    List<AdvancementHolder> breakAdvancements = new ArrayList<>();

    for (Pair<Block, Integer> blockPair : WOODCUTTING_LOG) {
      Block block = blockPair.getFirst();
      Integer xpMult = blockPair.getSecond();
      breakAdvancements.add(Advancement.Builder.advancement()
        .parent(breakAdvancements.isEmpty() ? skramRoot : breakAdvancements.getLast())
        .display(
          new ItemStackTemplate(block.asItem()),
          Component.literal("Chop: " + block.getName().getString()),
          Component.literal("Chopping this block grants a permanent +" + xpMult + "% boost to all woodcutting xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "woodcutting_xp_mult_up" + xpMult))
        ).addCriterion(
          "chop_block",
          BreakBlockTrigger.TriggerInstance.breaksBlock(blocks, block)
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("chop_" + block.getName().getString()))));
    }
  }
}
