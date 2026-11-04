package com.avi.skram.mixins;

import com.avi.skram.perks.PerkPreciseSmasher;
import com.avi.skram.perks.Perks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {Item.class})
public class ItemMixin {

  @Inject(
    method = "mineBlock(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)Z",
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V", shift = At.Shift.BEFORE)},
    cancellable = true
  )
  public void overrideDamageTool(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
    if (!(owner instanceof Player player)) return;

    PerkPreciseSmasher perk = player.getData(Perks.PRECISE_SMASHER);
    if (!perk.isFullyEnabled()) return;
    if (Math.random() > 0.1) return;

    cir.setReturnValue(true);
  }

}
