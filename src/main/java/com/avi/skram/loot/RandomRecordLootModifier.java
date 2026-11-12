package com.avi.skram.loot;

import com.avi.skram.item.SkramItems;
import com.avi.skram.utils.MathUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class RandomRecordLootModifier extends LootModifier {

  public static final MapCodec<RandomRecordLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
    LootModifier.codecStart(inst).apply(inst, RandomRecordLootModifier::new)
  );

  public RandomRecordLootModifier(Optional<Holder<LootItemCondition>> condition, int priority) {
    super(condition, priority);
  }

  @Override
  protected @NonNull ObjectArrayList<ItemStack> doApply(@NonNull ObjectArrayList<ItemStack> generatedLoot, @NonNull LootContext context) {
    ItemStack stack = new ItemStack(SkramItems.ALL_RECORDS.get(MathUtils.RANDOM.nextInt(SkramItems.ALL_RECORDS.size())).get());
    generatedLoot.add(stack);

    return generatedLoot;
  }

  @Override
  public @NonNull MapCodec<? extends IGlobalLootModifier> codec() {
    return CODEC;
  }
}
