package com.avi.skram.misc;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.item.SkillNameDataComponent;
import com.avi.skram.item.SkramItems;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.concurrent.ThreadLocalRandom;

// You get a lamp! You get a lamp!
public class GenieEvent {

  public static final int BASE_TICK_TIME = 20 * 60 * 30;

  public static int tickCounter = BASE_TICK_TIME;

  public static void handleTick(ServerTickEvent.Post event) {
    tickCounter--;
    if (tickCounter > 0) return;

    // Get all players on the server, and give them a random lamp!
    event.getServer().getPlayerList().getPlayers().forEach(player -> {
      ItemStack stack = new ItemStack(SkramItems.ANCIENT_LAMP.get());
      stack.set(SkramItems.SKILL_NAME_DATA_COMPONENT, new SkillNameDataComponent(Skills.getRandomSkill()));
      ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
      player.level().addFreshEntity(entity);
      player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SkramSounds.RANDOM_TREASURE.value(), SoundSource.PLAYERS, 0.6f, 1.0f);
    });

    tickCounter = BASE_TICK_TIME + (int) (20 * 60 * 60 * (MathUtils.RANDOM.nextDouble() + MathUtils.RANDOM.nextDouble()) / 2);
  }


}
