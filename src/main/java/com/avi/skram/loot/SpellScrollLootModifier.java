package com.avi.skram.loot;

import com.avi.skram.item.SkramItems;
import com.avi.skram.item.SpellNameDataComponent;
import com.avi.skram.magic.spells.Spells;
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

public class SpellScrollLootModifier extends LootModifier {

  public static final MapCodec<SpellScrollLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
    LootModifier.codecStart(inst).apply(inst, SpellScrollLootModifier::new)
  );

  public SpellScrollLootModifier(Optional<Holder<LootItemCondition>> condition, int priority) {
    super(condition, priority);
  }

  @Override
  protected @NonNull ObjectArrayList<ItemStack> doApply(@NonNull ObjectArrayList<ItemStack> generatedLoot, @NonNull LootContext context) {
    ItemStack stack = new ItemStack(SkramItems.SPELL_SCROLL.get(), 1);
    stack.set(SkramItems.SPELL_NAME_DATA_COMPONENT.get(), new SpellNameDataComponent(Spells.getRandomSpellName()));
    generatedLoot.add(stack);

    return generatedLoot;
  }

  @Override
  public @NonNull MapCodec<? extends IGlobalLootModifier> codec() {
    return CODEC;
  }
}
