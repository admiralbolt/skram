package com.avi.skram.mods.infernal_mobs.modifiers;

import atomicstryker.infernalmobs.common.MobModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

public class MobModifierBarraging extends MobModifier {

  public static String[] PREFIX = {"barraging"};
  public static String[] SUFFIX = {"theSwarm"};

  public static long COOLDOWN_MILLISECONDS = 1 * 1000;
  public long lastUsedTime = 0L;

  public MobModifierBarraging() {
    super();
  }

  public MobModifierBarraging(MobModifier next) {
    super(next);
  }

  @Override
  public String getModName() {
    return "barraging";
  }

  @Override
  protected String[] getModNamePrefix() {
    return PREFIX;
  }

  @Override
  public String[] getModNameSuffix() {
    return SUFFIX;
  }

  @Override
  public boolean onUpdate(LivingEntity mob) {
    if (this.hasSteadyTarget() && this.getMobTarget() instanceof Player player) {
      this.launchMissile(mob, player);
    }

    return super.onUpdate(mob);
  }

  public void launchMissile(LivingEntity mob, Player player) {
    long time = System.currentTimeMillis();
    if (time - lastUsedTime <= COOLDOWN_MILLISECONDS) return;

    Vec3 direction = player.getPosition(0.5f).subtract(mob.position()).normalize();
    SmallFireball entity = new SmallFireball(mob.level(), mob, direction.normalize());
    entity.setPos(entity.getX(), mob.getY((double)0.5F) + (double)0.5F, entity.getZ());
    mob.level().addFreshEntity(entity);
    this.lastUsedTime = time;
  }


}
