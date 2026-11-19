package com.avi.skram.mixins;

import com.avi.skram.skills.SkillMercantile;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.ReflectionUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {WanderingTrader.class})
public class WanderingTraderMixin {

  @Inject(
    method = {"mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/wanderingtrader/WanderingTrader;openTradingScreen(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/network/chat/Component;I)V", shift = At.Shift.BEFORE)}
  )
  private void applyMercantileToWanderingTrader(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    WanderingTrader trader = (WanderingTrader) (Object) this;
    MerchantOffers offers = trader.getOffers();

    SkillMercantile mercantile = player.getData(Skills.MERCANTILE);
    if (!mercantile.enabledAbilities[SkillMercantile.PRICE_REDUCTION_INDEX]) return;

    for (MerchantOffer offer : offers) {
      offer.resetSpecialPriceDiff();

      int costReduction = (int) (offer.getBaseCostA().getCount() * (1 - mercantile.getPriceReduction()));
      if (costReduction == 0) continue;

      offer.addToSpecialPriceDiff(-costReduction);
    }
  }
}
