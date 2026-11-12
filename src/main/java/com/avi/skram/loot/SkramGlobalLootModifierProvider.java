package com.avi.skram.loot;

import com.avi.skram.Skram;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SkramGlobalLootModifierProvider extends GlobalLootModifierProvider {

  public SkramGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, Skram.MODID);
  }

  public static Map<String, Float> spellScrollChances = ImmutableMap.<String, Float>builder()
    .put("entities/guardian", 0.03f)
    .put("entities/elder_guardian", 0.05f)
    .put("entities/witch", 0.09f)
    .put("entities/evoker", 0.4f)
    .put("entities/illusioner", 0.8f)
    .put("chests/abandoned_camp_common_chest", 0.09f)
    .put("chests/abandoned_camp_secret_chest", 0.11f)
    .put("chests/abandoned_mineshaft", 0.07f)
    .put("chests/ancient_city", 0.13f)
    .put("chests/bastion_treasure", 0.07f)
    .put("chests/buried_treasure", 0.1f)
    .put("chests/desert_pyramid", 0.1f)
    .put("chests/end_city_treasure", 0.13f)
    .put("chests/igloo_chest", 0.04f)
    .put("chests/jungle_temple", 0.11f)
    .put("chests/nether_bridge", 0.04f)
    .put("chests/pillager_outpost", 0.12f)
    .put("chests/ruined_portal", 0.08f)
    .put("chests/shipwreck_treasure", 0.09f)
    .put("chests/simple_dungeon", 0.06f)
    .put("chests/stronghold_corridor", 0.03f)
    .put("chests/stronghold_crossing", 0.03f)
    .put("chests/stronghold_library", 0.28f)
    .put("chests/trial_chambers/reward", 0.22f)
    .put("chests/trial_chambers/reward_rare", 0.45f)
    .put("chests/trial_chambers/reward_unique", 0.75f)
    .put("chests/village/village_cartographer", 0.22f)
    .put("chests/woodland_mansion", 0.35f)
    .build();

  public static List<String> allChests = List.of(
    "chests/abandoned_camp_common_chest",
    "chests/abandoned_camp_secret_chest",
    "chests/abandoned_mineshaft",
    "chests/ancient_city",
    "chests/bastion_treasure",
    "chests/buried_treasure",
    "chests/desert_pyramid",
    "chests/end_city_treasure",
    "chests/igloo_chest",
    "chests/jungle_temple",
    "chests/nether_bridge",
    "chests/pillager_outpost",
    "chests/ruined_portal",
    "chests/shipwreck_treasure",
    "chests/simple_dungeon",
    "chests/stronghold_corridor",
    "chests/stronghold_crossing",
    "chests/stronghold_library",
    "chests/trial_chambers/reward",
    "chests/trial_chambers/reward_rare",
    "chests/trial_chambers/reward_unique",
    "chests/village/village_cartographer",
    "chests/woodland_mansion"
  );

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

    // Records!!!!
    for (String table : allChests) {
      this.add("add_random_record_to_" + table.replace("/", "_"), new RandomRecordLootModifier(
        Optional.of(Holder.direct(
          AllOfCondition.allOf(
            HolderSet.direct(
              Holder.direct(new LootTableIdCondition.Builder(Identifier.withDefaultNamespace(table)).build()),
              Holder.direct(LootItemRandomChanceCondition.randomChance(0.11f).build())
            )
          )
        )),
        IGlobalLootModifier.DEFAULT_PRIORITY
      ));
    }

    // The Batman.
    this.add("add_spawn_egg_to_bat", new AddItemLootModifier(
      Optional.of(Holder.direct(
        AllOfCondition.allOf(
          HolderSet.direct(
            Holder.direct(new LootTableIdCondition.Builder(Identifier.withDefaultNamespace("entities/bat")).build()),
            Holder.direct(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(
              this.registries.lookupOrThrow(Registries.ENCHANTMENT), 0.2f, 0.05f
            ).build()
          ))
        )
      )), IGlobalLootModifier.DEFAULT_PRIORITY, Items.BAT_SPAWN_EGG
    ));
  }


}
