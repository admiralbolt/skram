package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SkillSailing extends Skill {

  public static final String NAME = "sailing";
  public static final StreamCodec<ByteBuf, SkillSailing> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillSailing::new);
  public static final String DESCRIPTION = """
    Boat faster.
    
    Acceleration is a magic value again because once again it's used in physics calculation I can't be bothered to understand.
    More number means more faster.
    """;

  public static final int SAILING_ACCELERATION_INDEX = 0;

  // See AbstractBoat.controlBoat() for where this value comes from.
  public static final float BASE_BOAT_ACCELERATION = 0.04f;

  public SkillSailing() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true};
  }

  public SkillSailing(ByteBuf buffer) {
    super(buffer);
  }

  public float getAccelerationBoost() {
    return  0.02f * this.level;
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(new AbilityDescription(
      "Sailing Acceleration: +" + (int) (this.getAccelerationBoost() * 100) + "%.",
      SAILING_ACCELERATION_INDEX
    ));
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}