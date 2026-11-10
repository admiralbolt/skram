package com.avi.skram.mods.infernal_mobs;

// InfernalMobs doesn't have any event system or "nice" API, so we take matters into our own hands.

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;
import com.avi.skram.Skram;
import com.avi.skram.mods.infernal_mobs.modifiers.MobModifierMighty;
import com.avi.skram.mods.infernal_mobs.modifiers.MobModifierHexing;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;


@EventBusSubscriber(modid = Skram.MODID)
public class InfernalMobsInjector {



  @SubscribeEvent(priority = EventPriority.LOW)
  public static void injectModifiers(ServerStartedEvent event) {
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

      // Now we go to town.
      mobMods.add(MobModifierMighty.class);
      mobMods.add(MobModifierHexing.class);
      mobMods.add(MobModifierMighty.class);

    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("UH OH SPAGHET => " + e);
    }
  }

}
