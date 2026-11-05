package com.avi.skram.mixins;

import com.avi.skram.attachments.MiscAttachments;
import com.avi.skram.attachments.PlayerDoubleJumpTracker;
import com.avi.skram.perks.PerkSpaceJump;
import com.avi.skram.perks.Perks;
import com.avi.skram.skills.SkillAcrobatics;
import com.avi.skram.skills.Skills;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {LivingEntity.class})
public class LivingEntityMixin {

  @Shadow
  private int noJumpDelay;

  @ModifyExpressionValue(
    method = {"aiStep()V"},
    at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onGround()Z")}
  )
  private boolean onGround(boolean original) {
    LivingEntity entity = (LivingEntity) (Object) this;
    if (!(entity instanceof Player player)) return original;

    PerkSpaceJump perk = player.getData(Perks.SPACE_JUMP);
    if (!perk.isFullyEnabled()) return original;

    PlayerDoubleJumpTracker tracker = player.getData(MiscAttachments.PLAYER_DOUBLE_JUMP_TRACKER);
    if (!original && this.noJumpDelay <= 3 && tracker.jumpAvailable) {
      tracker.jumpAvailable = false;
      player.setData(MiscAttachments.PLAYER_DOUBLE_JUMP_TRACKER, tracker);

      SkillAcrobatics acrobatics = player.getData(Skills.ACROBATICS);
      acrobatics.addXp(player, 50, true);
      return true;
    }

    return original;
  }

  @Inject(
    method = {"aiStep()V"},
    at = {@At(value = "HEAD")}
  )
  private void resetDoubleJumpTracker(CallbackInfo ci) {
    LivingEntity entity = (LivingEntity) (Object) this;
    if (!(entity instanceof Player player)) return;
    if (!entity.onGround()) return;

    PerkSpaceJump perk = player.getData(Perks.SPACE_JUMP);
    if (!perk.isFullyEnabled()) return;

    PlayerDoubleJumpTracker tracker = player.getData(MiscAttachments.PLAYER_DOUBLE_JUMP_TRACKER);
    if (tracker.jumpAvailable) return;

    tracker.jumpAvailable = true;
    player.setData(MiscAttachments.PLAYER_DOUBLE_JUMP_TRACKER, tracker);
  }


}
