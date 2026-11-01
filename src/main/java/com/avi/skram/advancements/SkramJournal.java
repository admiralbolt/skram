package com.avi.skram.advancements;

import com.avi.skram.Skram;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

import java.util.List;

// This is mostly just advancements, but with some added tracking to give skill XP bonuses.
@EventBusSubscriber(modid = Skram.MODID)
public class SkramJournal {

  @SubscribeEvent
  public static void onGatherRegistries(GatherDataRegistryEntriesEvent event) {
    System.out.println("GATHER_REGISTRIES --> THIS IS GETTING CALLED.");
    event.add(Registries.ADVANCEMENT, bootstrap -> {
      new AdvancementProvider(
        List.of(MiningJournal::new)
      );
    });

    event.gatherFor("minecraft", Skram.MODID)
      .advancement(MiningJournal::new);
  }
}
