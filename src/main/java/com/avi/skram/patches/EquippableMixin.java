package com.avi.skram.patches;

import com.avi.skram.skills.SkillHealth;
import com.avi.skram.skills.Skills;
import com.avi.skram.ui.GenericMessageToast;
import com.avi.skram.ui.ToastUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {Equippable.class})
public class EquippableMixin {

  @Inject(
    method = {"swapWithEquipmentSlot(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/InteractionResult;"},
    at = {@At("HEAD")},
    cancellable = true
  )
  private void overrideSwapWithEquipment(ItemStack inHand, Player player, CallbackInfoReturnable<InteractionResult> cir) {
    SkillHealth health = player.getData(Skills.HEALTH);
    if (inHand == ItemStack.EMPTY) return;
    if (player.isCreative()) return;

    int requiredLevel = Skills.requiredLevel(inHand);
    if (health.level >= requiredLevel) return;

    cir.setReturnValue(InteractionResult.FAIL);

    if (!player.level().isClientSide()) return;

    ToastManager toastManager = Minecraft.getInstance().gui.toastManager();

    if (!ToastUtils.hasGenericMessageToast(toastManager, health.getRequiredLevelToastName())) {
      toastManager.addToast(new GenericMessageToast(
        health.getRequiredLevelToastName(), Skills.SKILL_ICONS.getOrDefault(health.name, Skills.DEFAULT_ICON), "Using a " + inHand.getItemName().getString() + " requires a " + health.name + " level of " + requiredLevel + "."
      ));
    }
  }

}
