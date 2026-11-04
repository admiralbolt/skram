package com.avi.skram.mixins;

import com.avi.skram.attachments.MiscAttachments;
import com.avi.skram.attachments.PlayerProjectileModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ThrownLingeringPotion.class})
public class ThrownLingeringPotionMixin {

  @Inject(
    method = {"Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrownLingeringPotion;onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.BEFORE)}
  )
  private void makeCloudBigger(ServerLevel level, ItemStack potionItem, HitResult hitResult, CallbackInfo ci, @Local(name = "cloud") AreaEffectCloud cloud) {
    ThrownLingeringPotion entity = (ThrownLingeringPotion) (Object) this;
    PlayerProjectileModifier modifier = entity.getData(MiscAttachments.PLAYER_PROJECTILE_MODIFIER);

    if (modifier.areaMultiplier == 1) return;

    cloud.setRadius((float) (cloud.getRadius() * modifier.areaMultiplier));
  }
}
