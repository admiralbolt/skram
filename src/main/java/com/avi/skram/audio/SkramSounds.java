package com.avi.skram.audio;

import com.avi.skram.Skram;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SkramSounds {

  public static ResourceKey<JukeboxSong> createSong(String name) {
    return ResourceKey.create(Registries.JUKEBOX_SONG, Identifier.fromNamespaceAndPath(Skram.MODID, name));
  }

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


  // Jukebox!
  public static final Supplier<SoundEvent> ARCTURIA = SOUND_EVENTS.register("arcturia", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> ARCTURIA_SONG = createSong("arcturia");

  public static final Supplier<SoundEvent> ABANDONED_MINE_BATTLE = SOUND_EVENTS.register("abandoned_mine_battle", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> ABANDONED_MINE_BATTLE_SONG = createSong("abandoned_mine_battle");
  public static final Supplier<SoundEvent> ABANDONED_RESEARCH_BATTLE = SOUND_EVENTS.register("abandoned_research_battle", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> ABANDONED_RESEARCH_BATTLE_SONG = createSong("abandoned_research_battle");
  public static final Supplier<SoundEvent> BELLATRIS = SOUND_EVENTS.register("bellatris", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> BELLATRIS_SONG = createSong("bellatris");
  public static final Supplier<SoundEvent> CASTORIA = SOUND_EVENTS.register("castoria", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> CASTORIA_SONG = createSong("castoria");
  public static final Supplier<SoundEvent> DENNEBURG = SOUND_EVENTS.register("denneburg", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> DENNEBURG_SONG = createSong("denneburg");
  public static final Supplier<SoundEvent> ELTANIN = SOUND_EVENTS.register("eltanin", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> ELTANIN_SONG = createSong("eltanin");
  public static final Supplier<SoundEvent> FORAX_SHAMBLER_BATTLE = SOUND_EVENTS.register("forax_shambler_battle", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> FORAX_SHAMBLER_BATTLE_SONG = createSong("forax_shambler_battle");
  public static final Supplier<SoundEvent> FORAX_WOOD_GOLEM_FIGHT = SOUND_EVENTS.register("forax_wood_golem_fight", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> FORAX_WOOD_GOLEM_FIGHT_SONG = createSong("forax_wood_golem_fight");
  public static final Supplier<SoundEvent> FORAX = SOUND_EVENTS.register("forax", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> FORAX_SONG = createSong("forax");
  public static final Supplier<SoundEvent> GACRUXIA_HAUNTED_HOUSE_BOSS = SOUND_EVENTS.register("gacruxia_haunted_house_boss", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> GACRUXIA_HAUNTED_HOUSE_BOSS_SONG = createSong("gacruxia_haunted_house_boss");
  public static final Supplier<SoundEvent> GACRUXIA = SOUND_EVENTS.register("gacruxia", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> GACRUXIA_SONG = createSong("gacruxia");
  public static final Supplier<SoundEvent> GRAND_TREE = SOUND_EVENTS.register("grand_tree", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> GRAND_TREE_SONG = createSong("grand_tree");
  public static final Supplier<SoundEvent> MISC_BATTLE1 = SOUND_EVENTS.register("misc_battle1", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> MISC_BATTLE1_SONG = createSong("misc_battle1");
  public static final Supplier<SoundEvent> MISC_BATTLE3 = SOUND_EVENTS.register("misc_battle3", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> MISC_BATTLE3_SONG = createSong("misc_battle3");
  public static final Supplier<SoundEvent> REMEMBERANCE = SOUND_EVENTS.register("rememberance", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> REMEMBERANCE_SONG = createSong("rememberance");
  public static final Supplier<SoundEvent> SECRET_LAB_BATTLE = SOUND_EVENTS.register("secret_lab_battle", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> SECRET_LAB_BATTLE_SONG = createSong("secret_lab_battle");
  public static final Supplier<SoundEvent> SEWER_BATTLE = SOUND_EVENTS.register("sewer_battle", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> SEWER_BATTLE_SONG = createSong("sewer_battle");
  public static final Supplier<SoundEvent> SHROUDED_DEN_BATTLE = SOUND_EVENTS.register("shrouded_den_battle", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> SHROUDED_DEN_BATTLE_SONG = createSong("shrouded_den_battle");
  public static final Supplier<SoundEvent> SHROUDED_DEN_BOSS = SOUND_EVENTS.register("shrouded_den_boss", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> SHROUDED_DEN_BOSS_SONG = createSong("shrouded_den_boss");
  public static final Supplier<SoundEvent> SUNKEN_TEMPLE = SOUND_EVENTS.register("sunken_temple", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> SUNKEN_TEMPLE_SONG = createSong("sunken_temple");
  public static final Supplier<SoundEvent> WATERFALL_CAVE_BOSS_FIGHT = SOUND_EVENTS.register("waterfall_cave_boss_fight", SoundEvent::createVariableRangeEvent);
  public static final ResourceKey<JukeboxSong> WATERFALL_CAVE_BOSS_FIGHT_SONG = createSong("waterfall_cave_boss_fight");

}
