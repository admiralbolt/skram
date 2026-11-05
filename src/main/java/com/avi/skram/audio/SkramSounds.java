package com.avi.skram.audio;

import com.avi.skram.Skram;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SkramSounds {

  public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
    DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Skram.MODID);

  public static final Holder<SoundEvent> SPELL_CAST_START = SOUND_EVENTS.register(
    "spell_cast_start", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_LOOP = SOUND_EVENTS.register(
    "spell_cast_loop", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_HEAL = SOUND_EVENTS.register(
    "spell_heal", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_INTERRUPTED = SOUND_EVENTS.register(
    "spell_interrupted", SoundEvent::createVariableRangeEvent
  );

}
