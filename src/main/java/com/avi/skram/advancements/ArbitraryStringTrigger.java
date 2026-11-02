package com.avi.skram.advancements;

import com.avi.skram.Skram;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class ArbitraryStringTrigger extends SimpleCriterionTrigger<ArbitraryStringTrigger.TriggerInstance> {

  @Override
  public @NonNull Codec<TriggerInstance> codec() {
    return TriggerInstance.CODEC;
  }

  public void trigger(ServerPlayer player, String triggerValue) {
    this.trigger(player, t -> t.matches(triggerValue));
  }

  public record TriggerInstance(Optional<Holder<LootItemCondition>> player,
                                String expectedValue) implements SimpleCriterionTrigger.SimpleInstance {
    public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
      Codec.STRING.fieldOf("expectedValue").forGetter(TriggerInstance::expectedValue)
    ).apply(instance, TriggerInstance::new));

    public static Criterion<ArbitraryStringTrigger.TriggerInstance> make(String value) {
      return Skram.ARBITRARY_STRING_TRIGGER.get().createCriterion(new ArbitraryStringTrigger.TriggerInstance(Optional.empty(), value));
    }

    public boolean matches(String providedValue) {
      return this.expectedValue.equals(providedValue);
    }

  }

}
