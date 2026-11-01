package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public class SkillSailing extends Skill {

  public static final String NAME = "sailing";
  public static final StreamCodec<ByteBuf, SkillSailing> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillSailing::new);

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
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Sailing Acceleration: +" + (int) (this.getAccelerationBoost() * 100) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[SAILING_ACCELERATION_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(SAILING_ACCELERATION_INDEX))
        .build()
    );

    return vertical;
  }
}