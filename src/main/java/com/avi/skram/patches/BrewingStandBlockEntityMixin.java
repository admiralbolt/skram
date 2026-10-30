package com.avi.skram.patches;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlocks;
import com.avi.skram.blocks.PlayerOwnedData;
import com.avi.skram.skills.SkillBrewing;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.ReflectionUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {BrewingStandBlockEntity.class})
public class BrewingStandBlockEntityMixin {

  @Inject(
    method = {"serverTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;setChanged(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V")}
  )
  private static void serverTick(ServerLevel level, BlockPos pos, BlockState selfState, BrewingStandBlockEntity entity, CallbackInfo ci) {
    ChunkAccess chunk = level.getChunk(pos);
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);
    PlayerOwnedData playerData = pob.playerOwnedBlocks.get(pos);
    if (playerData == null) return;

    Player owner = playerData.getOwner(level);
    float brewingSpeed = 0;
    if (owner != null) {
      SkillBrewing brewing = owner.getData(Skills.BREWING);
      if (brewing.enabledAbilities[SkillBrewing.BREWING_SPEED_INDEX]) {
        brewingSpeed = brewing.getBrewingSpeed();
      }
    } else {
      // If the owner is offline, used the saved value of brewing speed.
      brewingSpeed = 0.1f;
    }

    // I think there's a more "proper" way of accessing private field vars, but we just use reflection.
    try {
      int totalBrewTime = (int) ReflectionUtils.getFieldValue(entity, "totalBrewTime");
      int newTicks = (int) Math.max(1, totalBrewTime * (1.0f / (1 + brewingSpeed)));
      // Set both the totalBrewTime, and the brewTime fields.
      ReflectionUtils.setFieldValue(entity, "totalBrewTime", newTicks);
      ReflectionUtils.setFieldValue(entity, "brewTime", newTicks);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Inject(
    method = {"doBrew(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;)V"},
    at = {@At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;onPotionBrewed(Lnet/minecraft/core/NonNullList;)V")}
  )
  private static void overrideDoBrew(ServerLevel level, BlockPos pos, BrewingStandBlockEntity entity, CallbackInfo ci, @Local(name = "items") NonNullList<ItemStack> items) {
    ChunkAccess chunk = level.getChunk(pos);
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);
    PlayerOwnedData playerData = pob.playerOwnedBlocks.get(pos);
    if (playerData == null) return;

    Player owner = playerData.getOwner(level);
    if (owner == null) return;

    SkillBrewing brewing = owner.getData(Skills.BREWING);
    brewing.addXpForItems(owner, items);
  }

  @Inject(
    method = {"getDefaultName()Lnet/minecraft/network/chat/Component;"},
    at = {@At(value = "RETURN")},
    cancellable = true
  )
  private void overrideGetDefaultName(CallbackInfoReturnable<Component> cir) {
    BrewingStandBlockEntity blockEntity = (BrewingStandBlockEntity) (Object) this;

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
