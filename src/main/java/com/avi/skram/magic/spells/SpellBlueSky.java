package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class SpellBlueSky extends Spell {

  public static String NAME = "blue_sky";
  public static int REQUIRED_MAGIC_LEVEL = 6;
  public static int COST = 400;
  public static int CASTING_TICKS = 60;

  public static int CLEAR_WEATHER_TICKS = 20 * 60 * 20;

  public SpellBlueSky() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    MinecraftServer server = player.level().getServer();
    if (server == null) return;

    server.setWeatherParameters(CLEAR_WEATHER_TICKS, 0, false, false);
  }

  @Override
  public String getDescription(Player player) {
    return "Clear the weather.";
  }
}
