package com.avi.skram.entity;

import net.minecraft.core.particles.ParticleTypes;
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

public class EnderCreeper extends Creeper {

  public static String NAME = "ender_creeper";

  public EnderCreeper(EntityType<? extends Creeper> type, Level level) {
    super(type, level);
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
    return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.35).add(Attributes.MAX_HEALTH, 40).add(Attributes.ATTACK_DAMAGE, 6).add(Attributes.JUMP_STRENGTH, 0.9).add(Attributes.SAFE_FALL_DISTANCE, 20).add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
  }

  @Override
  public void aiStep() {
    super.aiStep();
    if (this.level().isClientSide()) {
      for (int i = 0; i < 2; ++i) {
        this.level().addParticle(ParticleTypes.PORTAL, this.getRandomX((double) 0.5F), this.getRandomY() - (double) 0.25F, this.getRandomZ((double) 0.5F), (this.random.nextDouble() - (double) 0.5F) * (double) 2.0F, -this.random.nextDouble(), (this.random.nextDouble() - (double) 0.5F) * (double) 2.0F);
      }
    }
  }

}
