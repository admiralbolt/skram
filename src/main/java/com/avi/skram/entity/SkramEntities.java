package com.avi.skram.entity;

import com.avi.skram.Skram;
import com.avi.skram.entity.projectile.Icicle;
import com.avi.skram.entity.projectile.SkramColoredProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Skram.MODID)
public class SkramEntities {

  public static Supplier<EntityType<SkramColoredProjectile>> COLORED_PROJECTILE;
  public static Supplier<EntityType<Decoy>> DECOY;
  public static Supplier<EntityType<Icicle>> ICICLE;
  public static Supplier<EntityType<NapalmCreeper>> NAPALM_CREEPER;

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

    DECOY = Skram.ENTITY_TYPES.register(
      Decoy.NAME, () -> EntityType.Builder.<Decoy>of(
        Decoy::new, MobCategory.MISC
      ).sized(0.9F, 0.9F)
        .build(ResourceKey.create(
          Registries.ENTITY_TYPE,
          Identifier.fromNamespaceAndPath(Skram.MODID, Decoy.NAME)
        ))
    );

    ICICLE = Skram.ENTITY_TYPES.register(
      Icicle.NAME, () -> EntityType.Builder.<Icicle>of(
        Icicle::new, MobCategory.MISC
      ).sized(0.5F, 0.5F)
        .clientTrackingRange(4)
        .updateInterval(10)
        .build(ResourceKey.create(
          Registries.ENTITY_TYPE,
          Identifier.fromNamespaceAndPath(Skram.MODID, Icicle.NAME)
        ))
    );

    NAPALM_CREEPER = Skram.ENTITY_TYPES.register(
      NapalmCreeper.NAME, () -> EntityType.Builder.<NapalmCreeper>of(
        NapalmCreeper::new, MobCategory.MONSTER
      )
        .sized(1.0f, 1.0f)
        .spawnDimensionsScale(1.0f)
        .clientTrackingRange(8)
        .updateInterval(10)
        .fireImmune()
        .build(ResourceKey.create(
          Registries.ENTITY_TYPE,
          Identifier.fromNamespaceAndPath(Skram.MODID, NapalmCreeper.NAME)
        ))
    );
  }

  @SubscribeEvent
  public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
    event.put(DECOY.get(), Decoy.createAttributes().build());
    event.put(NAPALM_CREEPER.get(), NapalmCreeper.createAttributes().build());
  }

  @SubscribeEvent
  public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
    event.register(NAPALM_CREEPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
  }
}
