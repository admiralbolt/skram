package com.avi.skram.advancements;

import com.avi.skram.Skram;
import com.avi.skram.advancements.skills.*;
import com.avi.skram.item.SkramItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

import java.util.List;

// This is mostly just advancements, but with some added tracking to give skill XP bonuses.
@EventBusSubscriber(modid = Skram.MODID)
public class SkramAdvancements {

  @SubscribeEvent
  public static void onGatherRegistries(GatherDataRegistryEntriesEvent event) {
    event.add(Registries.ADVANCEMENT, bootstrap -> {
      new AdvancementProvider(
        List.of(SkramAdvancementSubProvider::new)
      );
    });

    event.gatherFor("minecraft", Skram.MODID)
      .advancement(SkramAdvancementSubProvider::new);
  }

  public static class SkramAdvancementSubProvider extends AdvancementSubProvider {

    public AdvancementHolder skramRoot;

    protected SkramAdvancementSubProvider(BootstrapContext<Advancement> output) {
      super(output);
    }

    @Override
    public void generate() {
      this.skramRoot = Advancement.Builder.advancement()
        .rootDisplay(
          new ItemStackTemplate(SkramItems.AWESOME_SNAKE),
          Component.literal("Skram"),
          Component.literal("We ride again."),
          Identifier.fromNamespaceAndPath(Skram.MODID, "ui/advancement_background"),
          AdvancementType.GOAL,
          true,
          true,
          false
        ).rewards(
          AdvancementRewards.Builder.experience(100)
        ).addCriterion(
          "pickup_dirt",
          InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIRT)
        ).save(this.output, Identifier.fromNamespaceAndPath(Skram.MODID, "skram_root"));

      BotanyAdvancements.generate(this.output, this.skramRoot);
      DiggingAdvancements.generate(this.output, this.skramRoot);
      FarmingAdvancements.generate(this.output, this.skramRoot);
      MeleeAdvancements.generate(this.output, this.skramRoot);
      MiningAdvancements.generate(this.output, this.skramRoot);
      RangedAdvancements.generate(this.output, this.skramRoot);
      WoodcuttingAdvancements.generate(this.output, this.skramRoot);
    }
  }

  public static String prettify(String input) {
    return input.toLowerCase().replace(".", "_").replace(" ", "_");
  }

}
