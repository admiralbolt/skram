package com.avi.skram.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SpellNameDataComponent(String spellName) {

  public static final Codec<SpellNameDataComponent> CODEC = RecordCodecBuilder.create(instance ->
    instance.group(
      Codec.STRING.fieldOf("spellName").forGetter(SpellNameDataComponent::spellName)
    ).apply(instance, SpellNameDataComponent::new)
  );

}
