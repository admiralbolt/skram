package com.avi.skram.patches;

import com.avi.skram.skills.SkillHealth;
import com.avi.skram.skills.Skills;
import com.avi.skram.ui.GenericMessageToast;
import com.avi.skram.ui.ToastUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {IItemStackExtension.class})
public interface IItemStackExtensionMixin {

  @Inject(
    method = {"canEquip(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;)Z"},
    at = {@At(value = "RETURN")},
    cancellable = true
  )
  private void overrideCanEquip(EquipmentSlot armorType, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
    if (!(entity instanceof Player player)) return;

    ItemStack stack = (ItemStack) (Object) this;
    SkillHealth health = player.getData(Skills.HEALTH);
    if (stack == ItemStack.EMPTY) return;
    if (player.isCreative()) return;

    int requiredLevel = Skills.requiredLevel(stack);
    if (health.level >= requiredLevel) return;

    cir.setReturnValue(false);

    if (!player.level().isClientSide()) return;

    ToastManager toastManager = Minecraft.getInstance().gui.toastManager();

    if (!ToastUtils.hasGenericMessageToast(toastManager, health.getRequiredLevelToastName())) {
      toastManager.addToast(new GenericMessageToast(
        health.getRequiredLevelToastName(), Skills.SKILL_ICONS.getOrDefault(health.name, Skills.DEFAULT_ICON), "Using a " + stack.getItemName().getString() + " requires a " + health.name + " level of " + requiredLevel + "."
      ));
    }
  }

}
