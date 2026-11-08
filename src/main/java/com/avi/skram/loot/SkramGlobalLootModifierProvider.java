package com.avi.skram.loot;

import com.avi.skram.Skram;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SkramGlobalLootModifierProvider extends GlobalLootModifierProvider {

  public SkramGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, Skram.MODID);
  }

  @Override
  protected void start() {
    this.add("skram_loot_modifier_instance", new SpellScrollLootModifier(
      Optional.of(Holder.direct(new LootTableIdCondition.Builder(Identifier.withDefaultNamespace("entities/witch")).build())),
      IGlobalLootModifier.DEFAULT_PRIORITY
    ));
  }


}
