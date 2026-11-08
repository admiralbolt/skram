package com.avi.skram.loot;

import com.avi.skram.Skram;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SkramGlobalLootModifierProvider extends GlobalLootModifierProvider {

  public SkramGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, Skram.MODID);
  }

  public static Map<String, Float> spellScrollChances = ImmutableMap.<String, Float>builder()
    .put("entities/guardian", 0.01f)
    .put("entities/elder_guardian", 0.01f)
    .put("entities/witch", 0.02f)
    .put("entities/evoker", 0.07f)
    .put("entities/illusioner", 0.15f)
    .put("chests/abandoned_camp_common_chest", 0.02f)
    .put("chests/abandoned_camp_secret_chest", 0.05f)
    .put("chests/abandoned_mineshaft", 0.02f)
    .put("chests/ancient_city", 0.05f)
    .put("chests/bastion_treasure", 0.03f)
    .put("chests/buried_treasure", 0.02f)
    .put("chests/desert_pyramid", 0.02f)
    .put("chests/end_city_treasure", 0.05f)
    .put("chests/igloo_chest", 0.01f)
    .put("chests/jungle_temple", 0.02f)
    .put("chests/nether_bridge", 0.01f)
    .put("chests/pillager_outpost", 0.05f)
    .put("chests/ruined_portal", 0.02f)
    .put("chests/shipwreck_treasure", 0.04f)
    .put("chests/simple_dungeon", 0.02f)
    .put("chests/stronghold_corridor", 0.01f)
    .put("chests/stronghold_crossing", 0.01f)
    .put("chests/stronghold_library", 0.11f)
    .put("chests/trial_chambers/reward", 0.1f)
    .put("chests/trial_chambers/reward_rare", 0.2f)
    .put("chests/trial_chambers/reward_unique", 0.35f)
    .put("chests/village/village_cartographer", 0.07f)
    .put("chests/woodland_mansion", 0.1f)
    .build();

  @Override
  protected void start() {
    for (Map.Entry<String, Float> entry : spellScrollChances.entrySet()) {
      this.add("add_spell_scroll_to_" + entry.getKey().replace("/", "_"), new SpellScrollLootModifier(
        Optional.of(Holder.direct(
          AllOfCondition.allOf(
            HolderSet.direct(
              Holder.direct(new LootTableIdCondition.Builder(Identifier.withDefaultNamespace(entry.getKey())).build()),
              Holder.direct(
                LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(
                  this.registries.lookupOrThrow(Registries.ENCHANTMENT), entry.getValue(), entry.getValue() / 10
                ).build()
              )
            )
          )
        )),
        IGlobalLootModifier.DEFAULT_PRIORITY
      ));
    }
  }


}
