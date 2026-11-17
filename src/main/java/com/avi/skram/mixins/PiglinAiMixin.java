package com.avi.skram.mixins;

import com.avi.skram.skills.SkillMercantile;
import com.avi.skram.skills.Skills;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = {PiglinAi.class})
public class PiglinAiMixin {

  @Inject(
    method = {"throwItemsTowardPlayer(Lnet/minecraft/world/entity/monster/piglin/Piglin;Lnet/minecraft/world/entity/player/Player;Ljava/util/List;)V"},
    at = {@At(value = "HEAD")}
  )
  private static void givePlayerXPOnPiglinTrade(Piglin body, Player player, List<ItemStack> itemStacks, CallbackInfo ci) {
    if (player.level().isClientSide()) return;

    SkillMercantile mercantile = player.getData(Skills.MERCANTILE);
    mercantile.addXp(player, 100);
  }
}
