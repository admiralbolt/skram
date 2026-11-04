package com.avi.skram.patches;

import com.avi.skram.perks.PerkFlyingDutchman;
import com.avi.skram.perks.Perks;
import com.avi.skram.utils.ReflectionUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;

@Mixin(value = {LocalPlayer.class})
public class LocalPlayerMixin {

  @Inject(
    method = {"aiStep()V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;", ordinal = 1)}
  )
  private void customOnJump(CallbackInfo ci) {
    // This is crazy if it works.
    LocalPlayer player = (LocalPlayer) (Object) this;
    Entity entity = player.getControlledVehicle();

    PerkFlyingDutchman perk = player.getData(Perks.FLYING_DUTCHMAN);
    if (!perk.isFullyEnabled()) return;
    if (!(entity instanceof AbstractBoat boat)) return;
    if (!player.input.keyPresses.jump()) return;

    AbstractBoat.Status status = null;
    // Reflection strikes again.
    try {
      status = (AbstractBoat.Status) ReflectionUtils.invokeMethod(boat, "getStatus");
    } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
      System.out.println("no status for you.");
    }

    if (status == null || status == AbstractBoat.Status.IN_AIR) return;

    Vec3 baseDelta = boat.getDeltaMovement();
    boat.setDeltaMovement(baseDelta.x, player.getAttributeValue(Attributes.JUMP_STRENGTH) * 0.8, baseDelta.z);
    // TODO: Delete this.
    // I'm actually not sure if this is necessary at all? Going to keep it around just in case come network testing.
    // ClientPacketDistributor.sendToServer(new BoatJumpPacket(player.getId()));
  }
}