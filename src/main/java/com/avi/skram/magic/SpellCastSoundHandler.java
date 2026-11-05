package com.avi.skram.magic;

import com.avi.skram.audio.DynamicSoundInstance;
import com.avi.skram.audio.SkramSounds;
import com.avi.skram.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class SpellCastSoundHandler {

  public static int START_CAST_TICKS = 16;
  public static float START_CAST_BASE_VOLUME = 0.35f;
  public static float START_CAST_END_VOLUME = 0.25f;

  public Player player;

  public boolean isCasting = false;
  public int timer = 0;
  public boolean shouldLoop = false;

  public DynamicSoundInstance startCast;
  public DynamicSoundInstance castLoop;

  public SpellCastSoundHandler(Player player) {
    this.player = player;
  }

  public void startCasting(int totalTicks) {
    this.isCasting = true;
    this.timer = START_CAST_TICKS;
    this.startCast = new DynamicSoundInstance(SkramSounds.SPELL_CAST_START.value(), SoundSource.PLAYERS, this.player, false, START_CAST_BASE_VOLUME, 1.0f);
    this.startCast.isPlaying = true;
    Minecraft.getInstance().getSoundManager().play(this.startCast);
  }

  public void stopCasting() {
    this.isCasting = false;
    this.timer = 0;
    this.shouldLoop = false;
    if (this.startCast != null) {
      this.startCast.isPlaying = false;
    }
    if (this.castLoop != null) {
      this.castLoop.isPlaying = false;
    }
  }

  public void tick() {
    if (!this.isCasting) return;

    if (this.startCast != null && this.timer > 0) {
      this.startCast.setVolume((float) MathUtils.linearRescale(((double) this.timer / START_CAST_TICKS), 0, START_CAST_TICKS, START_CAST_END_VOLUME, START_CAST_BASE_VOLUME));
    }

    if (!this.shouldLoop) {
      this.timer--;
      if (this.timer <= 0) {
        this.shouldLoop = true;
        this.castLoop = new DynamicSoundInstance(SkramSounds.SPELL_CAST_LOOP.value(), SoundSource.PLAYERS, this.player, true, START_CAST_END_VOLUME, 1.1f);
        this.castLoop.isPlaying = true;
        Minecraft.getInstance().getSoundManager().play(this.castLoop);
      }
    }
  }

}
