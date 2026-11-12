package com.avi.skram.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class AddItemLootModifier extends LootModifier {

  public final Item item;

  public static final MapCodec<AddItemLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
    LootModifier.codecStart(inst).and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item)).apply(inst, AddItemLootModifier::new)
  );

  public AddItemLootModifier(Optional<Holder<LootItemCondition>> condition, int priority, Item item) {
    super(condition, priority);
    this.item = item;
  }

  @Override
  protected @NonNull ObjectArrayList<ItemStack> doApply(@NonNull ObjectArrayList<ItemStack> generatedLoot, @NonNull LootContext context) {
    ItemStack stack = new ItemStack(item);
    generatedLoot.add(stack);

    return generatedLoot;
  }

  @Override
  public @NonNull MapCodec<? extends IGlobalLootModifier> codec() {
    return CODEC;
  }
}
