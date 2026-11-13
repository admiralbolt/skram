package com.avi.skram;

import com.avi.skram.client.screens.GigaChestScreen;
import com.avi.skram.client.screens.MegaChestScreen;
import com.avi.skram.inventory.SkramMenus;
import com.avi.skram.magic.SkramMagic;
import com.avi.skram.magic.SpellCastSoundHandler;
import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.spells.Spell;
import com.avi.skram.magic.spells.Spells;
import com.avi.skram.network.CastingSyncServerPacket;
import com.avi.skram.ui.SkramScreen;
import com.avi.skram.ui.SpellUI;
import com.avi.skram.ui.XPGainGui;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
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
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
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
      castStart();
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

  public static void castStart() {
    Player player = Minecraft.getInstance().player;
    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    Spell spell = Spells.SPELLS_BY_NAME.get(castingInfo.equippedSpellName);
    if (spell == null) return;

    if (castingInfo.isCasting) {
      castingInfo.cancel = true;
    } else {
      if (spell.canCast(player)) {
        // If we can cast it, do so!
        castingInfo.currentMana -= spell.manaCost;
        castingInfo.isCasting = true;
        castingInfo.castingTimer = spell.castingTimeTicks;
        castingInfo.spellName = castingInfo.equippedSpellName;
        castingInfo.startCasting = true;
      } else {
        // Otherwise, mark that we failed to cast a spell.
        castingInfo.failedCasting = true;
        // Rather than using a separate var to track failed casting, we reuse the same timer.
        castingInfo.castingTimer = 20;
      }
    }

    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

  public static void handleTick(PlayerTickEvent.Pre event) {
    Player player = event.getEntity();
    if (!player.level().isClientSide()) return;

    AttributeMap attributes = player.getAttributes();
    AttributeInstance maxManaInstance = attributes.getInstance(SkramMagic.MAX_MANA);
    AttributeInstance manaRegenInstance = attributes.getInstance(SkramMagic.MANA_REGENERATION);
    if (maxManaInstance == null || manaRegenInstance == null) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    Spell spell = Spells.SPELLS_BY_NAME.get(castingInfo.spellName);

    // First, we increase our mana based on our mana regen value.
    castingInfo.currentMana = Math.min(maxManaInstance.getValue(), castingInfo.currentMana + manaRegenInstance.getValue());

    // If we are currently casting, decrease the timer.
    if (castingInfo.isCasting) {
      // Reset cancel status if we've just started casting.
      if (castingInfo.startCasting) {
        castingInfo.cancel = false;
      }

      // Wait a tick before consuming startCasting().
      if (castingInfo.startCasting && (spell.castingTimeTicks - castingInfo.castingTimer) >= 1) {
        castingInfo.startCasting = false;
      }
      castingInfo.castingTimer--;

      if (castingInfo.cancel) {
        // If we got the cancel signal from somewhere, cancel the spell.
        // We refund mana based on time left.
        double refund = ((double) castingInfo.castingTimer / spell.castingTimeTicks) * spell.manaCost * 0.8;
        castingInfo.isCasting = false;
        castingInfo.castingTimer = 0;
        castingInfo.currentMana += refund;
        castingInfo.spellName = "";
        castingInfo.cancel = false;
      } else if (castingInfo.castingTimer <= 0) {
        // Reset our values, and cast our spell.
        castingInfo.isCasting = false;
        castingInfo.castingTimer = 0;

        spell.sendCastPacket(player);
      }
    }

    // If we failed casting.
    if (castingInfo.failedCasting) {
      if (castingInfo.isCasting) {
        castingInfo.failedCasting = false;
      } else {
        castingInfo.castingTimer--;
        if (castingInfo.castingTimer <= 0) {
          castingInfo.failedCasting = false;
        }
      }
    }

    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
    ClientPacketDistributor.sendToServer(new CastingSyncServerPacket(castingInfo));
  }

  public static SpellCastSoundHandler HANDLER = null;

  public static void handleTickPost(PlayerTickEvent.Post event) {
    Player player = event.getEntity();
    if (!player.level().isClientSide()) return;

    AttributeMap attributes = player.getAttributes();
    AttributeInstance maxManaInstance = attributes.getInstance(SkramMagic.MAX_MANA);
    AttributeInstance manaRegenInstance = attributes.getInstance(SkramMagic.MANA_REGENERATION);
    if (maxManaInstance == null || manaRegenInstance == null) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    SpellUI.update(castingInfo, maxManaInstance.getValue());

    if (HANDLER == null || HANDLER.player != player) {
      HANDLER = new SpellCastSoundHandler(player);
    }

    if (castingInfo.failedCasting) {
      HANDLER.failedCasting();
    } else if (castingInfo.startCasting) {
      HANDLER.startCasting(castingInfo.castingTimer);
    } else if (!castingInfo.isCasting) {
      HANDLER.stopCasting();
    }

    HANDLER.tick();
  }

  public static void cancelSpell(AttackEntityEvent event) {
    Player player = event.getEntity();
    if (!player.level().isClientSide()) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    castingInfo.cancel = true;
    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

  public static void cancelSpell(PlayerInteractEvent event) {
    Player player = event.getEntity();
    if (!player.level().isClientSide()) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    castingInfo.cancel = true;
    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

  @SubscribeEvent
  public static void ON_TICK(PlayerTickEvent.Pre event) {
    handleTick(event);
  }

  @SubscribeEvent
  public static void ON_TICK_POST(PlayerTickEvent.Post event) {
    handleTickPost(event);
  }

}
