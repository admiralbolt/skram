package com.avi.skram.magic.spells;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Spells {

  public static Spell CONJURE_SUPPLIES = new SpellConjureSupplies();
  public static Spell FEATHER_FALL = new SpellFeatherFall();
  public static Spell ICARIAN_FLIGHT = new SpellIcarianFlight();
  public static Spell MINOR_HEAL = new SpellMinorHeal();
  public static Spell MUSCLE_BURST = new SpellMuscleBurst();

  public static Map<String, Spell> SPELLS_BY_NAME = ImmutableMap.<String, Spell>builder()
    .put(SpellConjureSupplies.NAME, CONJURE_SUPPLIES)
    .put(SpellFeatherFall.NAME, FEATHER_FALL)
    .put(SpellIcarianFlight.NAME, ICARIAN_FLIGHT)
    .put(SpellMinorHeal.NAME, MINOR_HEAL)
    .put(SpellMuscleBurst.NAME, MUSCLE_BURST)
    .build();

}
