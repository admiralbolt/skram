package com.avi.skram.patches;

import com.avi.skram.skills.SkillDemolition;
import com.avi.skram.skills.Skills;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {PrimedTnt.class})
public class PrimedTntMixin {

  @Shadow
  private EntityReference<LivingEntity> owner;

  @Shadow
  private float explosionPower;

  @Inject(
    method = {"explode()V"},
    at = {@At(value = "RETURN")}
  )
  private void overrideExplode(CallbackInfo ci) {
    PrimedTnt tnt = (PrimedTnt) (Object) this;

    if (tnt.level().isClientSide()) return;

    LivingEntity entity = EntityReference.getLivingEntity(this.owner, tnt.level());
    if (entity == null) return;
    if (!(entity instanceof Player player)) return;

    SkillDemolition demolition = player.getData(Skills.DEMOLITION);
    demolition.addXp(player, 80 * this.explosionPower);
  }
}
