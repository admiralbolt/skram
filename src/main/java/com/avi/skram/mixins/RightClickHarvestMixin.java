package com.avi.skram.mixins;

import com.avi.skram.skills.SkillFarming;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {RightClickHarvest.class})
public class RightClickHarvestMixin {

  @Inject(
    method = {"dropStacks(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Z)V"},
    at = {@At(value = "HEAD")},
    cancellable = true
  )
  private static void applyFarmingToRightClickHarvest(BlockState state, ServerLevel world, BlockPos pos, Entity entity, ItemStack toolStack, boolean removeReplant, CallbackInfo ci) {
    Item replant = state.getCloneItemStack(world, pos, true).getItem();
    boolean[] removedReplant = new boolean[]{!removeReplant};
    final int countMultiplier;
    if (entity instanceof Player player) {
      SkillFarming farming = player.getData(Skills.FARMING);
      countMultiplier = MathUtils.getScalingRandom(farming.getFortuneChance());
    } else {
      countMultiplier = 1;
    }

    Block.getDrops(state, world, pos, null, entity, toolStack).forEach((stack) -> {
      stack.setCount(stack.getCount() * countMultiplier);
      if (!removedReplant[0] && stack.getItem() == replant) {
        stack.setCount(stack.getCount() - 1);
        removedReplant[0] = true;
      }

      Block.popResource(world, pos, stack);
    });
    state.spawnAfterBreak(world, pos, toolStack, true);
    ci.cancel();
  }
}
