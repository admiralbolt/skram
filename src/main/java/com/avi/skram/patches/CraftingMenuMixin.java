package com.avi.skram.patches;

import com.avi.skram.skills.SkillCooking;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {CraftingMenu.class})
public class CraftingMenuMixin {


  @Inject(
    method = "quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;",
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;")}
  )
  private static void handleCraftingShiftClick(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir, @Local(name = {"stack"}) ItemStack stack) {
    SkillCooking.overrideFood(player, stack);
  }

}
