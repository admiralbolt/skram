package com.avi.skram.patches;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlocks;
import com.avi.skram.blocks.PlayerOwnedData;
import com.avi.skram.skills.SkillCooking;
import com.avi.skram.skills.SkillSmithing;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.ReflectionUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;

@Mixin(value = {AbstractFurnaceBlockEntity.class})
public class AbstractFurnaceBlockEntityMixin {

  // We apply our smithing speed up by adjusting the total number of ticks it takes to cook a particular item.
  @Inject(method = {"getTotalCookTime(Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)I"}, at = {@At(value = "RETURN")}, cancellable = true)
  private static void overrideCookTime(RecipeHolder<? extends AbstractCookingRecipe> recipe, AbstractFurnaceBlockEntity blockEntity, CallbackInfoReturnable<Integer> returnValue) {
    if (blockEntity.getLevel() != null && blockEntity.getLevel().isClientSide()) return;

    // Getting the result of a recipe is guarded by a protected method ._.
    AbstractCookingRecipe acr = recipe.value();
    Item output = null;

    try {
      ItemStackTemplate template = (ItemStackTemplate) ReflectionUtils.invokeMethod(acr, "result");
      output = template.create().getItem();
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    Level level = blockEntity.getLevel();
    if (level == null) return;

    ChunkAccess chunk = level.getChunk(blockEntity.getBlockPos());
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);
    PlayerOwnedData playerData = pob.playerOwnedBlocks.get(blockEntity.getBlockPos());
    if (playerData == null) return;

    Player owner = playerData.getOwner((ServerLevel) level);
    if (owner == null) {
      // If the owner is offline, used the saved value of smithing speed.
      returnValue.setReturnValue((int) Math.max(1, returnValue.getReturnValue() * (1f / (1 + playerData.smithingSpeed))));
      return;
    }

    SkillSmithing smithing = owner.getData(Skills.SMITHING);
    if (!smithing.enabledAbilities[SkillSmithing.SMITHING_SPEED_INDEX]) return;

    double multiplier = 1 / (1 + smithing.getSmithingSpeed());

    int newTicks = (int) Math.max(1, returnValue.getReturnValue() * multiplier);
    returnValue.setReturnValue(newTicks);
  }


  @Inject(
    method = {"serverTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V")}
  )
  private static void beforeBurn(ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity, CallbackInfo ci, @Local(name = {"burnResult"}) ItemStack burnResult) {
    if (level.isClientSide()) return;

    ChunkAccess chunk = level.getChunk(pos);
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);
    PlayerOwnedData playerData = pob.playerOwnedBlocks.get(pos);
    if (playerData == null) return;

    Player owner = playerData.getOwner(level);

    if (owner == null) return;

    // Cooking
    if (SkillCooking.XP_MAP.containsKey(burnResult.getItem())) {
      SkillCooking cooking = owner.getData(Skills.COOKING);
      cooking.addXp(owner, SkillCooking.XP_MAP.get(burnResult.getItem()));
    }

    // Smithing.
    if (SkillSmithing.XP_MAP.containsKey(burnResult.getItem())) {
      SkillSmithing smithing = owner.getData(Skills.SMITHING);

      double bonusXP = 0;
      if (smithing.enabledAbilities[SkillSmithing.DOUBLE_PRODUCTION_INDEX] && Math.random() < smithing.getDoubleProductionChance()) {
        burnResult.setCount(burnResult.count() + 1);
        bonusXP += 5;
      }

      smithing.addXp(owner, SkillSmithing.XP_MAP.get(burnResult.getItem()) + bonusXP);
    }
  }

}
