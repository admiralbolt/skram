package com.avi.skram.magic.spells;

import com.avi.skram.utils.MathUtils;
import com.google.common.collect.ImmutableMap;

import java.util.List;
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

  public static List<String> ALL_SPELLS = SPELLS_BY_NAME.keySet().stream().sorted().toList();

  public static String getRandomSpellName() {
    return ALL_SPELLS.get(MathUtils.RANDOM.nextInt(ALL_SPELLS.size()));
  }

}
