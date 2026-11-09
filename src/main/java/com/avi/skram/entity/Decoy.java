package com.avi.skram.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class Decoy extends Goat {

  public static final String NAME = "decoy";

  public int ticksAlive = 0;

  public Decoy(EntityType<? extends Goat> type, Level level) {
    super(type, level);
  }

  public Decoy(Level level, int ticksAlive) {
    super(SkramEntities.DECOY.get(), level);
    this.ticksAlive = ticksAlive;
    this.setScreamingGoat(true);
  }

  @Override
  public void tick() {
    super.tick();

    if (this.level().isClientSide()) return;

    if (this.ticksAlive <= 0) {
      this.kill((ServerLevel) this.level());
    }
    this.ticksAlive--;
  }

  public static AttributeSupplier.@NonNull Builder createAttributes() {
    return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 10).add(Attributes.MOVEMENT_SPEED, 0.001).add(Attributes.ATTACK_DAMAGE, 0).add(Attributes.JUMP_STRENGTH, 0);
  }

}
