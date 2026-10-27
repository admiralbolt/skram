package com.avi.skram;

import com.avi.skram.network.ClientPayloadHandler;
import com.avi.skram.network.ServerPayloadHandler;
import com.avi.skram.skills.*;
import com.avi.skram.ui.AddSkillButtonToInventory;
import com.avi.skram.ui.XPGainGui;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Skram.MODID)
public class EventHandler {

  @SubscribeEvent
  public static void ON_BREAKING_BLOCK(PlayerEvent.BreakSpeed event) {
    SkillDigging.applySpeed(event);
    SkillMining.applySpeed(event);
  }

  @SubscribeEvent
  public static void ON_BLOCK_BROKEN(BreakBlockEvent event) {
    if (event.getLevel().isClientSide()) return;
    if (event.isCanceled()) return;

    SkillDigging.gainXp(event);
    SkillMining.gainXp(event);
  }

  @SubscribeEvent
  public static void ON_BLOCK_DROPS(BlockDropsEvent event) {
    SkillMining.applyFortune(event);
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
    SkillAcrobatics acrobatics = (SkillAcrobatics) event.getEntity().getData(Skills.ACROBATICS);
    acrobatics.applyModifiers(event.getEntity());
    SkillAthletics athletics = (SkillAthletics) event.getEntity().getData(Skills.ATHLETICS);
    athletics.applyModifiers(event.getEntity());
  }

  @SubscribeEvent
  public static void ON_PLAYER_RESPAWN(PlayerEvent.PlayerRespawnEvent event) {
    SkillAcrobatics acrobatics = (SkillAcrobatics) event.getEntity().getData(Skills.ACROBATICS);
    acrobatics.applyModifiers(event.getEntity());
    SkillAthletics athletics = (SkillAthletics) event.getEntity().getData(Skills.ATHLETICS);
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

}
