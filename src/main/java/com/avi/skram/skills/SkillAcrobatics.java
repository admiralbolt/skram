package com.avi.skram.skills;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class SkillAcrobatics extends Skill {

  public static final String NAME = "acrobatics";
  public static StreamCodec<ByteBuf, SkillAcrobatics> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillAcrobatics::new);

  public static final Identifier JUMP_STRENGTH_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "acrobatics_jump_boost");
  public static final Identifier SAFE_FALL_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "acrobatics_safe_fall_boost");

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
    if (jumpStrength == null) return;

    jumpStrength.addOrReplacePermanentModifier(new AttributeModifier(
      JUMP_STRENGTH_BOOST, this.getJumpBoost(), AttributeModifier.Operation.ADD_VALUE
    ));

    AttributeInstance safeFall = attributes.getInstance(Attributes.SAFE_FALL_DISTANCE);
    if (safeFall == null) return;

    safeFall.addOrReplacePermanentModifier(new AttributeModifier(
      SAFE_FALL_BOOST, this.getSafeFallDistanceBoost(), AttributeModifier.Operation.ADD_VALUE
    ));
  }

  @Override
  public void onLevelChange(Player player) {
    this.applyModifiers(player);
  }

  public static void handleJump(LivingEvent.LivingJumpEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;

    SkillAcrobatics acrobatics = (SkillAcrobatics) player.getData(Skills.ACROBATICS);
    acrobatics.addXp(player, 1000);
  }
  
}
