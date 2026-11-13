package com.avi.skram.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

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

  public static void teleportBetweenDimensions(Player player) {
    if (!(player.level() instanceof ServerLevel currentLevel)) return;

    ResourceKey<Level> targetDimension = currentLevel.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER;
    ServerLevel targetLevel = currentLevel.getServer().getLevel(targetDimension);
    if (targetLevel == null) return;

    boolean goingToNether = targetLevel.dimension() == Level.NETHER;
    WorldBorder targetWorldBorder = targetLevel.getWorldBorder();
    double coordinateScale = DimensionType.getTeleportationScale(currentLevel.dimensionType(), targetLevel.dimensionType());
    BlockPos approximateExitPos = targetWorldBorder.clampToBounds(
      player.getX() * coordinateScale,
      player.getY(),
      player.getZ() * coordinateScale
    );

    Optional<BlockPos> portalPos = targetLevel.getPortalForcer()
      .findClosestPortalPosition(approximateExitPos, goingToNether, targetWorldBorder);
    BlockPos exitPos = portalPos.orElse(approximateExitPos);
    Vec3 targetPos = new Vec3(exitPos.getX() + 0.5, exitPos.getY(), exitPos.getZ() + 0.5);
    TeleportTransition.PostTeleportTransition postTeleport = TeleportTransition.PLAY_PORTAL_SOUND;

    if (portalPos.isPresent()) {
      BlockState portalState = targetLevel.getBlockState(exitPos);
      Direction.Axis portalAxis = portalState.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS)
        .orElse(Direction.Axis.X);
      BlockUtil.FoundRectangle portal = BlockUtil.getLargestRectangleAround(
        exitPos,
        portalAxis,
        21,
        Direction.Axis.Y,
        21,
        pos -> targetLevel.getBlockState(pos) == portalState
      );

      BlockPos bottomLeft = portal.minCorner;
      EntityDimensions dimensions = player.getDimensions(player.getPose());
      double offsetRight = dimensions.width() / 2.0 + (portal.axis1Size - dimensions.width()) / 2.0;
      double offsetUp = (portal.axis2Size - dimensions.height()) / 2.0;
      boolean xAligned = portalAxis == Direction.Axis.X;
      targetPos = new Vec3(
        bottomLeft.getX() + (xAligned ? offsetRight : 0.5),
        bottomLeft.getY() + offsetUp,
        bottomLeft.getZ() + (xAligned ? 0.5 : offsetRight)
      );
      postTeleport = TeleportTransition.PLAY_PORTAL_SOUND.then(entity -> entity.placePortalTicket(exitPos));
    }

    Vec3 collisionFreePos = PortalShape.findCollisionFreePosition(
      targetPos,
      targetLevel,
      player,
      player.getDimensions(player.getPose())
    );
    player.teleport(new TeleportTransition(
      targetLevel,
      collisionFreePos,
      Vec3.ZERO,
      player.getYRot(),
      player.getXRot(),
      postTeleport
    ));
  }

  public static void applyKnockback(Entity entity, Vec3 sourceLocation, double power) {
    if (power <= 0) return;

    // If the entity is a living entity, we want to apply it's knockback resistance.
    if (entity instanceof LivingEntity live) {
      power *= (1.0 - live.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }

    Vec3 forceVector = entity.position().subtract(sourceLocation).normalize().scale(power);
    entity.setDeltaMovement(forceVector);
  }

  public static void applyPull(Entity entity, Vec3 sourceLocation, double power) {
    if (power <= 0) return;

    // If the entity is a living entity, we want to apply it's knockback resistance.
    if (entity instanceof LivingEntity live) {
      power *= (1.0 - live.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }

    Vec3 forceVector = sourceLocation.subtract(entity.position()).normalize().scale(power);
    entity.setDeltaMovement(forceVector);
  }

  public static boolean isLocalPlayer(Player player) {
    return player.level().isClientSide() && player == Minecraft.getInstance().player;
  }

}
