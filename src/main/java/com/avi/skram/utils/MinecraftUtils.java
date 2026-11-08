package com.avi.skram.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MinecraftUtils {

  public static HitResult rayCast(Player player, double distance) {
    Level level = player.level();
    Vec3 eyePosition = player.getEyePosition(1.0F);
    Vec3 lookVector = player.getViewVector(1.0F);
    Vec3 endPosition = eyePosition.add(lookVector.scale(distance));

    ClipContext context = new ClipContext(
      eyePosition,
      endPosition,
      ClipContext.Block.OUTLINE,
      ClipContext.Fluid.ANY,
      player
    );

    return level.clip(context);
  }

}
