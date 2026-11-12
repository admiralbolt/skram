package com.avi.skram.perks;

import com.avi.skram.skills.SkillHusbandry;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;

import java.util.List;
import java.util.Map;

public class PerkGenealogy extends Perk {
  public static final String NAME = "genealogy";
  public static final String DESCRIPTION = "Bred horses have increased stats, and can exceed normal maximum values.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillHusbandry.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkGenealogy> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkGenealogy::new);

  public static final double MAX_HEALTH = 30;
  public static final double MAX_SPEED = 0.3375;
  public static final double MAX_JUMP = 1.0;

  public static List<ItemStack> getResearchCost() {
    return List.of(

    );
  }

  public PerkGenealogy() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkGenealogy(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void makeHorseGoFast(BabyEntitySpawnEvent event) {
    if (event.getCausedByPlayer() == null) return;

    Player player = event.getCausedByPlayer();
    if (player.level().isClientSide()) return;
    if (!(event.getChild() instanceof AbstractHorse horse)) return;

    applyAttributes(event.getParentA(), event.getParentB(), horse);
  }

  public static void applyAttributes(Mob parentA, Mob parentB, AbstractHorse horse) {
    // For each attribute (health, jump, movement speed), add a bonus based on a percentage of the higher
    // value between the two parents.
    double highestHealth = Math.max(parentA.getMaxHealth(), parentB.getMaxHealth());
    AttributeInstance healthAttribute = horse.getAttribute(Attributes.MAX_HEALTH);
    double healthMultiplier = 0.1 * Math.min(1, (2 * MAX_HEALTH / (MAX_HEALTH + highestHealth)));
    healthAttribute.setBaseValue(healthAttribute.getBaseValue() + highestHealth * healthMultiplier);

    double highestSpeed = Math.max(parentA.getAttributeBaseValue(Attributes.MOVEMENT_SPEED), parentB.getAttributeBaseValue(Attributes.MOVEMENT_SPEED));
    AttributeInstance speedAttribute = horse.getAttribute(Attributes.MOVEMENT_SPEED);
    double speedMultiplier = 0.1 * Math.min(1, (2 * MAX_SPEED / (MAX_SPEED + highestSpeed)));
    speedAttribute.setBaseValue(speedAttribute.getBaseValue() + speedMultiplier);

    double highestJump = Math.max(parentA.getAttributeBaseValue(Attributes.JUMP_STRENGTH), parentB.getAttributeBaseValue(Attributes.JUMP_STRENGTH));
    AttributeInstance jumpAttribute = horse.getAttribute(Attributes.JUMP_STRENGTH);
    double jumpMultiplier = 0.1 * Math.min(1, (2 * MAX_JUMP / (MAX_JUMP + highestJump)));
    jumpAttribute.setBaseValue(jumpAttribute.getBaseValue() + jumpMultiplier);

    // We also need to add a safe fall distance boost :)
    AttributeInstance safeFallAttribute = horse.getAttribute(Attributes.SAFE_FALL_DISTANCE);
    safeFallAttribute.setBaseValue(safeFallAttribute.getBaseValue() + 10 * jumpAttribute.getBaseValue() * jumpAttribute.getBaseValue());
  }
}
