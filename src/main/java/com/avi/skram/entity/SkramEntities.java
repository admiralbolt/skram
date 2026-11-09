package com.avi.skram.entity;

import com.avi.skram.Skram;
import com.avi.skram.entity.projectile.SkramColoredProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class SkramEntities {

  public static Supplier<EntityType<SkramColoredProjectile>> COLORED_PROJECTILE;

  public static void register() {
    COLORED_PROJECTILE = Skram.ENTITY_TYPES.register(
      SkramColoredProjectile.NAME, () -> EntityType.Builder.<SkramColoredProjectile>of(
          SkramColoredProjectile::new, MobCategory.MISC
        ).sized(0.5F, 0.5F)
        .clientTrackingRange(4)
        .updateInterval(10)
        .build(ResourceKey.create(
          Registries.ENTITY_TYPE,
          Identifier.fromNamespaceAndPath(Skram.MODID, SkramColoredProjectile.NAME)
        ))
    );
  }

}
