package com.avi.skram;

import com.avi.skram.client.SkramMagicClient;
import com.avi.skram.client.screens.GigaChestScreen;
import com.avi.skram.client.screens.MegaChestScreen;
import com.avi.skram.inventory.SkramMenus;
import com.avi.skram.skills.SkillAthletics;
import com.avi.skram.skills.Skills;
import com.avi.skram.ui.SkramScreen;
import com.avi.skram.ui.SpellUI;
import com.avi.skram.ui.XPGainGui;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

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
      SkramMagicClient.castStart();
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

  public static boolean isLocalPlayer(Player player) {
    return player.level().isClientSide() && player == Minecraft.getInstance().player;
  }

  public static void handleAthleticsTick(PlayerTickEvent.Pre event) {
    Player player = event.getEntity();
    if (!SkramClient.isLocalPlayer(player)) return;

    Vec3 velocity = player.getDeltaMovement();
    // If we aren't moving at all, don't give xp.
    if (velocity.x == 0 && velocity.z == 0) return;

    SkillAthletics athletics = player.getData(Skills.ATHLETICS);

    // Sprinting is worth the most.
    if (player.isSprinting()) {
      athletics.addXp(player, 4, true);
      return;
    }

    // player.isSwimming() checks for sprinting in the water.
    if (player.isSwimming()) {
      athletics.addXp(player, 3, true);
      return;
    }

    if (player.isInWater()) {
      athletics.addXp(player, 1, true);
      return;
    }

    // We must be grounded, and not on rails.
    if (!player.onGround() || player.isOnRails()) return;

    athletics.addXp(player, 1, true);
  }

  @SubscribeEvent
  public static void ON_TICK(PlayerTickEvent.Pre event) {
    SkramClient.handleAthleticsTick(event);
    SkramMagicClient.handleTick(event);
  }

  @SubscribeEvent
  public static void ON_TICK_POST(PlayerTickEvent.Post event) {
    SkramMagicClient.handleTickPost(event);
  }
}
