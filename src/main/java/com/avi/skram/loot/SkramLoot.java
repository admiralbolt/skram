package com.avi.skram.loot;

import com.avi.skram.Skram;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Skram.MODID)
public class SkramLoot {

  public static final Supplier<MapCodec<SpellScrollLootModifier>> MODIFIER =
    Skram.GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("random_spell_scroll", () -> SpellScrollLootModifier.CODEC);

  public static final Supplier<MapCodec<AddItemLootModifier>> ADD_ITEM_MODIFIER =
    Skram.GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("add_item_modifier", () -> AddItemLootModifier.CODEC);

  public static final Supplier<MapCodec<RandomRecordLootModifier>> RANDOM_RECORD_MODIFIER =
    Skram.GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("random_random_record", () -> RandomRecordLootModifier.CODEC);


  @SubscribeEvent
  public static void gatherData(GatherDataEvent.Client event) {
    event.createProvider(SkramGlobalLootModifierProvider::new);
  }


}
