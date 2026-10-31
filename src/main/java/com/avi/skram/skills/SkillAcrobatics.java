package com.avi.skram.skills;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class SkillAcrobatics extends Skill {

  public static final String NAME = "acrobatics";
  public static StreamCodec<ByteBuf, SkillAcrobatics> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillAcrobatics::new);

  public static final Identifier JUMP_STRENGTH_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "acrobatics_jump_boost");
  public static final Identifier SAFE_FALL_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "acrobatics_safe_fall_boost");

  public static final int JUMP_STRENGTH_INDEX = 0;
  public static final int SAFE_FALL_INDEX = 1;

  public SkillAcrobatics() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true};
  }

  public SkillAcrobatics(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.ACROBATICS, player.getData(Skills.ACROBATICS));
  }

  public double getJumpBoost() {
    return (double) this.level / 100;
  }

  public double getSafeFallDistanceBoost() {
    return 0.5 * (double) this.level / 5;
  }

  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();

    AttributeInstance jumpStrength = attributes.getInstance(Attributes.JUMP_STRENGTH);
    if (jumpStrength != null) {
      if (this.enabledAbilities[JUMP_STRENGTH_INDEX]) {
        jumpStrength.addOrReplacePermanentModifier(new AttributeModifier(
          JUMP_STRENGTH_BOOST, this.getJumpBoost(), AttributeModifier.Operation.ADD_VALUE
        ));
      } else {
        jumpStrength.removeModifier(JUMP_STRENGTH_BOOST);
      }
    }

    AttributeInstance safeFall = attributes.getInstance(Attributes.SAFE_FALL_DISTANCE);
    if (safeFall != null) {
      if (this.enabledAbilities[SAFE_FALL_INDEX]) {
        safeFall.addOrReplacePermanentModifier(new AttributeModifier(
          SAFE_FALL_BOOST, this.getSafeFallDistanceBoost(), AttributeModifier.Operation.ADD_VALUE
        ));
      } else {
        safeFall.removeModifier(SAFE_FALL_BOOST);
      }
    }
  }

  @Override
  public void onLevelChange(Player player) {
    super.onLevelChange(player);
    this.applyModifiers(player);
  }

  public static void handleJump(LivingEvent.LivingJumpEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    SkillAcrobatics acrobatics = player.getData(Skills.ACROBATICS);
    acrobatics.addXp(player, 50);
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Jump Strength: +" + this.getJumpBoost() + "."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[JUMP_STRENGTH_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(JUMP_STRENGTH_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Safe Fall Distance: +" + this.getSafeFallDistanceBoost() + " blocks."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[SAFE_FALL_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(SAFE_FALL_INDEX))
        .build()
    );

    return vertical;
  }

}
