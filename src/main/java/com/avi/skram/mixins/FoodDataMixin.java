package com.avi.skram.mixins;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = {FoodData.class})
public class FoodDataMixin {

  @ModifyArg(
    method = {"tick(Lnet/minecraft/server/level/ServerPlayer;)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V", ordinal = 0)},
    index = 0
  )
  private float overrideHealFromFoodFirst(float heal) {
    return heal * 0.4f;
  }

  @ModifyArg(
    method = {"tick(Lnet/minecraft/server/level/ServerPlayer;)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V", ordinal = 1)},
    index = 0
  )
  private float overrideHealFromFoodSecond(float heal) {
    return heal * 0.25f;
  }

}
