package com.avi.skram.mixins;

import com.avi.skram.perks.PerkGrenadier;
import com.avi.skram.perks.Perks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ThrowablePotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = {ThrowablePotionItem.class})
public class ThrowablePotionItemMixin {

  @ModifyArg(
    method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileFromRotation(Lnet/minecraft/world/entity/projectile/Projectile$ProjectileFactory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;FFF)Lnet/minecraft/world/entity/projectile/Projectile;")},
    index = 5
  )
  private float modifyThrowingPower(float pow, @Local(name = "player", argsOnly = true) Player player) {
    PerkGrenadier perk = player.getData(Perks.GRENADIER);
    if (!perk.isFullyEnabled()) return pow;

    return pow * 2;
  }

}
