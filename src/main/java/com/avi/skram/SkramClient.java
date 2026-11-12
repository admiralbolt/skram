package com.avi.skram;

import com.avi.skram.client.screens.MegaChestScreen;
import com.avi.skram.client.screens.GigaChestScreen;
import com.avi.skram.inventory.SkramMenus;
import com.avi.skram.magic.SkramMagic;
import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.spells.Spell;
import com.avi.skram.magic.spells.Spells;
import com.avi.skram.ui.SkramScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.util.Lazy;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Skram.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Skram.MODID, value = Dist.CLIENT)
public class SkramClient {

    public static final KeyMapping.Category SKRAM_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Skram.MODID, "skram"));
    public static final Lazy<KeyMapping> OPEN_SKILLS_UI = Lazy.of(() -> new KeyMapping("key.skram.open_skills_ui", InputConstants.Type.KEYBOARD, InputConstants.KEY_R, SKRAM_CATEGORY));
    public static final Lazy<KeyMapping> CAST_SPELL = Lazy.of(() -> new KeyMapping("key.skram.cast_spell", InputConstants.Type.KEYBOARD, InputConstants.KEY_C, SKRAM_CATEGORY));

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
}
