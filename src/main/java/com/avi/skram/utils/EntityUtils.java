package com.avi.skram.utils;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;

public class EntityUtils {

  // The ONLY reason I'm doing this is because I can't lookup spawn eggs during datagen. Cries.
  public static final Map<EntityType<?>, Item> ENTITY_TO_SPAWN_EGG = ImmutableMap.<EntityType<?>, Item>builder()
    .put(EntityTypes.BLAZE, Items.BLAZE_SPAWN_EGG)
    .put(EntityTypes.BOGGED, Items.BOGGED_SPAWN_EGG)
    .put(EntityTypes.BREEZE, Items.BREEZE_SPAWN_EGG)
    .put(EntityTypes.CAMEL_HUSK, Items.CAMEL_HUSK_SPAWN_EGG)
    .put(EntityTypes.CAVE_SPIDER, Items.CAVE_SPIDER_SPAWN_EGG)
    .put(EntityTypes.CREAKING, Items.CREAKING_SPAWN_EGG)
    .put(EntityTypes.CREEPER, Items.CREEPER_SPAWN_EGG)
    .put(EntityTypes.DROWNED, Items.DROWNED_SPAWN_EGG)
    .put(EntityTypes.ELDER_GUARDIAN, Items.ELDER_GUARDIAN_SPAWN_EGG)
    .put(EntityTypes.ENDER_DRAGON, Items.ENDER_DRAGON_SPAWN_EGG)
    .put(EntityTypes.ENDERMAN, Items.ENDERMAN_SPAWN_EGG)
    .put(EntityTypes.ENDERMITE, Items.ENDERMITE_SPAWN_EGG)
    .put(EntityTypes.EVOKER, Items.EVOKER_SPAWN_EGG)
    .put(EntityTypes.GHAST, Items.GHAST_SPAWN_EGG)
    .put(EntityTypes.GUARDIAN, Items.GUARDIAN_SPAWN_EGG)
    .put(EntityTypes.HOGLIN, Items.HOGLIN_SPAWN_EGG)
    .put(EntityTypes.HUSK, Items.HUSK_SPAWN_EGG)
    .put(EntityTypes.MAGMA_CUBE, Items.MAGMA_CUBE_SPAWN_EGG)
    .put(EntityTypes.PARCHED, Items.PARCHED_SPAWN_EGG)
    .put(EntityTypes.PHANTOM, Items.PHANTOM_SPAWN_EGG)
    .put(EntityTypes.PIGLIN, Items.PIGLIN_SPAWN_EGG)
    .put(EntityTypes.PIGLIN_BRUTE, Items.PIGLIN_BRUTE_SPAWN_EGG)
    .put(EntityTypes.PILLAGER, Items.PILLAGER_SPAWN_EGG)
    .put(EntityTypes.RAVAGER, Items.RAVAGER_SPAWN_EGG)
    .put(EntityTypes.SHULKER, Items.SHULKER_SPAWN_EGG)
    .put(EntityTypes.SILVERFISH, Items.SILVERFISH_SPAWN_EGG)
    .put(EntityTypes.SKELETON, Items.SKELETON_SPAWN_EGG)
    .put(EntityTypes.SLIME, Items.SLIME_SPAWN_EGG)
    .put(EntityTypes.SPIDER, Items.SPIDER_SPAWN_EGG)
    .put(EntityTypes.STRAY, Items.STRAY_SPAWN_EGG)
    .put(EntityTypes.VEX, Items.VEX_SPAWN_EGG)
    .put(EntityTypes.VINDICATOR, Items.VINDICATOR_SPAWN_EGG)
    .put(EntityTypes.WITCH, Items.WITCH_SPAWN_EGG)
    .put(EntityTypes.WITHER_SKELETON, Items.WITHER_SKELETON_SPAWN_EGG)
    .put(EntityTypes.WITHER, Items.WITHER_SPAWN_EGG)
    .put(EntityTypes.ZOGLIN, Items.ZOGLIN_SPAWN_EGG)
    .put(EntityTypes.ZOMBIE_VILLAGER, Items.ZOMBIE_VILLAGER_SPAWN_EGG)
    .put(EntityTypes.ZOMBIE, Items.ZOMBIE_SPAWN_EGG)
    .put(EntityTypes.ZOMBIE_HORSE, Items.ZOMBIE_HORSE_SPAWN_EGG)
    .put(EntityTypes.ZOMBIFIED_PIGLIN, Items.ZOMBIFIED_PIGLIN_SPAWN_EGG)
    .put(EntityTypes.ZOMBIE_NAUTILUS, Items.ZOMBIE_NAUTILUS_SPAWN_EGG)
    .build();

}
