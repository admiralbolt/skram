package com.avi.skram.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SkillNameDataComponent(String skillName) {

  public static final Codec<SkillNameDataComponent> CODEC = RecordCodecBuilder.create(instance ->
    instance.group(
      Codec.STRING.fieldOf("skillName").forGetter(SkillNameDataComponent::skillName)
    ).apply(instance, SkillNameDataComponent::new)
  );

}
