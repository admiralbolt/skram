package com.avi.skram.advancements;

import com.avi.skram.Skram;
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

public class MiningJournal extends AdvancementSubProvider {

  // Unfortunately we are defining a giant list here, because tags aren't available.
  // If they are available, I can't figure out how to access them.
  // Besides, it appears that the VanillaHusbandryAdvancements does this anyway.
  public static final List<Block> MINING_LOG = List.of(
    Blocks.STONE,
    Blocks.COBBLESTONE,
    Blocks.INFESTED_COBBLESTONE,
    Blocks.MOSSY_COBBLESTONE,
    Blocks.GRANITE,
    Blocks.ANDESITE,
    Blocks.DRIPSTONE_BLOCK,
    Blocks.REDSTONE_ORE,
    Blocks.COAL_ORE,
    Blocks.COPPER_ORE,
    Blocks.IRON_ORE,
    Blocks.GOLD_ORE,
    Blocks.DIAMOND_ORE,
    Blocks.LAPIS_ORE,
    Blocks.EMERALD_ORE,
    Blocks.DEEPSLATE_COAL_ORE,
    Blocks.DEEPSLATE_COPPER_ORE,
    Blocks.DEEPSLATE_IRON_ORE,
    Blocks.DEEPSLATE_GOLD_ORE,
    Blocks.DEEPSLATE_DIAMOND_ORE,
    Blocks.DEEPSLATE_LAPIS_ORE,
    Blocks.DEEPSLATE_EMERALD_ORE,
    Blocks.OBSIDIAN,
    Blocks.CRYING_OBSIDIAN
    );

  private final HolderGetter<Block> blocks;

  protected MiningJournal(BootstrapContext<Advancement> output) {
    super(output);
    this.blocks = output.lookup(Registries.BLOCK);
  }

  @Override
  public void generate() {
    System.out.println("GENERATING_MINING Journal is running.");
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

    for (Block block : MINING_LOG) {
      breakAdvancements.add(Advancement.Builder.advancement()
        .parent(breakAdvancements.isEmpty() ? example : breakAdvancements.getLast())
        .display(
          new ItemStackTemplate(block.asItem()),
          Component.literal("Break: " + block.getName().getString()),
          Component.literal("Holy shit"),
          AdvancementType.TASK,
          true,
          false,
          false
        ).rewards(
          AdvancementRewards.Builder.function(Identifier.fromNamespaceAndPath(Skram.MODID, "mining_xp_mult_up1"))
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
