package com.avi.skram.mixins;

import com.avi.skram.perks.PerkBlastMining;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {IBlockStateExtension.class})
public interface IBlockStateExtensionMixin {

  @Inject(
    method = "onBlockExploded(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V",
    at = {@At(value = "HEAD")}
  )
  private void handleBlockExploded(ServerLevel level, BlockPos pos, Explosion explosion, CallbackInfo ci) {
    PerkBlastMining.handleBlockExploded(level, pos, explosion);
  }
}
