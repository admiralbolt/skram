package com.avi.skram.magic.spells;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Spells {

  public static Spell MINOR_HEAL = new SpellMinorHeal();

  public static Map<String, Spell> SPELLS_BY_NAME = ImmutableMap.<String, Spell>builder()
    .put(SpellMinorHeal.NAME, MINOR_HEAL)
    .build();

}
