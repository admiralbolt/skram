package com.avi.skram.mixins;

import com.avi.skram.attachments.MiscAttachments;
import com.avi.skram.attachments.PlayerProjectileModifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = {ThrownSplashPotion.class})
public class ThrownSplashPotionMixin {


  @ModifyArg(
    method = {"onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;")},
    index = 1
  )
  private AABB increaseHitBox(AABB bb) {
    AABB newHitBox = bb;
    ThrownSplashPotion entity = (ThrownSplashPotion) (Object) this;
    PlayerProjectileModifier modifier = entity.getData(MiscAttachments.PLAYER_PROJECTILE_MODIFIER);

    if (modifier.areaMultiplier != 1.0) {
      double xSize = newHitBox.maxX - newHitBox.minX;
      double ySize = newHitBox.maxY - newHitBox.minY;
      double zSize = newHitBox.maxZ - newHitBox.minZ;

      newHitBox = newHitBox.inflate(
        xSize * (modifier.areaMultiplier - 1) / 2,
        ySize * (modifier.areaMultiplier - 1) / 2,
        zSize * (modifier.areaMultiplier - 1) / 2
      );
    }

    return newHitBox;
  }

  // Annoyingly, the maximum range for a potions splash isn't only handled by the bounding box, but also an arbitrary
  // distance of 16, see this line =>
  //    if (dist < (double)16.0F) {
  // So we need to inject THAT particular constant as well.
  @ModifyExpressionValue(
    method = {"onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V"},
    at = {@At(value = "CONSTANT", args = "doubleValue=16.0")}
  )
  private double increaseDist(double original) {
    ThrownSplashPotion entity = (ThrownSplashPotion) (Object) this;
    PlayerProjectileModifier modifier = entity.getData(MiscAttachments.PLAYER_PROJECTILE_MODIFIER);
    // NOTE: The math used here is using distance squared, so we need to square our multiplier accordingly.
    return (original * modifier.areaMultiplier * modifier.areaMultiplier);
  }

  // AND we compute a scale based on the total allowed distance away here:
  //  double scale = (double)1.0F - Math.sqrt(dist) / (double)4.0F;
  // Which is based on the old max distance of 4 aka max squared distance 16.
  @ModifyExpressionValue(
    method = {"onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V"},
    at = {@At(value = "CONSTANT", args = "doubleValue=4.0", ordinal = 2)}
  )
  private double increaseScaleDivisor(double original) {
    ThrownSplashPotion entity = (ThrownSplashPotion) (Object) this;
    PlayerProjectileModifier modifier = entity.getData(MiscAttachments.PLAYER_PROJECTILE_MODIFIER);
    return original * modifier.areaMultiplier;
  }

}
