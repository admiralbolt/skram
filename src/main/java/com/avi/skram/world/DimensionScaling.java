package com.avi.skram.world;

import com.avi.skram.Skram;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Map;

// Apply custom bonuses to all entities based on dimension.
@EventBusSubscriber(modid = Skram.MODID)
public class DimensionScaling {

  public static Map<Holder<Attribute>, AttributeModifier> NETHER_MODIFIERS = Map.of(
    Attributes.ATTACK_DAMAGE, new AttributeModifier(Identifier.fromNamespaceAndPath(Skram.MODID, "nether_attack_damage"), 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
    Attributes.MOVEMENT_SPEED, new AttributeModifier(Identifier.fromNamespaceAndPath(Skram.MODID, "nether_movement_speed"), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
    Attributes.MAX_HEALTH, new AttributeModifier(Identifier.fromNamespaceAndPath(Skram.MODID, "nether_health"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
    Attributes.ARMOR, new AttributeModifier(Identifier.fromNamespaceAndPath(Skram.MODID, "nether_armor"), 1, AttributeModifier.Operation.ADD_VALUE)
  );

  public static Map<Holder<Attribute>, AttributeModifier> END_MODIFIERS = Map.of(
    Attributes.ATTACK_DAMAGE, new AttributeModifier(Identifier.fromNamespaceAndPath(Skram.MODID, "end_attack_damage"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
    Attributes.MOVEMENT_SPEED, new AttributeModifier(Identifier.fromNamespaceAndPath(Skram.MODID, "end_movement_speed"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
    Attributes.MAX_HEALTH, new AttributeModifier(Identifier.fromNamespaceAndPath(Skram.MODID, "end_health"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
    Attributes.ARMOR, new AttributeModifier(Identifier.fromNamespaceAndPath(Skram.MODID, "end_armor"), 3, AttributeModifier.Operation.ADD_VALUE)
  );

  public static Map<ResourceKey<Level>, Map<Holder<Attribute>, AttributeModifier>> ALL_MODIFIERS = Map.of(
    Level.NETHER, NETHER_MODIFIERS,
    Level.END, END_MODIFIERS
  );

  public static void applyModifiers(LivingEntity entity, Map<Holder<Attribute>, AttributeModifier> modifiers) {
    if (modifiers == null || modifiers.isEmpty()) return;

    AttributeMap attributes = entity.getAttributes();
    for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : modifiers.entrySet()) {
      AttributeInstance instance = attributes.getInstance(entry.getKey());
      if (instance == null) continue;

      instance.addOrReplacePermanentModifier(entry.getValue());
    }

    // Make sure we update the current health.
    entity.setHealth(entity.getMaxHealth());
  }

  @SubscribeEvent
  public static void handleJoin(EntityJoinLevelEvent event) {
    if (!(event.getEntity() instanceof LivingEntity entity)) return;
    if (entity.level().isClientSide()) return;
    if (!(entity instanceof Mob)) return;
    if (entity.getType().getCategory().isFriendly()) return;

    applyModifiers(entity, ALL_MODIFIERS.get(entity.level().dimension()));
  }

}
