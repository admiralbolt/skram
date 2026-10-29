package com.avi.skram.patches;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlocks;
import com.avi.skram.blocks.PlayerOwnedData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {FurnaceBlockEntity.class})
public class FurnaceBlockEntityMixin {

  @Inject(
    method = {"getDefaultName()Lnet/minecraft/network/chat/Component;"},
    at = {@At(value = "RETURN")},
    cancellable = true
  )
  private void overrideGetDefaultName(CallbackInfoReturnable<Component> cir) {
    FurnaceBlockEntity blockEntity = (FurnaceBlockEntity) (Object) this;

    Level level = blockEntity.getLevel();
    if (level == null) return;

    ChunkAccess chunk = level.getChunk(blockEntity.getBlockPos());
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);
    PlayerOwnedData playerData = pob.playerOwnedBlocks.get(blockEntity.getBlockPos());
    if (playerData == null) return;

    String displayName = playerData.displayName;
    if (displayName.isEmpty()) return;

    Component baseReturn = cir.getReturnValue();
    MutableComponent newComponent = Component.empty();
    newComponent.append(displayName);
    newComponent.append(Component.literal("'s "));
    newComponent.append(baseReturn);

    cir.setReturnValue(newComponent);
  }
}
