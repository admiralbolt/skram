package com.avi.skram.entity;

import com.avi.skram.utils.ReflectionUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class NapalmCreeper extends Creeper {

  public static String NAME = "napalm_creeper";

  public NapalmCreeper(EntityType<? extends Creeper> type, Level level) {
    super(type, level);

    // This technically has private access, so we do a funny thing and use reflection.
    try {
      ReflectionUtils.setFieldValue(this, "explosionRadius", 6);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("Can't set explosionRadius for Napalm Creeper. Ah: " + e);
    }
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(1, new FloatGoal(this));
    this.goalSelector.addGoal(2, new SwellGoal(this));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, (double)1.0F, false));
    this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
    this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
    this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
  }

  public static AttributeSupplier.@NonNull Builder createAttributes() {
    return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 30).add(Attributes.ATTACK_DAMAGE, 6).add(Attributes.JUMP_STRENGTH, 0.9).add(Attributes.SAFE_FALL_DISTANCE, 20).add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
  }

}
