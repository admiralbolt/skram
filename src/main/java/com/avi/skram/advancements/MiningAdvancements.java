package com.avi.skram.advancements;

import com.avi.skram.Skram;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class MiningAdvancements extends AdvancementSubProvider {

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

  private final HolderGetter<Block> blocks;

  protected MiningAdvancements(BootstrapContext<Advancement> output) {
    super(output);
    this.blocks = output.lookup(Registries.BLOCK);
  }

  @Override
  public void generate() {
    @SuppressWarnings("removal") AdvancementHolder example = Advancement.Builder.advancement()
      .rootDisplay(
        new ItemStackTemplate(Items.GRASS_BLOCK),
        Component.literal("Test Title"),
        Component.literal("Test description text. This can go on for a little while, just to see what it looks like. Line wrapping and all that."),
        Identifier.withDefaultNamespace("gui/advancements/background/stone"),
        AdvancementType.GOAL,
        true,
        true,
        false
      ).rewards(
        AdvancementRewards.Builder.experience(100)
      ).addCriterion(
        "pickup_dirt",
        InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIRT)
      ).save(this.output, Identifier.fromNamespaceAndPath(Skram.MODID, "example_advancement"));

    List<AdvancementHolder> breakAdvancements = new ArrayList<>();

    for (Pair<Block, Integer> blockPair : MINING_LOG) {
      Block block = blockPair.getFirst();
      Integer xpMult = blockPair.getSecond();
      breakAdvancements.add(Advancement.Builder.advancement()
        .parent(breakAdvancements.isEmpty() ? example : breakAdvancements.getLast())
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
          BreakBlockTrigger.TriggerInstance.breaksBlock(this.blocks, block)
        ).save(this.output, Identifier.fromNamespaceAndPath(Skram.MODID, prettify("mine_" + block.getName().getString()))));
    }
  }

  public static String prettify(String input) {
    return input.toLowerCase().replace(".", "_").replace(" ", "_");
  }

}
