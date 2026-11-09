package com.avi.skram.magic.spells;

import com.avi.skram.network.SpellCastPacket;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MinecraftUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class SpellLightningStorm extends Spell {

  public static String NAME = "lightning_storm";
  public static int REQUIRED_MAGIC_LEVEL = 30;
  public static int COST = 2100;
  public static int CASTING_TICKS = 60;

  public static double MAX_DISTANCE = 60;
  public static double BASE_RADIUS = 2;

  public SpellLightningStorm() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, Holder.direct(SoundEvents.LIGHTNING_BOLT_THUNDER));
  }

  @Override
  public void sendCastPacket(Player player) {
    HitResult hitResult = MinecraftUtils.rayCast(player, MAX_DISTANCE);
    ClientPacketDistributor.sendToServer(new SpellCastPacket(this.name, (hitResult instanceof BlockHitResult blockHitResult) ? blockHitResult : null));
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    if (hitResult == null) return;

    SkillMagic magic = player.getData(Skills.MAGIC);
    int radius = (int) (BASE_RADIUS * (1 + magic.getSpellPower()));

    for (int i = -radius; i <= radius; ++i) {
      for (int j = -radius; j <= radius; ++j) {
        BlockPos targetPos = hitResult.getBlockPos().offset(i, 0, j);

        LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(player.level(), EntitySpawnReason.EVENT);
        if (bolt != null) {
          bolt.snapTo(Vec3.atBottomCenterOf(targetPos));
          player.level().addFreshEntity(bolt);
        }
      }
    }
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Call down lightning in a %d block radius around the target location.", (int) (BASE_RADIUS * (1 + magic.getSpellPower())));
  }
}
