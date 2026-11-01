package com.avi.skram.advancements;

import com.avi.skram.Skram;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.EnterBlockTrigger;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

public class BreakBlockTrigger extends SimpleCriterionTrigger<BreakBlockTrigger.TriggerInstance> {

  // 1. Define the codec so Minecraft knows how to parse your conditions from JSON datagen
  @Override
  public Codec<TriggerInstance> codec() {
    return TriggerInstance.CODEC;
  }

  public void trigger(ServerPlayer player, BlockState state) {
    this.trigger(player, t -> t.matches(state));
  }

  public record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<HolderSet<Block>> block, Optional<StatePropertiesPredicate> state) implements SimpleCriterionTrigger.SimpleInstance {
    public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
      RegistryCodecs.holderSet(Registries.BLOCK).optionalFieldOf("blocks").forGetter(TriggerInstance::block),
      StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(TriggerInstance::state)
    ).apply(instance, TriggerInstance::new));

    public static Criterion<BreakBlockTrigger.TriggerInstance> breaksBlock(HolderGetter<Block> blocks, Block block) {
      return breaksBlock(HolderSet.direct(block.builtInRegistryHolder()));
    }

    public static Criterion<BreakBlockTrigger.TriggerInstance> breaksBlock(HolderSet<Block> block) {
      return Skram.BREAK_BLOCK_TRIGGER.get().createCriterion(new BreakBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(block), Optional.empty()));
    }

    public boolean matches(BlockState state) {
      return this.block.isPresent() && !state.is(this.block.get()) ? false : !this.state.isPresent() || this.state.get().matches(state);
    }


  }

}
