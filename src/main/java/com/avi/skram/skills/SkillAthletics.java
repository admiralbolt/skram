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
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class SkillAthletics extends Skill {

  public static final String NAME = "athletics";
  public static final StreamCodec<ByteBuf, SkillAthletics> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillAthletics::new);
  public static final Identifier MOVEMENT_SPEED_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "athletics_movement_speed_boost");
  public static final Identifier OXYGEN_BONUS = Identifier.fromNamespaceAndPath(Skram.MODID, "athletics_oxygen_bonus");

  public SkillAthletics() {
    super(NAME, 1, 0);
  }

  public SkillAthletics(ByteBuf buffer) {
    super(buffer);
  }

  // This bonus is effectively (1 + X) * faster.
  public double getMovementSpeedBoost() {
    return (double) this.level / 400;
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
      movementSpeed.addOrReplacePermanentModifier(new AttributeModifier(
        MOVEMENT_SPEED_BOOST, this.getMovementSpeedBoost(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
      ));
    }

    AttributeInstance oxygenBonus = attributes.getInstance(Attributes.OXYGEN_BONUS);
    if (oxygenBonus != null) {
      oxygenBonus.addOrReplacePermanentModifier(new AttributeModifier(
        OXYGEN_BONUS, this.getOxygenBonus(), AttributeModifier.Operation.ADD_VALUE
      ));
    }
  }

  @Override
  public void onLevelChange(Player player) {
    this.applyModifiers(player);
  }

  public static void handleTick(PlayerTickEvent.Pre event) {
    Player player = event.getEntity();
    if (!player.level().isClientSide()) return;

    Vec3 velocity = player.getDeltaMovement();
    // If we aren't moving at all, don't give xp.
    if (velocity.x == 0 && velocity.z == 0) return;

    SkillAthletics athletics = (SkillAthletics) player.getData(Skills.ATHLETICS);

    // Sprinting is worth the most.
    if (player.isSprinting()) {
      athletics.addXp(player, 40000, true);
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
}