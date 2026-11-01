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

public class MiningAdvancements {

  // Unfortunately we are defining a giant list here, because tags aren't available.
  // If they are available, I can't figure out how to access them.
  // Besides, it appears that the VanillaHusbandryAdvancements does this anyway.
  public static final List<Pair<Block, Integer>> MINING_LOG = List.of(
    Pair.of(Blocks.ANCIENT_DEBRIS, 3),
    Pair.of(Blocks.ANDESITE, 1),
    Pair.of(Blocks.BASALT, 1),
    Pair.of(Blocks.BLACKSTONE, 1),
    Pair.of(Blocks.CINNABAR, 1),
    Pair.of(Blocks.COAL_ORE, 1),
    Pair.of(Blocks.COBBLESTONE, 1),
    Pair.of(Blocks.COPPER_ORE, 1),
    Pair.of(Blocks.CRYING_OBSIDIAN, 3),
    Pair.of(Blocks.DEEPSLATE, 1),
    Pair.of(Blocks.DEEPSLATE_COAL_ORE, 1),
    Pair.of(Blocks.DEEPSLATE_COPPER_ORE, 1),
    Pair.of(Blocks.DEEPSLATE_DIAMOND_ORE, 2),
    Pair.of(Blocks.DEEPSLATE_EMERALD_ORE, 2),
    Pair.of(Blocks.DEEPSLATE_GOLD_ORE, 1),
    Pair.of(Blocks.DEEPSLATE_IRON_ORE, 1),
    Pair.of(Blocks.DEEPSLATE_LAPIS_ORE, 1),
    Pair.of(Blocks.DEEPSLATE_REDSTONE_ORE, 1),
    Pair.of(Blocks.DIAMOND_ORE, 2),
    Pair.of(Blocks.DIORITE, 1),
    Pair.of(Blocks.DRIPSTONE_BLOCK, 1),
    Pair.of(Blocks.EMERALD_ORE, 2),
    Pair.of(Blocks.GOLD_ORE, 1),
    Pair.of(Blocks.GRANITE, 1),
    Pair.of(Blocks.INFESTED_COBBLESTONE, 2),
    Pair.of(Blocks.IRON_ORE, 1),
    Pair.of(Blocks.LAPIS_ORE, 1),
    Pair.of(Blocks.MOSSY_COBBLESTONE, 1),
    Pair.of(Blocks.NETHER_GOLD_ORE, 1),
    Pair.of(Blocks.OBSIDIAN, 2),
    Pair.of(Blocks.REDSTONE_ORE, 1),
    Pair.of(Blocks.SANDSTONE, 1),
    Pair.of(Blocks.STONE, 1),
    Pair.of(Blocks.SULFUR, 1),
    Pair.of(Blocks.TUFF, 1)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    HolderGetter<Block> blocks = output.lookup(Registries.BLOCK);
    List<AdvancementHolder> breakAdvancements = new ArrayList<>();

    for (Pair<Block, Integer> blockPair : MINING_LOG) {
      Block block = blockPair.getFirst();
      Integer xpMult = blockPair.getSecond();
      breakAdvancements.add(Advancement.Builder.advancement()
        .parent(breakAdvancements.isEmpty() ? skramRoot : breakAdvancements.getLast())
        .display(
          new ItemStackTemplate(block.asItem()),
          Component.literal("Mine: " + block.getName().getString()),
          Component.literal("Mining this block grants a permanent +" + xpMult + "% boost to all mining xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "mining_xp_mult_up" + xpMult))
        ).addCriterion(
          "mine_block",
          BreakBlockTrigger.TriggerInstance.breaksBlock(blocks, block)
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("mine_" + block.getName().getString()))));
    }
  }
}
