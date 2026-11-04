package com.avi.skram.patches;

import com.avi.skram.perks.PerkFlyingDutchman;
import com.avi.skram.perks.Perks;
import com.avi.skram.skills.SkillSailing;
import com.avi.skram.skills.Skills;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = {AbstractBoat.class})
public class AbstractBoatMixin {

  @Shadow
  private boolean inputUp;

  @Shadow
  private AbstractBoat.Status status;

  @Inject(
    method = {"controlBoat()V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;setYRot(F)V")}
  )
  private void overrideControlBoat(CallbackInfo ci, @Local(name = "acceleration") LocalFloatRef acceleration) {
    AbstractBoat boat = (AbstractBoat) (Object) this;
    List<Entity> passengers = boat.getPassengers();
    Player player = null;
    for (Entity entity : passengers) {
      if (entity instanceof Player) {
        player = (Player) entity;
        break;
      }
    }

    if (player == null) return;

    SkillSailing sailing = player.getData(Skills.SAILING);
    if (!sailing.enabledAbilities[SkillSailing.SAILING_ACCELERATION_INDEX]) return;

    if (this.inputUp) {
      acceleration.set(acceleration.get() + SkillSailing.BASE_BOAT_ACCELERATION * sailing.getAccelerationBoost());
    }
  }

  @Inject(
    method = {"controlBoat()V"},
    at = {@At(value = "RETURN")}
  )
  private void gainXPOnBoatMove(CallbackInfo ci) {
    AbstractBoat boat = (AbstractBoat) (Object) this;
    List<Entity> passengers = boat.getPassengers();
    Player player = null;
    for (Entity entity : passengers) {
      if (entity instanceof Player) {
        player = (Player) entity;
        break;
      }
    }

    if (player == null) return;

    SkillSailing sailing = player.getData(Skills.SAILING);
    double speed = boat.getDeltaMovement().length();
    // Make sure we sync to the server, as this is called on the client side.
    sailing.addXp(player, 6 * speed, true);
  }

  @Inject(
    method = {"floatBoat()V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;setDeltaMovement(DDD)V", shift = At.Shift.BEFORE)}
  )
  private void captainOfLegendAirDrift(CallbackInfo ci, @Local(name = "invFriction") LocalFloatRef invFriction) {
    if (this.status != AbstractBoat.Status.IN_AIR) return;

    AbstractBoat boat = (AbstractBoat) (Object) this;
    List<Entity> passengers = boat.getPassengers();
    Player player = null;
    for (Entity entity : passengers) {
      if (entity instanceof Player) {
        player = (Player) entity;
        break;
      }
    }

    if (player == null) return;

    PerkFlyingDutchman perk = player.getData(Perks.FLYING_DUTCHMAN);
    if (!perk.isFullyEnabled()) return;


  }


}
