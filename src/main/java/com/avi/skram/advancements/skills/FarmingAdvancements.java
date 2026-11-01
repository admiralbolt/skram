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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class FarmingAdvancements {

  public static final List<Pair<Block, Integer>> FARMING_LOG = List.of(
    Pair.of(Blocks.BAMBOO, 1),
    Pair.of(Blocks.BEETROOTS, 1),
    Pair.of(Blocks.BROWN_MUSHROOM, 1),
    Pair.of(Blocks.CARROTS, 1),
    Pair.of(Blocks.CAVE_VINES_PLANT, 2),
    Pair.of(Blocks.CHORUS_FLOWER, 3),
    Pair.of(Blocks.COCOA, 1),
    Pair.of(Blocks.CRIMSON_FUNGUS, 3),
    Pair.of(Blocks.MELON, 2),
    Pair.of(Blocks.NETHER_WART, 3),
    Pair.of(Blocks.PITCHER_CROP, 1),
    Pair.of(Blocks.POTATOES, 1),
    Pair.of(Blocks.PUMPKIN, 2),
    Pair.of(Blocks.RED_MUSHROOM, 1),
    Pair.of(Blocks.SUGAR_CANE, 1),
    Pair.of(Blocks.SWEET_BERRY_BUSH, 2),
    Pair.of(Blocks.TORCHFLOWER_CROP, 2),
    Pair.of(Blocks.WARPED_FUNGUS, 3),
    Pair.of(Blocks.WHEAT, 1)
  );

  public static void generate(BootstrapContext<Advancement> output, AdvancementHolder skramRoot) {
    HolderGetter<Block> blocks = output.lookup(Registries.BLOCK);
    List<AdvancementHolder> breakAdvancements = new ArrayList<>();

    for (Pair<Block, Integer> blockPair : FARMING_LOG) {
      Block block = blockPair.getFirst();
      Integer xpMult = blockPair.getSecond();
      breakAdvancements.add(Advancement.Builder.advancement()
        .parent(breakAdvancements.isEmpty() ? skramRoot : breakAdvancements.getLast())
        .display(
          new ItemStackTemplate((block.asItem() == Items.AIR) ? Items.GLOW_BERRIES : block.asItem()),
          Component.literal("Farm: " + block.getName().getString()),
          Component.literal("Farming this block grants a permanent +" + xpMult + "% boost to all farming xp."),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "farming_xp_mult_up" + xpMult))
        ).addCriterion(
          "farm_block",
          BreakBlockTrigger.TriggerInstance.breaksBlock(blocks, block)
        ).save(output, Identifier.fromNamespaceAndPath(Skram.MODID, SkramAdvancements.prettify("farm_" + block.getName().getString()))));
    }
  }
}
