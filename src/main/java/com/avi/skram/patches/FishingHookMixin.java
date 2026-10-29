package com.avi.skram.patches;

import com.avi.skram.skills.SkillFishing;
import com.avi.skram.skills.Skills;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {FishingHook.class})
public class FishingHookMixin {

  @Shadow
  private int timeUntilLured;

  // This took an eternity to figure out, but we need to shift by two here.
  // An assignment like this:
  //   someVar = someFunction()
  // Gets treated as two individual lines. We can't inject directly after someFunction() call.
  @Inject(
    method = {"catchingFish(Lnet/minecraft/core/BlockPos;)V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I", ordinal = 2, shift = At.Shift.BY, by = 2)}
  )
  private void catchingFish(BlockPos blockPos, CallbackInfo ci) {
    FishingHook hook = (FishingHook) (Object) this;
    if (hook.level().isClientSide()) return;

    Player player = hook.getPlayerOwner();
    if (player == null) return;

    SkillFishing fishing = player.getData(Skills.FISHING);
    if (!fishing.enabledAbilities[SkillFishing.FISHING_SPEED_INDEX]) return;

    this.timeUntilLured = (int) ((double) this.timeUntilLured / (1 + fishing.getSpeedBonus()));
  }

}
