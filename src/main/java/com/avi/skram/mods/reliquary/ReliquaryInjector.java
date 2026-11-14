package com.avi.skram.mods.reliquary;

import com.avi.skram.Skram;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import reliquary.init.ModItems;
import reliquary.util.InventoryHelper;

@EventBusSubscriber(modid = Skram.MODID)
public class ReliquaryInjector {

  // Nerf infernal claws / chalice heavily.
  // Was hoping to do this directly, but instead we subscribe to the same events and run the same checks.
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void beforePlayerHurt(LivingIncomingDamageEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;

    if ((event.getSource() == player.damageSources().inFire() || event.getSource() == player.damageSources().onFire()) && InventoryHelper.playerHasItem(player, ModItems.INFERNAL_CLAWS.get())) {
      player.causeFoodExhaustion(event.getAmount() * 2.25f);
      return;
    }

    if (event.getSource().is(DamageTypeTags.IS_FIRE) && InventoryHelper.playerHasItem(player, ModItems.INFERNAL_CHALICE.get())) {
      player.causeFoodExhaustion(event.getAmount() * 1.75f);
    }
  }
}
