package com.avi.skram.mixins;

import com.avi.skram.attachments.MiscAttachments;
import com.avi.skram.attachments.SkramEntityModifier;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {Creeper.class})
public class CreeperMixin {

  @Inject(
    method = {"getSwellDir()I"},
    at = {@At(value = "RETURN")},
    cancellable = true
  )
  private void getSwellDir(CallbackInfoReturnable<Integer> cir) {
    // Get our override.
    Creeper creeper = (Creeper) (Object) this;
    SkramEntityModifier modifier = creeper.getData(MiscAttachments.SKRAM_ENTITY_MODIFIER);
    if (!modifier.canExplode) {
      cir.setReturnValue(0);
    }
  }
}
