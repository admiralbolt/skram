package com.avi.skram.mixins;

import com.avi.skram.attachments.MiscAttachments;
import com.avi.skram.attachments.SkramEntityModifier;
import com.avi.skram.entity.EnderCreeper;
import com.avi.skram.entity.NapalmCreeper;
import com.avi.skram.skills.SkillDemolition;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import com.avi.skram.utils.ReflectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = {Creeper.class})
public class CreeperMixin {

  @Shadow
  private int explosionRadius;

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

  @Inject(
    method = {"explodeCreeper()V"},
    at = {@At(value = "HEAD")},
    cancellable = true)
  private void overrideExplodeCreeper(CallbackInfo ci) {
    Creeper creeper = (Creeper) (Object) this;

    if (creeper instanceof NapalmCreeper) {
      // Fire!
      Level var2 = creeper.level();
      if (var2 instanceof ServerLevel level) {
        try {
          float explosionMultiplier = creeper.isPowered() ? 2.0F : 1.0F;
          ReflectionUtils.setFieldValue(creeper, "dead", true);
          level.explode(creeper, creeper.getX(), creeper.getY(), creeper.getZ(), (float) this.explosionRadius * explosionMultiplier, true, Level.ExplosionInteraction.MOB);
          ReflectionUtils.invokeMethod(creeper, "spawnLingeringCloud");
          ReflectionUtils.invokeMethod(creeper, "triggerOnDeathMobEffects", level, Entity.RemovalReason.KILLED);
          creeper.discard();

          // Create a lava spawn at the source.
          BlockPos pos = new BlockPos((int) creeper.getX(), (int) creeper.getY(), (int) creeper.getZ());
          level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 11);

          // Early return from the normal explode method.
          ci.cancel();
        } catch (Exception e) {
          System.out.println("Bad reflection: " + e);
        }
      }
    }

    if (creeper instanceof EnderCreeper) {
      if (creeper.level() instanceof ServerLevel level) {
        try {
          ReflectionUtils.setFieldValue(creeper, "dead", true);

          // Our "explosion" is something else entirely.
          AABB hitBox = creeper.getHitbox().inflate(6);
          List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, hitBox);

          // I think it's funnier if EVERYTHING gets teleported to the same location.
          double baseDx = MathUtils.randomSign() * (0.4 + 0.3 * MathUtils.RANDOM.nextDouble());
          double baseDy = (0.4 + 0.3 * MathUtils.RANDOM.nextDouble());
          double baseDz = MathUtils.randomSign() * (0.4 + 0.3 * MathUtils.RANDOM.nextDouble());
          for (LivingEntity entity : entities) {
            // Randomly teleport outward and upward. Power scales based on distance and blast resistance.
            double power = 130;
            power *= Math.min(1.0, 1.0 / entity.distanceTo(creeper));

            if (entity instanceof Player player) {
              SkillDemolition demolition = player.getData(Skills.DEMOLITION);
              power *= demolition.getBlastResistance();
            }

            // Pick a random positive vertical direction.
            entity.teleportRelative(
              baseDx * power,
              baseDy * power,
              baseDz * power
            );
          }

          // Mostly for visual effects.
          level.explode(creeper, creeper.getX(), creeper.getY(), creeper.getZ(), 0.8f, Level.ExplosionInteraction.MOB);
          ReflectionUtils.invokeMethod(creeper, "spawnLingeringCloud");
          ReflectionUtils.invokeMethod(creeper, "triggerOnDeathMobEffects", level, Entity.RemovalReason.KILLED);
          creeper.discard();
          // Early return from the normal explode method.
          ci.cancel();
        } catch (Exception e) {
          System.out.println("Bad reflection: " + e);
        }
      }
    }


  }
}
