package com.avi.skram.mixins;

import com.avi.skram.attachments.MiscAttachments;
import com.avi.skram.attachments.PlayerProjectileModifier;
import com.avi.skram.perks.PerkGrenadier;
import com.avi.skram.perks.Perks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(value = {Projectile.class})
public class ProjectileMixin {

  @Inject(
    method = {"spawnProjectile(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;)Lnet/minecraft/world/entity/projectile/Projectile;"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.BEFORE)}
  )
  private static <T extends Projectile> void attachPlayerProjectileModifier(T projectile, ServerLevel serverLevel, ItemStack itemStack, Consumer<T> shootFunction, CallbackInfoReturnable<T> cir) {
    if (!(projectile.getOwner() instanceof Player player)) return;

    // Make and add our attachment! Remember, calling getData() creates a default instance.
    PlayerProjectileModifier modifier = new PlayerProjectileModifier();

    PerkGrenadier perk = player.getData(Perks.GRENADIER);
    if (perk.isFullyEnabled()) {
      modifier.areaMultiplier = 2.0;
    }

    projectile.setData(MiscAttachments.PLAYER_PROJECTILE_MODIFIER, modifier);
  }
}
