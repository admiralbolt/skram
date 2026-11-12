package com.avi.skram.mods.infernal_mobs;

// InfernalMobs doesn't have any event system or "nice" API, so we take matters into our own hands.

import atomicstryker.infernalmobs.common.InfernalMobsConfig;
import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;
import atomicstryker.infernalmobs.common.mods.MM_Ender;
import com.avi.skram.Skram;
import com.avi.skram.mods.infernal_mobs.modifiers.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@EventBusSubscriber(modid = Skram.MODID)
public class InfernalMobsInjector {

  public static Set<Class<?>> EXCLUDE = Set.of(
    // This can cause crashes apparently since it calls hurt() inside LivingDamageEvent.Pre.
    MM_Ender.class
  );

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void injectInfernal(ServerStartedEvent event) {
    // The internal list of modifiers is set up as a result of this event.
    // We subscribe to the same event at a lower priority to modify the list after the fact.
    // This is tracked within a private list:
    //    private ArrayList<Class<? extends MobModifier>> mobMods = null;
    // This is attached to a static singleton instance:
    //   private static InfernalMobsCore instance;
    InfernalMobsCore instance = InfernalMobsCore.instance();
    try {
      Field listField = InfernalMobsCore.class.getDeclaredField("mobMods");
      listField.setAccessible(true);
      ArrayList<Class<? extends MobModifier>> mobMods = (ArrayList<Class<? extends MobModifier>>) listField.get(instance);

      // Now we go to town. Add our mob mods.
      mobMods.add(MobModifierBarraging.class);
      mobMods.add(MobModifierDeathThroes.class);
      mobMods.add(MobModifierInfested.class);
      mobMods.add(MobModifierHexing.class);
      mobMods.add(MobModifierMighty.class);
      mobMods.add(MobModifierUnholyPriest.class);

      // The default config values are also setup in the same place, via the loadConfig(Level world) method.
      // There's probably a way of doing this via a config file, but I'm hella lazy.
      Field configField = InfernalMobsCore.class.getDeclaredField("config");
      configField.setAccessible(true);
      InfernalMobsConfig config = (InfernalMobsConfig) configField.get(instance);

      // Amusingly rarities are ints. Setting a rarity of 15 means a roughly 1 / 15 chance.
      // Defaults are 15, 7, 7.
      config.setEliteRarity(9);
      config.setUltraRarity(6);
      config.setInfernoRarity(5);
      config.setMaxDamage(20);

      // Need to reset the enabled mods config since we've updated the list of mods.
      Map<String, Boolean> modsEnabled = new HashMap<>();
      for (Class<?> c : mobMods) {
        if (EXCLUDE.contains(c)) continue;

        modsEnabled.put(c.getSimpleName(), true);
      }
      config.setModsEnabled(modsEnabled);

    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("UH OH SPAGHET => " + e);
    }
  }

}
