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
  public static float LOOP_CAST_END_VOLUME = 0.12f;

  public Player player;

  public boolean isCasting = false;
  public boolean shouldLoop = false;

  public int startCastTimer = 0;
  public int totalLoopTime = 0;
  public int loopingTimer = 0;

  public static int FAILED_CAST_DELAY_TICKS = 20;
  public int failedCastTimer = 0;


  public DynamicSoundInstance startCast;
  public DynamicSoundInstance castLoop;

  public SpellCastSoundHandler(Player player) {
    this.player = player;
  }

  public void failedCasting() {
    if (failedCastTimer > 0) return;

    this.failedCastTimer = FAILED_CAST_DELAY_TICKS;
    this.player.playSound(SkramSounds.SPELL_CAST_FAILED.value(), 0.2F, 1.0F);
  }

  public void startCasting(int totalTicks) {
    if (this.isCasting) return;

    this.isCasting = true;
    this.startCastTimer = START_CAST_TICKS;
    this.totalLoopTime = totalTicks - START_CAST_TICKS;
    this.loopingTimer = totalTicks - START_CAST_TICKS;
    this.startCast = new DynamicSoundInstance(SkramSounds.SPELL_CAST_START.value(), SoundSource.PLAYERS, this.player, false, START_CAST_BASE_VOLUME, 1.0f);
    this.startCast.isPlaying = true;
    Minecraft.getInstance().getSoundManager().play(this.startCast);
  }

  public void stopCasting() {
    if (this.startCast == null || !this.startCast.isPlaying) return;
    if (this.castLoop == null || !this.castLoop.isPlaying) return;

    if (this.startCastTimer > 0 || this.loopingTimer > 1) {
      // We were interrupted!
      this.player.playSound(SkramSounds.SPELL_INTERRUPTED.value(), 0.4F, 1.0F);
    }

    this.isCasting = false;
    this.startCastTimer = 0;
    this.shouldLoop = false;
    if (this.startCast != null) {
      this.startCast.isPlaying = false;
    }
    if (this.castLoop != null) {
      this.castLoop.isPlaying = false;
    }
  }

  public void tick() {
    this.failedCastTimer--;

    if (!this.isCasting) return;

    if (this.startCast != null && this.startCastTimer > 0) {
      this.startCast.setVolume((float) MathUtils.linearRescale(((double) this.startCastTimer / START_CAST_TICKS), 0, START_CAST_TICKS, START_CAST_END_VOLUME, START_CAST_BASE_VOLUME));
    }

    if (this.shouldLoop && this.loopingTimer > 0) {
      this.castLoop.setVolume((float) MathUtils.linearRescale(((double) this.loopingTimer / this.totalLoopTime), 0, this.totalLoopTime, LOOP_CAST_END_VOLUME, START_CAST_END_VOLUME));
    }

    if (this.shouldLoop) {
      this.loopingTimer--;
    } else {
      this.startCastTimer--;
      if (this.startCastTimer <= 0) {
        this.shouldLoop = true;
        this.castLoop = new DynamicSoundInstance(SkramSounds.SPELL_CAST_LOOP.value(), SoundSource.PLAYERS, this.player, true, START_CAST_END_VOLUME, 1.0f);
        this.castLoop.isPlaying = true;
        Minecraft.getInstance().getSoundManager().play(this.castLoop);
      }
    }
  }

}
