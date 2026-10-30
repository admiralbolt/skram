package com.avi.skram;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlocks;
import com.avi.skram.blocks.PlayerOwnedData;
import com.avi.skram.network.ClientPayloadHandler;
import com.avi.skram.network.ServerPayloadHandler;
import com.avi.skram.skills.*;
import com.avi.skram.ui.AddSkillButtonToInventory;
import com.avi.skram.ui.SkillScreen;
import com.avi.skram.ui.XPGainGui;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Skram.MODID)
public class EventHandler {

  public static final KeyMapping.Category SKRAM_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Skram.MODID, "skram"));
  public static final Lazy<KeyMapping> OPEN_SKILLS_UI = Lazy.of(() -> new KeyMapping("key.skram.open_skills_ui", InputConstants.Type.KEYBOARD, InputConstants.KEY_R, SKRAM_CATEGORY));

  @SubscribeEvent
  public static void ON_PLAYER_PLACED_BLOCK(BlockEvent.EntityPlaceEvent event) {
    if (event.getLevel().isClientSide()) return;
    if (!(event.getEntity() instanceof Player player)) return;

    ChunkAccess chunk = event.getLevel().getChunk(event.getPos());
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);

    SkillFarming farming = player.getData(Skills.FARMING);
    PlayerOwnedData data = new PlayerOwnedData();
    data.uuid = player.getUUID();
    data.displayName = player.getDisplayName().getString();
    data.growthChance = farming.getCropGrowthChance();

    pob.playerOwnedBlocks.put(event.getPos(), data);
    // Need to manually mark it unsaved so the data gets saved.
    chunk.markUnsaved();
  }

  @SubscribeEvent
  public static void ON_BREAKING_BLOCK(PlayerEvent.BreakSpeed event) {
    SkillDigging.applySpeed(event);
    SkillMining.applySpeed(event);
    SkillWoodcutting.applySpeed(event);
  }

  @SubscribeEvent
  public static void ON_BLOCK_BROKEN(BreakBlockEvent event) {
    if (event.getLevel().isClientSide()) return;
    if (event.isCanceled()) return;

    // Check for player placed blocks.
    LevelAccessor level = event.getLevel();
    ChunkAccess chunk = level.getChunk(event.getPos());
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);
    if (!pob.playerOwnedBlocks.containsKey(event.getPos())) {
      SkillBotany.gainXp(event);
      SkillDigging.gainXp(event);
      SkillFarming.gainXp(event);
      SkillMining.gainXp(event);
      SkillWoodcutting.gainXp(event);
    } else if (SkillFarming.isPlantFullyGrown(event.getState())) {
      SkillFarming.gainXp(event);
    }

    // Remove the block from our map.
    pob.playerOwnedBlocks.remove(event.getPos());
    chunk.markUnsaved();
  }

  @SubscribeEvent
  public static void ON_BLOCK_DROPS(BlockDropsEvent event) {
    LevelAccessor level = event.getLevel();
    ChunkAccess chunk = level.getChunk(event.getPos());
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);

    if (SkillFarming.isGrowablePlant(event.getState())) {
      // If it's a growable plant, it _must_ be fully grown.
      if (SkillFarming.isPlantFullyGrown(event.getState())) {
        SkillFarming.applyFortune(event);
      }
    } else if (!pob.playerOwnedBlocks.containsKey(event.getPos())) {
      SkillBotany.applyFortune(event);
      SkillFarming.applyFortune(event);
      SkillMining.applyFortune(event);
    }
  }

  @SubscribeEvent
  public static void ON_ITEM_SMELTED(PlayerEvent.ItemSmeltedEvent event) {
    if (event.getEntity().level().isClientSide()) return;

    SkillCooking.overrideFood(event.getEntity(), event.getSmelting());
  }

  @SubscribeEvent
  public static void ON_ITEM_CRAFTED(PlayerEvent.ItemCraftedEvent event) {
    if (event.getEntity().level().isClientSide()) return;

    SkillCooking.handleCrafting(event.getEntity(), event.getCrafting());
  }

  @SubscribeEvent
  public static void ON_SCREEN_INIT_POST(ScreenEvent.Init.Post event) {
    AddSkillButtonToInventory.addButtonOnInit(event);
  }

  @SubscribeEvent
  public static void ON_SCREEN_RENDER(ScreenEvent.Render.Post event) {
    AddSkillButtonToInventory.adjustButtonOnReRender(event);
  }

  @SubscribeEvent
  public static void ON_JOIN_WORLD(PlayerEvent.PlayerLoggedInEvent event) {
    SkillAcrobatics acrobatics = event.getEntity().getData(Skills.ACROBATICS);
    acrobatics.applyModifiers(event.getEntity());
    SkillAthletics athletics = event.getEntity().getData(Skills.ATHLETICS);
    athletics.applyModifiers(event.getEntity());
  }

  @SubscribeEvent
  public static void ON_PLAYER_RESPAWN(PlayerEvent.PlayerRespawnEvent event) {
    SkillAcrobatics acrobatics = event.getEntity().getData(Skills.ACROBATICS);
    acrobatics.applyModifiers(event.getEntity());
    SkillAthletics athletics = event.getEntity().getData(Skills.ATHLETICS);
    athletics.applyModifiers(event.getEntity());
  }

  @SubscribeEvent
  public static void ON_PLAYER_CHANGE_DIMENSION(PlayerEvent.PlayerChangedDimensionEvent event) {
    SkillAcrobatics acrobatics = event.getEntity().getData(Skills.ACROBATICS);
    acrobatics.applyModifiers(event.getEntity());
    SkillAthletics athletics = event.getEntity().getData(Skills.ATHLETICS);
    athletics.applyModifiers(event.getEntity());
  }

  @SubscribeEvent
  public static void ON_PLAYER_JUMP(LivingEvent.LivingJumpEvent event) {
    SkillAcrobatics.handleJump(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_LEFT_CLICK_BLOCK(PlayerInteractEvent.LeftClickBlock event) {
    SkillMining.handleLeftClick(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_FISHED(ItemFishedEvent event) {
    SkillFishing.handleCatchFish(event);
  }

  @SubscribeEvent
  public static void ON_ENTITY_HURT(LivingDamageEvent.Pre event) {
    SkillMelee.handleDamageEvent(event);
  }

  @SubscribeEvent
  public static void ON_CROP_GROW_PRE(CropGrowEvent.Pre event) {
    SkillFarming.handleCropGrowth(event);
  }

  @SubscribeEvent
  public static void ON_TICK(PlayerTickEvent.Pre event) {
    SkillAthletics.handleTick(event);
  }

  @SubscribeEvent
  public static void REGISTER_SERVER_PAYLOAD_HANDLERS(RegisterPayloadHandlersEvent event) {
    final PayloadRegistrar registrar = event.registrar("1");
    registrar.playBidirectional(
      SkillPacket.TYPE,
      SkillPacket.STREAM_CODEC,
      ServerPayloadHandler::handleSkillPacket
    );
  }

  @SubscribeEvent
  public static void REGISTER_CLIENT_PAYLOAD_HANDLERS(RegisterClientPayloadHandlersEvent event) {
    event.register(SkillPacket.TYPE, ClientPayloadHandler::handleSkillPacket);
  }

  public static final Identifier CUSTOM_LAYER = Identifier.fromNamespaceAndPath(Skram.MODID, "asdf");

  @SubscribeEvent
  public static void REGISTER_GUI_LAYERS(RegisterGuiLayersEvent event) {
    event.registerAbove(
      VanillaGuiLayers.HOTBAR,
      CUSTOM_LAYER,
      XPGainGui.layer
    );
  }

  @SubscribeEvent
  public static void REGISTER_BINDINGS(RegisterKeyMappingsEvent event) {
    event.registerCategory(SKRAM_CATEGORY);
    event.register(OPEN_SKILLS_UI.get());
  }

  @SubscribeEvent
  public static void ON_CLIENT_TICK(ClientTickEvent.Post event) {
    while (OPEN_SKILLS_UI.get().consumeClick()) {
      Minecraft mc = Minecraft.getInstance();
      if (!(mc.gui.screen() instanceof SkillScreen)) {
        Minecraft.getInstance().setScreenAndShow(new SkillScreen());
      }
    }
  }

}
