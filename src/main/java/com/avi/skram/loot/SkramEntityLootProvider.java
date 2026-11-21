package com.avi.skram.loot;

import com.avi.skram.entity.SkramEntities;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

public class SkramEntityLootProvider extends EntityLootSubProvider {

  protected SkramEntityLootProvider(Context output) {
    super(FeatureFlags.REGISTRY.allFlags(), output);
  }

  @Override
  protected @NonNull Stream<EntityType<?>> getKnownEntityTypes() {
    return Stream.of(
      SkramEntities.ENDER_CREEPER.get(),
      SkramEntities.NAPALM_CREEPER.get()
    );
  }

  @Override
  public void generate() {
    this.add(SkramEntities.NAPALM_CREEPER.get(), LootTable.lootTable()
      .withPool(
        LootPool.lootPool()
          .setRolls(ContextIntProviders.exactly(1))
          .add(
            LootItem.lootTableItem(Items.BLAZE_POWDER)
              .apply(SetItemCountFunction.setCount(ContextIntProviders.between(0, 1)))
              .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.enchantments, ContextFloatProviders.between(0.0F, 1.0F)))
          )
          .when(LootItemKilledByPlayerCondition.killedByPlayer())
      )
      .withPool(
        LootPool.lootPool()
          .setRolls(ContextIntProviders.exactly(1))
          .add(
            LootItem.lootTableItem(Items.GUNPOWDER)
              .apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 2)))
              .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.enchantments, ContextFloatProviders.between(0.0F, 1.0F)))
          )
          .when(LootItemKilledByPlayerCondition.killedByPlayer())
      )
    );

    this.add(SkramEntities.ENDER_CREEPER.get(), LootTable.lootTable()
      .withPool(
        LootPool.lootPool()
          .setRolls(ContextIntProviders.exactly(1))
          .add(
            LootItem.lootTableItem(Items.ENDER_EYE)
              .apply(SetItemCountFunction.setCount(ContextIntProviders.between(0, 1)))
              .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.enchantments, ContextFloatProviders.between(0.0F, 1.0F)))
          )
          .when(LootItemKilledByPlayerCondition.killedByPlayer())
      )
      .withPool(
        LootPool.lootPool()
          .setRolls(ContextIntProviders.exactly(1))
          .add(
            LootItem.lootTableItem(Items.GUNPOWDER)
              .apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 3)))
              .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.enchantments, ContextFloatProviders.between(0.0F, 1.0F)))
          )
          .when(LootItemKilledByPlayerCondition.killedByPlayer())
      )
    );
  }

}
