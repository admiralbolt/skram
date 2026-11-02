package com.avi.skram.advancements;

import com.avi.skram.Skram;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class ItemSmeltedTrigger extends SimpleCriterionTrigger<ItemSmeltedTrigger.TriggerInstance> {

  @Override
  public @NonNull Codec<TriggerInstance> codec() {
    return TriggerInstance.CODEC;
  }

  public void trigger(ServerPlayer player, ItemStack stack) {
    this.trigger(player, t -> t.matches(stack));
  }

  public record TriggerInstance(Optional<Holder<LootItemCondition>> player,
                                Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
    public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
      ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)
    ).apply(instance, TriggerInstance::new));


    public static Criterion<ItemSmeltedTrigger.TriggerInstance> isItem(HolderGetter<Item> items, ItemLike item) {
      ItemPredicate predicate = ItemPredicate.Builder.item().of(items, item.asItem()).build();
      return Skram.ITEM_SMELTED_TRIGGER.get().createCriterion(new ItemSmeltedTrigger.TriggerInstance(Optional.empty(), Optional.of(predicate)));
    }

    public boolean matches(ItemStack itemStack) {
      return this.item.isEmpty() || this.item.get().test(itemStack);
    }

  }

}
