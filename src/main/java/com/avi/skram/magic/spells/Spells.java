package com.avi.skram.magic.spells;

import com.avi.skram.Skram;
import com.avi.skram.utils.MathUtils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public class Spells {

  public static Identifier UNKNOWN_ICON = Identifier.fromNamespaceAndPath(Skram.MODID, "textures/spells/unknown.png");

  public static Spell BLUE_SKY = new SpellBlueSky();
  public static Spell BURNING_RUSH = new SpellBurningRush();
  public static Spell CONJURE_SUPPLIES = new SpellConjureSupplies();
  public static Spell CREATE_LAVA = new SpellCreateLava();
  public static Spell CREATE_WATER = new SpellCreateWater();
  public static Spell DECOY = new SpellDecoy();
  public static Spell DIMENSION_DOOR = new SpellDimensionDoor();
  public static Spell DISENCHANT = new SpellDisenchant();
  public static Spell ERUPTION = new SpellEruption();
  public static Spell FAERIE_FIRE = new SpellFaerieFire();
  public static Spell FEATHER_FALL = new SpellFeatherFall();
  public static Spell FERTILE_SOIL = new SpellFertileSoil();
  public static Spell FIRE_BOLT = new SpellFireBolt();
  public static Spell HEX = new SpellHex();
  public static Spell HOME_TELEPORT = new SpellHomeTeleport();
  public static Spell ICARIAN_FLIGHT = new SpellIcarianFlight();
  public static Spell ICE_SPIKE = new SpellIceSpike();
  public static Spell LIGHTNING_STORM = new SpellLightningStorm();
  public static Spell MINOR_HEAL = new SpellMinorHeal();
  public static Spell MUSCLE_BURST = new SpellMuscleBurst();
  public static Spell PLANAR_SHIFT = new SpellPlanarShift();
  public static Spell PURIFY = new SpellPurify();
  public static Spell RALLY = new SpellRally();
  public static Spell SAP = new SpellSap();

  public static Map<String, Spell> SPELLS_BY_NAME = ImmutableMap.<String, Spell>builder()
    .put(SpellBlueSky.NAME, BLUE_SKY)
    .put(SpellBurningRush.NAME, BURNING_RUSH)
    .put(SpellConjureSupplies.NAME, CONJURE_SUPPLIES)
    .put(SpellCreateLava.NAME, CREATE_LAVA)
    .put(SpellCreateWater.NAME, CREATE_WATER)
    .put(SpellDecoy.NAME, DECOY)
    .put(SpellDimensionDoor.NAME, DIMENSION_DOOR)
    .put(SpellDisenchant.NAME, DISENCHANT)
    .put(SpellEruption.NAME, ERUPTION)
    .put(SpellFaerieFire.NAME, FAERIE_FIRE)
    .put(SpellFeatherFall.NAME, FEATHER_FALL)
    .put(SpellFertileSoil.NAME, FERTILE_SOIL)
    .put(SpellFireBolt.NAME, FIRE_BOLT)
    .put(SpellHex.NAME, HEX)
    .put(SpellHomeTeleport.NAME, HOME_TELEPORT)
    .put(SpellIcarianFlight.NAME, ICARIAN_FLIGHT)
    .put(SpellIceSpike.NAME, ICE_SPIKE)
    .put(SpellLightningStorm.NAME, LIGHTNING_STORM)
    .put(SpellMinorHeal.NAME, MINOR_HEAL)
    .put(SpellMuscleBurst.NAME, MUSCLE_BURST)
    .put(SpellPlanarShift.NAME, PLANAR_SHIFT)
    .put(SpellPurify.NAME, PURIFY)
    .put(SpellRally.NAME, RALLY)
    .put(SpellSap.NAME, SAP)
    .build();

  public static List<String> ALL_SPELLS = SPELLS_BY_NAME.keySet().stream().sorted().toList();

  public static String getRandomSpellName() {
    return ALL_SPELLS.get(MathUtils.RANDOM.nextInt(ALL_SPELLS.size()));
  }

}
