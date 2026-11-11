package com.avi.skram.item;

import com.avi.skram.Skram;
import com.avi.skram.audio.SkramSounds;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SkramItems {

  // All of these are icons used for UI stuff, and not real items.
  public static DeferredItem<Item> AWESOME_SNAKE;
  public static DeferredItem<Item> BADLANDS;
  public static DeferredItem<Item> BAMBOO_JUNGLE;
  public static DeferredItem<Item> BASALT_DELTAS;
  public static DeferredItem<Item> BEACH;
  public static DeferredItem<Item> BIRCH_FOREST;
  public static DeferredItem<Item> CHERRY_GROVE;
  public static DeferredItem<Item> COLD_OCEAN;
  public static DeferredItem<Item> CRIMSON_FOREST;
  public static DeferredItem<Item> DAPPLED_FOREST;
  public static DeferredItem<Item> DARK_FOREST;
  public static DeferredItem<Item> DEEP_COLD_OCEAN;
  public static DeferredItem<Item> DEEP_DARK;
  public static DeferredItem<Item> DEEP_FROZEN_OCEAN;
  public static DeferredItem<Item> DEEP_LUKEWARM_OCEAN;
  public static DeferredItem<Item> DEEP_OCEAN;
  public static DeferredItem<Item> DESERT;
  public static DeferredItem<Item> DRIPSTONE_CAVES;
  public static DeferredItem<Item> END_BARRENS;
  public static DeferredItem<Item> END_HIGHLANDS;
  public static DeferredItem<Item> END_MIDLANDS;
  public static DeferredItem<Item> ERODED_BADLANDS;
  public static DeferredItem<Item> FLOWER_FOREST;
  public static DeferredItem<Item> FOREST;
  public static DeferredItem<Item> FROZEN_OCEAN;
  public static DeferredItem<Item> FROZEN_PEAKS;
  public static DeferredItem<Item> FROZEN_RIVER;
  public static DeferredItem<Item> GROVE;
  public static DeferredItem<Item> ICE_SPIKES;
  public static DeferredItem<Item> JAGGED_PEAKS;
  public static DeferredItem<Item> JUNGLE;
  public static DeferredItem<Item> LUKEWARM_OCEAN;
  public static DeferredItem<Item> LUSH_CAVES;
  public static DeferredItem<Item> MANGROVE_SWAMP;
  public static DeferredItem<Item> MEADOW;
  public static DeferredItem<Item> MUSHROOM_FIELDS;
  public static DeferredItem<Item> NETHER_WASTES;
  public static DeferredItem<Item> OCEAN;
  public static DeferredItem<Item> OLD_GROWTH_BIRCH_FOREST;
  public static DeferredItem<Item> OLD_GROWTH_PINE_TAIGA;
  public static DeferredItem<Item> OLD_GROWTH_SPRUCE_TAIGA;
  public static DeferredItem<Item> PALE_GARDEN;
  public static DeferredItem<Item> PLAINS;
  public static DeferredItem<Item> RIVER;
  public static DeferredItem<Item> SAVANNA;
  public static DeferredItem<Item> SAVANNA_PLATEAU;
  public static DeferredItem<Item> SMALL_END_ISLANDS;
  public static DeferredItem<Item> SNOWY_BEACH;
  public static DeferredItem<Item> SNOWY_PLAINS;
  public static DeferredItem<Item> SNOWY_SLOPES;
  public static DeferredItem<Item> SNOWY_TAIGA;
  public static DeferredItem<Item> SOUL_SAND_VALLEY;
  public static DeferredItem<Item> SPARSE_JUNGLE;
  public static DeferredItem<Item> STONY_PEAKS;
  public static DeferredItem<Item> STONY_SHORE;
  public static DeferredItem<Item> SULFUR_CAVES;
  public static DeferredItem<Item> SUNFLOWER_PLAINS;
  public static DeferredItem<Item> SWAMP;
  public static DeferredItem<Item> TAIGA;
  public static DeferredItem<Item> THE_END;
  public static DeferredItem<Item> THE_VOID;
  public static DeferredItem<Item> WARM_OCEAN;
  public static DeferredItem<Item> WARPED_FOREST;
  public static DeferredItem<Item> WINDSWEPT_FOREST;
  public static DeferredItem<Item> WINDSWEPT_GRAVELLY_HILLS;
  public static DeferredItem<Item> WINDSWEPT_HILLS;
  public static DeferredItem<Item> WINDSWEPT_SAVANNA;
  public static DeferredItem<Item> WOODED_BADLANDS;

  public static final Supplier<DataComponentType<SpellNameDataComponent>> SPELL_NAME_DATA_COMPONENT = Skram.DATA_COMPONENTS.registerComponentType(
    "spell_name", builder -> builder.persistent(SpellNameDataComponent.CODEC)
  );

  public static DeferredItem<Item> SPELL_SCROLL;

  // Music dics.
  public static DeferredItem<Item> MUSIC_DISC_ARCTURIA;
  public static DeferredItem<Item> MUSIC_DISC_ABANDONED_MINE_BATTLE;
  public static DeferredItem<Item> MUSIC_DISC_ABANDONED_RESEARCH_BATTLE;
  public static DeferredItem<Item> MUSIC_DISC_BELLATRIS;
  public static DeferredItem<Item> MUSIC_DISC_CASTORIA;
  public static DeferredItem<Item> MUSIC_DISC_DENNEBURG;
  public static DeferredItem<Item> MUSIC_DISC_ELTANIN;
  public static DeferredItem<Item> MUSIC_DISC_FORAX_SHAMBLER_BATTLE;
  public static DeferredItem<Item> MUSIC_DISC_FORAX_WOOD_GOLEM_FIGHT;
  public static DeferredItem<Item> MUSIC_DISC_FORAX;
  public static DeferredItem<Item> MUSIC_DISC_GACRUXIA_HAUNTED_HOUSE_BOSS;
  public static DeferredItem<Item> MUSIC_DISC_GACRUXIA;
  public static DeferredItem<Item> MUSIC_DISC_GRAND_TREE;
  public static DeferredItem<Item> MUSIC_DISC_MISC_BATTLE1;
  public static DeferredItem<Item> MUSIC_DISC_MISC_BATTLE3;
  public static DeferredItem<Item> MUSIC_DISC_REMEMBERANCE;
  public static DeferredItem<Item> MUSIC_DISC_SECRET_LAB_BATTLE;
  public static DeferredItem<Item> MUSIC_DISC_SEWER_BATTLE;
  public static DeferredItem<Item> MUSIC_DISC_SHROUDED_DEN_BATTLE;
  public static DeferredItem<Item> MUSIC_DISC_SHROUDED_DEN_BOSS;
  public static DeferredItem<Item> MUSIC_DISC_SUNKEN_TEMPLE;
  public static DeferredItem<Item> MUSIC_DISC_WATERFALL_CAVE_BOSS_FIGHT;

  public static List<DeferredItem<Item>> musicDiscs() {
    return List.of(
      MUSIC_DISC_ARCTURIA,
      MUSIC_DISC_ABANDONED_MINE_BATTLE,
      MUSIC_DISC_ABANDONED_RESEARCH_BATTLE,
      MUSIC_DISC_BELLATRIS,
      MUSIC_DISC_CASTORIA,
      MUSIC_DISC_DENNEBURG,
      MUSIC_DISC_ELTANIN,
      MUSIC_DISC_FORAX_SHAMBLER_BATTLE,
      MUSIC_DISC_FORAX_WOOD_GOLEM_FIGHT,
      MUSIC_DISC_FORAX,
      MUSIC_DISC_GACRUXIA_HAUNTED_HOUSE_BOSS,
      MUSIC_DISC_GACRUXIA,
      MUSIC_DISC_GRAND_TREE,
      MUSIC_DISC_MISC_BATTLE1,
      MUSIC_DISC_MISC_BATTLE3,
      MUSIC_DISC_REMEMBERANCE,
      MUSIC_DISC_SECRET_LAB_BATTLE,
      MUSIC_DISC_SEWER_BATTLE,
      MUSIC_DISC_SHROUDED_DEN_BATTLE,
      MUSIC_DISC_SHROUDED_DEN_BOSS,
      MUSIC_DISC_SUNKEN_TEMPLE,
      MUSIC_DISC_WATERFALL_CAVE_BOSS_FIGHT
    );
  }

  public static void register() {
    SPELL_SCROLL = Skram.ITEMS.register(SpellScroll.NAME,
      registryName -> new SpellScroll(
        new Item.Properties()
          .setId(ResourceKey.create(Registries.ITEM, registryName))
          .stacksTo(1)
          .durability(1)
          .rarity(Rarity.EPIC)
      )
    );

    MUSIC_DISC_ARCTURIA = registerMusicDisc("music_disc_arcturia", SkramSounds.ARCTURIA_SONG);
    MUSIC_DISC_ABANDONED_MINE_BATTLE = registerMusicDisc("music_disc_abandoned_mine_battle", SkramSounds.ABANDONED_MINE_BATTLE_SONG);
    MUSIC_DISC_ABANDONED_RESEARCH_BATTLE = registerMusicDisc("music_disc_abandoned_research_battle", SkramSounds.ABANDONED_RESEARCH_BATTLE_SONG);
    MUSIC_DISC_BELLATRIS = registerMusicDisc("music_disc_bellatris", SkramSounds.BELLATRIS_SONG);
    MUSIC_DISC_CASTORIA = registerMusicDisc("music_disc_castoria", SkramSounds.CASTORIA_SONG);
    MUSIC_DISC_DENNEBURG = registerMusicDisc("music_disc_denneburg", SkramSounds.DENNEBURG_SONG);
    MUSIC_DISC_ELTANIN = registerMusicDisc("music_disc_eltanin", SkramSounds.ELTANIN_SONG);
    MUSIC_DISC_FORAX_SHAMBLER_BATTLE = registerMusicDisc("music_disc_forax_shambler_battle", SkramSounds.FORAX_SHAMBLER_BATTLE_SONG);
    MUSIC_DISC_FORAX_WOOD_GOLEM_FIGHT = registerMusicDisc("music_disc_forax_wood_golem_fight", SkramSounds.FORAX_WOOD_GOLEM_FIGHT_SONG);
    MUSIC_DISC_FORAX = registerMusicDisc("music_disc_forax", SkramSounds.FORAX_SONG);
    MUSIC_DISC_GACRUXIA_HAUNTED_HOUSE_BOSS = registerMusicDisc("music_disc_gacruxia_haunted_house_boss", SkramSounds.GACRUXIA_HAUNTED_HOUSE_BOSS_SONG);
    MUSIC_DISC_GACRUXIA = registerMusicDisc("music_disc_gacruxia", SkramSounds.GACRUXIA_SONG);
    MUSIC_DISC_GRAND_TREE = registerMusicDisc("music_disc_grand_tree", SkramSounds.GRAND_TREE_SONG);
    MUSIC_DISC_MISC_BATTLE1 = registerMusicDisc("music_disc_misc_battle1", SkramSounds.MISC_BATTLE1_SONG);
    MUSIC_DISC_MISC_BATTLE3 = registerMusicDisc("music_disc_misc_battle3", SkramSounds.MISC_BATTLE3_SONG);
    MUSIC_DISC_REMEMBERANCE = registerMusicDisc("music_disc_rememberance", SkramSounds.REMEMBERANCE_SONG);
    MUSIC_DISC_SECRET_LAB_BATTLE = registerMusicDisc("music_disc_secret_lab_battle", SkramSounds.SECRET_LAB_BATTLE_SONG);
    MUSIC_DISC_SEWER_BATTLE = registerMusicDisc("music_disc_sewer_battle", SkramSounds.SEWER_BATTLE_SONG);
    MUSIC_DISC_SHROUDED_DEN_BATTLE = registerMusicDisc("music_disc_shrouded_den_battle", SkramSounds.SHROUDED_DEN_BATTLE_SONG);
    MUSIC_DISC_SHROUDED_DEN_BOSS = registerMusicDisc("music_disc_shrouded_den_boss", SkramSounds.SHROUDED_DEN_BOSS_SONG);
    MUSIC_DISC_SUNKEN_TEMPLE = registerMusicDisc("music_disc_sunken_temple", SkramSounds.SUNKEN_TEMPLE_SONG);
    MUSIC_DISC_WATERFALL_CAVE_BOSS_FIGHT = registerMusicDisc("music_disc_waterfall_cave_boss_fight", SkramSounds.WATERFALL_CAVE_BOSS_FIGHT_SONG);

    // All of these registrations are not "real" items, they are just icons used for the UI.
    AWESOME_SNAKE = Skram.ITEMS.registerSimpleItem("awesome_snake");
    BADLANDS = Skram.ITEMS.registerSimpleItem("badlands");
    BAMBOO_JUNGLE = Skram.ITEMS.registerSimpleItem("bamboo_jungle");
    BASALT_DELTAS = Skram.ITEMS.registerSimpleItem("basalt_deltas");
    BEACH = Skram.ITEMS.registerSimpleItem("beach");
    BIRCH_FOREST = Skram.ITEMS.registerSimpleItem("birch_forest");
    CHERRY_GROVE = Skram.ITEMS.registerSimpleItem("cherry_grove");
    COLD_OCEAN = Skram.ITEMS.registerSimpleItem("cold_ocean");
    CRIMSON_FOREST = Skram.ITEMS.registerSimpleItem("crimson_forest");
    DAPPLED_FOREST = Skram.ITEMS.registerSimpleItem("dappled_forest");
    DARK_FOREST = Skram.ITEMS.registerSimpleItem("dark_forest");
    DEEP_COLD_OCEAN = Skram.ITEMS.registerSimpleItem("deep_cold_ocean");
    DEEP_DARK = Skram.ITEMS.registerSimpleItem("deep_dark");
    DEEP_FROZEN_OCEAN = Skram.ITEMS.registerSimpleItem("deep_frozen_ocean");
    DEEP_LUKEWARM_OCEAN = Skram.ITEMS.registerSimpleItem("deep_lukewarm_ocean");
    DEEP_OCEAN = Skram.ITEMS.registerSimpleItem("deep_ocean");
    DESERT = Skram.ITEMS.registerSimpleItem("desert");
    DRIPSTONE_CAVES = Skram.ITEMS.registerSimpleItem("dripstone_caves");
    END_BARRENS = Skram.ITEMS.registerSimpleItem("end_barrens");
    END_HIGHLANDS = Skram.ITEMS.registerSimpleItem("end_highlands");
    END_MIDLANDS = Skram.ITEMS.registerSimpleItem("end_midlands");
    ERODED_BADLANDS = Skram.ITEMS.registerSimpleItem("eroded_badlands");
    FLOWER_FOREST = Skram.ITEMS.registerSimpleItem("flower_forest");
    FOREST = Skram.ITEMS.registerSimpleItem("forest");
    FROZEN_OCEAN = Skram.ITEMS.registerSimpleItem("frozen_ocean");
    FROZEN_PEAKS = Skram.ITEMS.registerSimpleItem("frozen_peaks");
    FROZEN_RIVER = Skram.ITEMS.registerSimpleItem("frozen_river");
    GROVE = Skram.ITEMS.registerSimpleItem("grove");
    ICE_SPIKES = Skram.ITEMS.registerSimpleItem("ice_spikes");
    JAGGED_PEAKS = Skram.ITEMS.registerSimpleItem("jagged_peaks");
    JUNGLE = Skram.ITEMS.registerSimpleItem("jungle");
    LUKEWARM_OCEAN = Skram.ITEMS.registerSimpleItem("lukewarm_ocean");
    LUSH_CAVES = Skram.ITEMS.registerSimpleItem("lush_caves");
    MANGROVE_SWAMP = Skram.ITEMS.registerSimpleItem("mangrove_swamp");
    MEADOW = Skram.ITEMS.registerSimpleItem("meadow");
    MUSHROOM_FIELDS = Skram.ITEMS.registerSimpleItem("mushroom_fields");
    NETHER_WASTES = Skram.ITEMS.registerSimpleItem("nether_wastes");
    OCEAN = Skram.ITEMS.registerSimpleItem("ocean");
    OLD_GROWTH_BIRCH_FOREST = Skram.ITEMS.registerSimpleItem("old_growth_birch_forest");
    OLD_GROWTH_PINE_TAIGA = Skram.ITEMS.registerSimpleItem("old_growth_pine_taiga");
    OLD_GROWTH_SPRUCE_TAIGA = Skram.ITEMS.registerSimpleItem("old_growth_spruce_taiga");
    PALE_GARDEN = Skram.ITEMS.registerSimpleItem("pale_garden");
    PLAINS = Skram.ITEMS.registerSimpleItem("plains");
    RIVER = Skram.ITEMS.registerSimpleItem("river");
    SAVANNA = Skram.ITEMS.registerSimpleItem("savanna");
    SAVANNA_PLATEAU = Skram.ITEMS.registerSimpleItem("savanna_plateau");
    SMALL_END_ISLANDS = Skram.ITEMS.registerSimpleItem("small_end_islands");
    SNOWY_BEACH = Skram.ITEMS.registerSimpleItem("snowy_beach");
    SNOWY_PLAINS = Skram.ITEMS.registerSimpleItem("snowy_plains");
    SNOWY_SLOPES = Skram.ITEMS.registerSimpleItem("snowy_slopes");
    SNOWY_TAIGA = Skram.ITEMS.registerSimpleItem("snowy_taiga");
    SOUL_SAND_VALLEY = Skram.ITEMS.registerSimpleItem("soul_sand_valley");
    SPARSE_JUNGLE = Skram.ITEMS.registerSimpleItem("sparse_jungle");
    STONY_PEAKS = Skram.ITEMS.registerSimpleItem("stony_peaks");
    STONY_SHORE = Skram.ITEMS.registerSimpleItem("stony_shore");
    SULFUR_CAVES = Skram.ITEMS.registerSimpleItem("sulfur_caves");
    SUNFLOWER_PLAINS = Skram.ITEMS.registerSimpleItem("sunflower_plains");
    SWAMP = Skram.ITEMS.registerSimpleItem("swamp");
    TAIGA = Skram.ITEMS.registerSimpleItem("taiga");
    THE_END = Skram.ITEMS.registerSimpleItem("the_end");
    THE_VOID = Skram.ITEMS.registerSimpleItem("the_void");
    WARM_OCEAN = Skram.ITEMS.registerSimpleItem("warm_ocean");
    WARPED_FOREST = Skram.ITEMS.registerSimpleItem("warped_forest");
    WINDSWEPT_FOREST = Skram.ITEMS.registerSimpleItem("windswept_forest");
    WINDSWEPT_GRAVELLY_HILLS = Skram.ITEMS.registerSimpleItem("windswept_gravelly_hills");
    WINDSWEPT_HILLS = Skram.ITEMS.registerSimpleItem("windswept_hills");
    WINDSWEPT_SAVANNA = Skram.ITEMS.registerSimpleItem("windswept_savanna");
    WOODED_BADLANDS = Skram.ITEMS.registerSimpleItem("wooded_badlands");
  }

  public static List<DeferredItem<Item>> ALL_RECORDS = new ArrayList<>();

  private static DeferredItem<Item> registerMusicDisc(String name, ResourceKey<JukeboxSong> song) {
    DeferredItem<Item> item = Skram.ITEMS.register(name, registryName -> new Item(
      new Item.Properties()
        .jukeboxPlayable(song)
        .rarity(Rarity.UNCOMMON)
        .stacksTo(1)
        .setId(ResourceKey.create(Registries.ITEM, registryName))
    ));
    ALL_RECORDS.add(item);
    return item;
  }

}
