package com.avi.skram.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class DynamicSoundInstance extends AbstractTickableSoundInstance {

  public boolean isPlaying = false;
  public Player player;

  public DynamicSoundInstance(SoundEvent soundEvent, SoundSource soundSource, Player player, boolean looping, float volume, float pitch) {
    super(soundEvent, soundSource, RandomSource.create());

    this.looping = looping;
    this.delay = 0;
    this.volume = volume;
    this.pitch = pitch;
    this.relative = false;
    this.x = player.getX();
    this.y = player.getY();
    this.z = player.getZ();
    this.player = player;
  }

  public void setVolume(float volume) {
    this.volume = volume;
  }

  public void setPitch(float pitch) {
    this.pitch = pitch;
  }

  @Override
  public void tick() {
    if (!this.isPlaying) {
      this.stop();
      return;
    }

    this.x = this.player.getX();
    this.y = this.player.getY();
    this.z = this.player.getZ();
  }

  @Override
  public boolean canPlaySound() {
    return true;
  }

  @Override
  public boolean canStartSilent() {
    return true;
  }


}
