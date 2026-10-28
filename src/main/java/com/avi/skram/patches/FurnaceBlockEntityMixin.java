package com.avi.skram.patches;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
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

    PlayerOwnedBlock pob = blockEntity.getData(BlockData.PLAYER_OWNED);
    Player owner = pob.getOwner((ServerLevel) blockEntity.getLevel());

    if (owner == null) return;

    Component baseReturn = cir.getReturnValue();
    MutableComponent newComponent = Component.empty();
    newComponent.append(owner.getDisplayName());
    newComponent.append(Component.literal("'s "));
    newComponent.append(baseReturn);

    cir.setReturnValue(newComponent);
  }
}
