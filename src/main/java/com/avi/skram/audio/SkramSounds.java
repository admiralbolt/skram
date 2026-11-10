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

  public static final Holder<SoundEvent> SPELL_CAST_FAILED = SOUND_EVENTS.register(
    "spell_cast_failed", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_FIRE = SOUND_EVENTS.register(
    "spell_cast_fire", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_GENERIC1 = SOUND_EVENTS.register(
    "spell_cast_generic1", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_GENERIC2 = SOUND_EVENTS.register(
    "spell_cast_generic2", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_GENERIC3 = SOUND_EVENTS.register(
    "spell_cast_generic3", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_GENERIC4 = SOUND_EVENTS.register(
    "spell_cast_generic4", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_GENERIC5 = SOUND_EVENTS.register(
    "spell_cast_generic5", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_GENERIC6 = SOUND_EVENTS.register(
    "spell_cast_generic6", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_GENERIC7 = SOUND_EVENTS.register(
    "spell_cast_generic7", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_OMINOUS = SOUND_EVENTS.register(
    "spell_cast_ominous", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_CAST_SLAM = SOUND_EVENTS.register(
    "spell_cast_slam", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_HEAL = SOUND_EVENTS.register(
    "spell_heal", SoundEvent::createVariableRangeEvent
  );

  public static final Holder<SoundEvent> SPELL_INTERRUPTED = SOUND_EVENTS.register(
    "spell_interrupted", SoundEvent::createVariableRangeEvent
  );

}
