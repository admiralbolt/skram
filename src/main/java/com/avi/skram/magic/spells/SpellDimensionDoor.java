package com.avi.skram.magic.spells;

import com.avi.skram.network.SpellCastPacket;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MinecraftUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Optional;

public class SpellDimensionDoor extends Spell {

  public static String NAME = "dimension_door";
  public static int REQUIRED_MAGIC_LEVEL = 16;
  public static int COST = 1300;
  public static int CASTING_TICKS = 20;

  public static double BASE_DISTANCE = 40;

  public SpellDimensionDoor() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, Holder.direct(SoundEvents.PLAYER_TELEPORT));
  }

  @Override
  public void sendCastPacket(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    HitResult hitResult = MinecraftUtils.rayCast(player, (1 + magic.getSpellPower()) * BASE_DISTANCE);

    ClientPacketDistributor.sendToServer(new SpellCastPacket(this.name, (hitResult instanceof BlockHitResult blockHitResult) ? Optional.of(blockHitResult) : Optional.empty()));
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    if (hitResult == null) return;

    Vec3 targetBlock = hitResult.getLocation();
    player.teleport(new TeleportTransition((ServerLevel) player.level(), targetBlock, Vec3.ZERO, 0.0F, 0.0F, Relative.ROTATION, TeleportTransition.DO_NOTHING));
    player.level().playSound(null, targetBlock.x, targetBlock.y, targetBlock.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Teleport to the targeted location. Can teleport up to %d blocks.", (int) (BASE_DISTANCE * (1 + magic.getSpellPower())));
  }
}
