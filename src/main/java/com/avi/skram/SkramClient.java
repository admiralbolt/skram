package com.avi.skram;

import com.avi.skram.client.screens.GigaChestScreen;
import com.avi.skram.client.screens.MegaChestScreen;
import com.avi.skram.inventory.SkramMenus;
import com.avi.skram.magic.SkramMagic;
import com.avi.skram.ui.SkramScreen;
import com.avi.skram.ui.SpellUI;
import com.avi.skram.ui.XPGainGui;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.util.Lazy;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Skram.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Skram.MODID, value = Dist.CLIENT)
public class SkramClient {

  public static final KeyMapping.Category SKRAM_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Skram.MODID, "skram"));
  public static final Lazy<KeyMapping> OPEN_SKILLS_UI = Lazy.of(() -> new KeyMapping("key.skram.open_skills_ui", InputConstants.Type.KEYBOARD, InputConstants.KEY_R, SKRAM_CATEGORY));
  public static final Lazy<KeyMapping> CAST_SPELL = Lazy.of(() -> new KeyMapping("key.skram.cast_spell", InputConstants.Type.KEYBOARD, InputConstants.KEY_C, SKRAM_CATEGORY));

  public static final Identifier CUSTOM_LAYER = Identifier.fromNamespaceAndPath(Skram.MODID, "asdf");

  public SkramClient(ModContainer container) {
    // Allows NeoForge to create a config screen for this mod's configs.
    // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
    // Do not forget to add translations for your config options to the en_us.json file.
    container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
  }

  @SubscribeEvent
  static void onClientSetup(FMLClientSetupEvent event) {
    // Some client setup code
    Skram.LOGGER.info("HELLO FROM CLIENT SETUP");
    Skram.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
  }

  @SubscribeEvent
  static void registerMenuScreens(RegisterMenuScreensEvent event) {
    event.register(SkramMenus.GIGA_CHEST.get(), GigaChestScreen::new);
    event.register(SkramMenus.MEGA_CHEST.get(), MegaChestScreen::new);
  }

  @SubscribeEvent
  public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
    event.register(OPEN_SKILLS_UI.get());
    event.register(CAST_SPELL.get());
  }


  @SubscribeEvent
  public static void ON_CLIENT_TICK(ClientTickEvent.Post event) {
    while (OPEN_SKILLS_UI.get().consumeClick()) {
      Minecraft mc = Minecraft.getInstance();
      if (!(mc.gui.screen() instanceof SkramScreen)) {
        Minecraft.getInstance().setScreenAndShow(new SkramScreen());
      }
    }

    while (CAST_SPELL.get().consumeClick()) {
      SkramMagic.castStart();
    }
  }

  @SubscribeEvent
  public static void REGISTER_GUI_LAYERS(RegisterGuiLayersEvent event) {
    event.registerAbove(
      VanillaGuiLayers.HOTBAR,
      CUSTOM_LAYER,
      XPGainGui.layer
    );

    event.registerAbove(
      VanillaGuiLayers.HOTBAR,
      SpellUI.SPELl_UI,
      SpellUI.layer
    );

  }

}
