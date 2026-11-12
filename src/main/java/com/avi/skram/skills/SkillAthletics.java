package com.avi.skram.skills;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class SkillAthletics extends Skill {

  public static final String NAME = "athletics";
  public static final StreamCodec<ByteBuf, SkillAthletics> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillAthletics::new);
  public static final String DESCRIPTION = """
    Gain XP by moving. More is gained if you are sprinting or swimming.
    
    1. Speed bonuses are magic numbers again, with even more complex calculations this time around. All that matters: bigger means faster.
    2. Oxygen bonus is *mostly* accurate. The actual oxygen decay is done via RNG calculation, so there is potential for time variance if you are very lucky or unlucky.
    """;

  public static final Identifier MOVEMENT_SPEED_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "athletics_movement_speed_boost");
  public static final Identifier SWIM_SPEED_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "athletics_swim_speed_boost");
  public static final Identifier OXYGEN_BONUS = Identifier.fromNamespaceAndPath(Skram.MODID, "athletics_oxygen_bonus");

  public static final int MOVEMENT_SPEED_INDEX = 0;
  public static final int SWIM_SPEED_INDEX = 1;
  public static final int OXYGEN_BONUS_INDEX = 2;

  public SkillAthletics() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true, true};
  }

  public SkillAthletics(ByteBuf buffer) {
    super(buffer);
  }

  // This bonus is effectively (1 + X) * faster.
  public double getMovementSpeedBoost() {
    return (double) this.level / 300;
  }

  // The relevant bit from the decreaseAirSupply method in LivingEntity:
  //   return oxygenBonus > 0.0 && this.random.nextDouble() >= 1.0 / (oxygenBonus + 1.0) ? currentSupply : currentSupply - 1;
  //
  // Each +1 here corresponds to another MULTIPLE of breath holding. +1 means 2x breath, +2 means 3x breath (at least
  // on average).
  public double getOxygenBonus() {
    return (double) this.level / 50;
  }

  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();

    AttributeInstance movementSpeed = attributes.getInstance(Attributes.MOVEMENT_SPEED);
    if (movementSpeed != null) {
      if (this.enabledAbilities[MOVEMENT_SPEED_INDEX]) {
        movementSpeed.addOrReplacePermanentModifier(new AttributeModifier(
          MOVEMENT_SPEED_BOOST, this.getMovementSpeedBoost(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ));
      } else {
        movementSpeed.removeModifier(MOVEMENT_SPEED_BOOST);
      }
    }

    AttributeInstance swimSpeed = attributes.getInstance(NeoForgeMod.SWIM_SPEED);
    if (swimSpeed != null) {
      if (this.enabledAbilities[SWIM_SPEED_INDEX]) {
        swimSpeed.addOrReplacePermanentModifier(new AttributeModifier(
          SWIM_SPEED_BOOST, this.getMovementSpeedBoost(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ));
      } else {
        swimSpeed.removeModifier(SWIM_SPEED_BOOST);
      }
    }

    AttributeInstance oxygenBonus = attributes.getInstance(Attributes.OXYGEN_BONUS);
    if (oxygenBonus != null) {
      if (this.enabledAbilities[OXYGEN_BONUS_INDEX]) {
        oxygenBonus.addOrReplacePermanentModifier(new AttributeModifier(
          OXYGEN_BONUS, this.getOxygenBonus(), AttributeModifier.Operation.ADD_VALUE
        ));
      } else {
        oxygenBonus.removeModifier(OXYGEN_BONUS);
      }
    }
  }

  public static void handleTick(PlayerTickEvent.Pre event) {
    Player player = event.getEntity();
    if (!player.level().isClientSide()) return;

    Vec3 velocity = player.getDeltaMovement();
    // If we aren't moving at all, don't give xp.
    if (velocity.x == 0 && velocity.z == 0) return;

    SkillAthletics athletics = player.getData(Skills.ATHLETICS);

    // Sprinting is worth the most.
    if (player.isSprinting()) {
      athletics.addXp(player, 4, true);
      return;
    }

    // player.isSwimming() checks for sprinting in the water.
    if (player.isSwimming()) {
      athletics.addXp(player, 3, true);
      return;
    }

    if (player.isInWater()) {
      athletics.addXp(player, 1, true);
      return;
    }

    // We must be grounded, and not on rails.
    if (!player.onGround() || player.isOnRails()) return;

    athletics.addXp(player, 1, true);
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(
      new AbilityDescription("Movement Speed: +" + String.format("%.2f", this.getMovementSpeedBoost()) + ".", MOVEMENT_SPEED_INDEX),
      new AbilityDescription("Swim Speed: +" + String.format("%.2f", this.getMovementSpeedBoost()) + ".", SWIM_SPEED_INDEX),
      new AbilityDescription("Oxygen Increase: +" + (100 * this.getOxygenBonus()) + "%.", OXYGEN_BONUS_INDEX)
    );
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}