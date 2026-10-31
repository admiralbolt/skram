package com.avi.skram.patches;

import com.avi.skram.skills.SkillMercantile;
import com.avi.skram.skills.Skills;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {Villager.class})
public class VillagerMixin {

  @Inject(
    method = {"updateSpecialPrices(Lnet/minecraft/world/entity/player/Player;)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/villager/Villager;getTradingPlayer()Lnet/minecraft/world/entity/player/Player;", shift = At.Shift.BY, by = 2)}
  )
  private static void overrideUpdateSpecialPrices(Player player, CallbackInfo ci, @Local(name = "merchantOffers") MerchantOffers merchantOffers) {
    SkillMercantile mercantile = player.getData(Skills.MERCANTILE);
    if (!mercantile.enabledAbilities[SkillMercantile.PRICE_REDUCTION_INDEX]) return;

    for (MerchantOffer offer : merchantOffers) {
      int costReduction = (int) (offer.getBaseCostA().getCount() * (1 - mercantile.getPriceReduction()));
      if (costReduction == 0) continue;

      offer.addToSpecialPriceDiff(-costReduction);
    }
  }
}
