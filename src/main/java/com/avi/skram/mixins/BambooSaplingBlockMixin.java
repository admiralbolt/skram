package com.avi.skram.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {BambooSaplingBlock.class})
public class BambooSaplingBlockMixin {

  @Inject(
    method = {"growBamboo(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"},
    at = {@At(value = "RETURN")}
  )
  private void overrideGrowBamboo(Level level, BlockPos pos, CallbackInfo ci) {
    // Invoke the neoforge common hook for plant growth.
    CommonHooks.fireCropGrowPost(level, pos, level.getBlockState(pos));
  }
}
