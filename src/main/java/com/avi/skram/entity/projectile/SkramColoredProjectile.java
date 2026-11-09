package com.avi.skram.entity.projectile;

import com.avi.skram.entity.SkramEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class SkramColoredProjectile extends AbstractHurtingProjectile {

  public static final String NAME = "colored_projectile";

  public SkramColoredProjectile(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
    super(type, level);
  }

  public SkramColoredProjectile(Level level, LivingEntity mob, Vec3 direction) {
    super(SkramEntities.COLORED_PROJECTILE.get(), mob, direction, level);
  }

  @Override
  protected void onHitEntity(@NonNull EntityHitResult hitResult) {
    // Eventually we should take a supplier function as an arg to make this generic.
  }

  @Override
  protected boolean shouldBurn() {
    return false;
  }

}
