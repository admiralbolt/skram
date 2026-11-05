package com.avi.skram.mixins;

import com.avi.skram.perks.PerkExpertEnchanter;
import com.avi.skram.perks.PerkSpinSlash;
import com.avi.skram.perks.Perks;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {Player.class})
public class PlayerMixin {

  @Inject(
    method = {"doSweepAttack(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;FLnet/minecraft/world/phys/AABB;)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;square(D)D", ordinal = 0, shift = At.Shift.BY, by = 2)}
  )
  private void overrideDoSweepAttack(Entity entity, float baseDamage, DamageSource damageSource, float attackStrengthScale, AABB sweepHitBox, CallbackInfo ci, @Local(name = "entityReachSq") LocalDoubleRef entityReachSq) {
    Player player = (Player) (Object) this;

    PerkSpinSlash perk = player.getData(Perks.SPIN_SLASH);
    if (!perk.isFullyEnabled()) return;

    entityReachSq.set(entityReachSq.get() * (1 + PerkSpinSlash.INCREASE));
  }

  @ModifyVariable(
    method = {"doSweepAttack(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;FLnet/minecraft/world/phys/AABB;)V"},
    at = @At("HEAD"),
    argsOnly = true,
    name = "sweepHitBox"
  )
  private AABB expandSweepHitbox(AABB sweepHitBox) {
    Player player = (Player) (Object) this;

    PerkSpinSlash perk = player.getData(Perks.SPIN_SLASH);
    if (!perk.isFullyEnabled()) return sweepHitBox;

    return sweepHitBox.inflate(
      (sweepHitBox.getXsize() * (PerkSpinSlash.INCREASE)) / 2,
      0,
      (sweepHitBox.getZsize() * (PerkSpinSlash.INCREASE)) / 2
    );
  }

  @ModifyArg(
    method = {"onEnchantmentPerformed(Lnet/minecraft/world/item/ItemStack;I)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;giveExperienceLevels(I)V")},
    index = 0
  )
  private int overrideEnchantmentCost(int amount) {
    Player player = (Player) (Object) this;
    PerkExpertEnchanter perk = player.getData(Perks.EXPERT_ENCHANTER);

    return perk.isFullyEnabled() ? 0 : amount;
  }

  @ModifyArg(
    method = {"causeFoodExhaustion(F)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V")},
    index = 0
  )
  private float overrideFoodExhaustion(float amount) {
    return amount * 1.5f;
  }



}
