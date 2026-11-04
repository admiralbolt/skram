package com.avi.skram.mixins;

import com.avi.skram.perks.PerkVolcanicSailor;
import com.avi.skram.perks.Perks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = {Entity.class})
public class EntityMixin {

  @Inject(
    method = "fireImmune()Z",
    at = {@At(value = "RETURN")},
    cancellable = true)
  private void overrideFireImmune(CallbackInfoReturnable<Boolean> cir) {
    // If it's already fire immune do nothing.
    if (cir.getReturnValueZ()) return;

    // This is complicated, but we need to check if the entity is a boat OR if the entity is riding in a boat ON lava.
    Entity entity = (Entity) (Object) this;
    if (entity instanceof AbstractBoat boat) {
      List<Entity> passengers = boat.getPassengers();
      Player player = null;
      for (Entity e : passengers) {
        if (e instanceof Player) {
          player = (Player) e;
          break;
        }
      }

      // This is evil, but if we don't have a player in the boat make it fire immune.
      // As soon as a player enters though...
      if (player == null) {
        cir.setReturnValue(true);
        return;
      }

      // If we have a player in the boat, we check for the relevant perk here.
      PerkVolcanicSailor perk = player.getData(Perks.VOLCANIC_SAILOR);
      if (!perk.isFullyEnabled()) return;

      cir.setReturnValue(true);
    } else if (entity instanceof Player player) {
      // We have to be riding in a boat, and that boat has to be on lava.
      if (!(player.getVehicle() instanceof AbstractBoat boat)) return;

      BlockPos base = BlockPos.containing(boat.getX(), boat.getY(), boat.getZ());
      if (!boat.level().getFluidState(base).is(FluidTags.LAVA) && !boat.level().getFluidState(base.below()).is(FluidTags.LAVA)) return;

      // If we have a player in the boat, we check for the relevant perk here.
      PerkVolcanicSailor perk = player.getData(Perks.VOLCANIC_SAILOR);
      if (!perk.isFullyEnabled()) return;

      cir.setReturnValue(true);
    }
  }
}
