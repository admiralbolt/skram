package com.avi.skram.patches;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlocks;
import com.avi.skram.blocks.PlayerOwnedData;
import com.avi.skram.skills.SkillDemolition;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.ReflectionUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {TntBlock.class})
public class TntBlockMixin {

  @Inject(
    method = {"prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Z"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z")}
  )
  private static void overridePrime(Level level, BlockPos pos, LivingEntity source, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir, @Local(name = "tnt") LocalRef<PrimedTnt> tnt) {
    // This is called immediately after the PrimedTnt is created, before it's added to the world.
    if (level.isClientSide()) return;

    PrimedTnt primedTnt = tnt.get();

    ChunkAccess chunk = level.getChunk(pos);
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);
    PlayerOwnedData playerData = pob.playerOwnedBlocks.get(pos);
    if (playerData == null) return;

    Player owner = playerData.getOwner((ServerLevel) level);

    float explosionPower = 0;

    if (owner != null) {
      SkillDemolition demolition = owner.getData(Skills.DEMOLITION);
      if (demolition.enabledAbilities[SkillDemolition.BLAST_POWER_INDEX]) {
        explosionPower = demolition.getBlastPower();;
      }

      // We also want to make sure that the owner of the PrimedTnt is set appropriately.
      try {
        ReflectionUtils.setFieldValue(primedTnt, "owner", EntityReference.of(owner));
      } catch (NoSuchFieldException | IllegalAccessException e) {
        System.out.println("aaaaaaah");
      }
    } else {
      // Fallback to playerData if the player isn't logged in.
      explosionPower = playerData.explosionPower;
    }

    // Override the value of explosionPower.
    try {
      float baseExplosionPower = (float) ReflectionUtils.getFieldValue(primedTnt, "explosionPower");
      ReflectionUtils.setFieldValue(primedTnt, "explosionPower", baseExplosionPower * (1 + explosionPower));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("aaaah2");
    }
  }
}
