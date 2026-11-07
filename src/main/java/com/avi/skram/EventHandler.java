package com.avi.skram;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlocks;
import com.avi.skram.blocks.PlayerOwnedData;
import com.avi.skram.commands.SkramCommands;
import com.avi.skram.magic.SkramMagic;
import com.avi.skram.magic.spells.SpellMinorHeal;
import com.avi.skram.magic.spells.Spells;
import com.avi.skram.network.*;
import com.avi.skram.perks.*;
import com.avi.skram.skills.*;
import com.avi.skram.ui.AddSkillButtonToInventory;
import com.avi.skram.ui.SpellUI;
import com.avi.skram.ui.SkramScreen;
import com.avi.skram.ui.XPGainGui;
import com.avi.skram.utils.ReflectionUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Skram.MODID)
public class EventHandler {

  public static final KeyMapping.Category SKRAM_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Skram.MODID, "skram"));
  public static final Lazy<KeyMapping> OPEN_SKILLS_UI = Lazy.of(() -> new KeyMapping("key.skram.open_skills_ui", InputConstants.Type.KEYBOARD, InputConstants.KEY_R, SKRAM_CATEGORY));
  public static final Lazy<KeyMapping> CAST_SPELL = Lazy.of(() -> new KeyMapping("key.skram.cast_spell", InputConstants.Type.KEYBOARD, InputConstants.KEY_C, SKRAM_CATEGORY));

  @SubscribeEvent
  public static void ON_PLAYER_PLACED_BLOCK(BlockEvent.EntityPlaceEvent event) {
    if (event.getLevel().isClientSide()) return;
    if (!(event.getEntity() instanceof Player player)) return;

    ChunkAccess chunk = event.getLevel().getChunk(event.getPos());
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);

    SkillBrewing brewing = player.getData(Skills.BREWING);
    SkillDemolition demolition = player.getData(Skills.DEMOLITION);
    SkillFarming farming = player.getData(Skills.FARMING);
    SkillSmithing smithing = player.getData(Skills.SMITHING);

    PlayerOwnedData data = new PlayerOwnedData();
    data.uuid = player.getUUID();
    data.displayName = player.getDisplayName().getString();
    data.growthChance = farming.getCropGrowthChance();
    data.smithingSpeed = smithing.getSmithingSpeed();
    data.brewingSpeed = brewing.getBrewingSpeed();
    data.explosionPower = demolition.getBlastPower();

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
      // Trigger the break block criteria!
      Skram.BREAK_BLOCK_TRIGGER.get().trigger((ServerPlayer) event.getPlayer(), event.getState());
    } else if (SkillFarming.isPlantFullyGrown(event.getState())) {
      SkillFarming.gainXp(event);
      // Trigger the break block criteria!
      Skram.BREAK_BLOCK_TRIGGER.get().trigger((ServerPlayer) event.getPlayer(), event.getState());
    }

    // Remove the block from our map.
    pob.playerOwnedBlocks.remove(event.getPos());
    chunk.markUnsaved();
  }

  @SubscribeEvent
  public static void ON_BLOCK_DROPS(BlockDropsEvent event) {
    if (!(event.getBreaker() instanceof Player)) return;
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
    // Loop through all skills and apply modifiers.
    for (Supplier skillSupplier : Skills.SKILLS_BY_NAME.values()) {
      Skill skill = (Skill) event.getEntity().getData(skillSupplier);
      skill.applyModifiers(event.getEntity());
    }
  }

  @SubscribeEvent
  public static void ON_PLAYER_RESPAWN(PlayerEvent.PlayerRespawnEvent event) {
    // Loop through all skills and apply modifiers.
    for (Supplier skillSupplier : Skills.SKILLS_BY_NAME.values()) {
      Skill skill = (Skill) event.getEntity().getData(skillSupplier);
      skill.applyModifiers(event.getEntity());
    }
  }

  @SubscribeEvent
  public static void ON_PLAYER_CHANGE_DIMENSION(PlayerEvent.PlayerChangedDimensionEvent event) {
    // Loop through all skills and apply modifiers.
    for (Supplier skillSupplier : Skills.SKILLS_BY_NAME.values()) {
      Skill skill = (Skill) event.getEntity().getData(skillSupplier);
      skill.applyModifiers(event.getEntity());
    }
  }

  @SubscribeEvent
  public static void ON_PLAYER_JUMP(LivingEvent.LivingJumpEvent event) {
    SkillAcrobatics.handleJump(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_ATTACK_ENTITY(AttackEntityEvent event) {
    SkramMagic.cancelSpell(event);
    SkillMelee.handleAttackEvent(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_LEFT_CLICK_BLOCK(PlayerInteractEvent.LeftClickBlock event) {
    SkramMagic.cancelSpell(event);
    SkillDigging.handleLeftClick(event);
    SkillMining.handleLeftClick(event);
    SkillWoodcutting.handleLeftClick(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_RIGHT_CLICK_BLOCK(PlayerInteractEvent.RightClickBlock event) {
    SkramMagic.cancelSpell(event);
    SkillDigging.handleRightClick(event);
    SkillFarming.handleRightClick(event);
    SkillHusbandry.handleBlockInteract(event);
    SkillWoodcutting.handleRightClick(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_RIGHT_CLICK_ITEM(PlayerInteractEvent.RightClickItem event) {
    SkramMagic.cancelSpell(event);
    PerkBatman.handleBatman(event);
    PerkClusterBomb.handleItemRightClick(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_ENTITY_INTERACT(PlayerInteractEvent.EntityInteract event) {
    SkramMagic.cancelSpell(event);
    SkillHusbandry.handleEntityInteract(event);
    PerkDebonair.handleEntityInteract(event);
    PerkThievery.handleEntityInteract(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_FISHED(ItemFishedEvent event) {
    SkillFishing.handleCatchFish(event);
    PerkBountifulCatch.handleCatchFish(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_USE_ITEM_TICK(LivingEntityUseItemEvent.Tick event) {
    SkillRanged.applyDrawSpeed(event);

    PerkSpeedEater.handleEating(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_USE_ITEM_FINISH(LivingEntityUseItemEvent.Finish event) {
    PerkGlutton.handleEat(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_SNEAKING(LivingEvent.LivingVisibilityEvent event) {
    SkillStealth.applyVisibility(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_SHIELD_BLOCK(LivingShieldBlockEvent event) {
    SkillHealth.handleShieldBlockEvent(event);
  }

  @SubscribeEvent
  public static void ON_ANVIL_UPDATE(AnvilUpdateEvent event) {
    SkillSmithing.handleAnvilUpdate(event);
    PerkMasterBlacksmith.handleAnvilUpdate(event);
  }

  @SubscribeEvent
  public static void ON_ANVIL_CRAFT(AnvilCraftEvent.Pre event) {
    SkillSmithing.handleAnvilCraft(event);
  }

  @SubscribeEvent
  public static void ON_VILLAGER_TRADE(TradeWithVillagerEvent event) {
    SkillMercantile.handleTrade(event);
  }

  @SubscribeEvent
  public static void ON_BABY_SPAWNED(BabyEntitySpawnEvent event) {
    SkillHusbandry.handleBreedEvent(event);
    PerkGemini.handleBreedEvent(event);
  }

  @SubscribeEvent
  public static void ON_LIVING_HEAL(LivingHealEvent event) {
    SkillHealth.handleHealEvent(event);
  }

  @SubscribeEvent
  public static void ON_LIVING_DROPS(LivingDropsEvent event) {
    SkillHusbandry.applyFortune(event);
    PerkEggSnatcher.harvestEggs(event);
  }

  @SubscribeEvent
  public static void ON_LIVING_XP_DROP(LivingExperienceDropEvent event) {
    PerkSlayer.addMoreXP(event);
  }

  @SubscribeEvent
  public static void ON_LIVING_DEATH(LivingDeathEvent event) {
    SkillDemolition.handleKill(event);
    SkillHusbandry.handleAnimalKilled(event);
    PerkVampirism.handleKill(event);
  }

  @SubscribeEvent
  public static void ON_ANIMAL_TAMED(AnimalTameEvent event) {
    SkillHusbandry.handleAnimalTamed(event);
  }

  @SubscribeEvent
  public static void ON_ENTITY_HURT(LivingDamageEvent.Pre event) {
    PerkBackstab.handleDamageEvent(event);
    PerkPugilist.handleDamageEvent(event);
    PerkSniper.handleDamageEvent(event);

    SkillDemolition.applyBlastResistance(event);
    SkillDemolition.handleDamageEvent(event);
    SkillMelee.handleDamageEvent(event);
    SkillRanged.handleDamageEvent(event);
    SkillStealth.handleDamageEvent(event);

    // Make sure that SkillHealth.handleDamageEvent() is called last, so that damage values get updated by skills.
    SkillHealth.handleDamageEvent(event);

    SkramMagic.cancelSpell(event);

    Perks.applyEntityModifier(event);
    PerkHamstring.handleDamageEvent(event);
    PerkJuggernaut.handleDamageEvent(event);
    PerkVitalicBreathing.handleDamageEvent(event);
  }

  @SubscribeEvent
  public static void ON_CROP_GROW_PRE(CropGrowEvent.Pre event) {
    SkillFarming.handleCropGrowth(event);
  }

  @SubscribeEvent
  public static void ON_CROP_GROW_POST(CropGrowEvent.Post event) {
    PerkChainReaction.handleCropGrowth(event);
  }

  @SubscribeEvent
  public static void ON_TICK(PlayerTickEvent.Pre event) {
    SkillAthletics.handleTick(event);
    SkramMagic.handleTick(event);
  }

  @SubscribeEvent
  public static void ON_TICK_POST(PlayerTickEvent.Post event) {
    SkramMagic.handleTickPost(event);
  }

  @SubscribeEvent
  public static void REGISTER_SERVER_PAYLOAD_HANDLERS(RegisterPayloadHandlersEvent event) {
    final PayloadRegistrar registrar = event.registrar("1");
    registrar.playBidirectional(
      SkillPacket.TYPE,
      SkillPacket.STREAM_CODEC,
      ServerPayloadHandler::handleSkillPacket
    );
    registrar.playBidirectional(
      PerkPacket.TYPE,
      PerkPacket.STREAM_CODEC,
      ServerPayloadHandler::handlePerkPacket
    );
    registrar.playBidirectional(
      PerkResearchPacket.TYPE,
      PerkResearchPacket.STREAM_CODEC,
      ServerPayloadHandler::handlePerkResearchPacket
    );
    registrar.playToServer(
      BoatJumpPacket.TYPE,
      BoatJumpPacket.STREAM_CODEC,
      ServerPayloadHandler::handleBoatJumpPacket
    );
    registrar.playToServer(
      SpellCastPacket.TYPE,
      SpellCastPacket.STREAM_CODEC,
      ServerPayloadHandler::handleSpellCastPacket
    );
    registrar.playToServer(
      SpellEquipPacket.TYPE,
      SpellEquipPacket.STREAM_CODEC,
      ServerPayloadHandler::handleSpellEquipPacket
    );
  }

  @SubscribeEvent
  public static void REGISTER_CLIENT_PAYLOAD_HANDLERS(RegisterClientPayloadHandlersEvent event) {
    event.register(SkillPacket.TYPE, ClientPayloadHandler::handleSkillPacket);
    event.register(PerkPacket.TYPE, ClientPayloadHandler::handlePerkPacket);
    event.register(PerkResearchPacket.TYPE, ClientPayloadHandler::handlePerkResearchPacket);
  }

  public static final Identifier CUSTOM_LAYER = Identifier.fromNamespaceAndPath(Skram.MODID, "asdf");

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

  @SubscribeEvent
  public static void REGISTER_BINDINGS(RegisterKeyMappingsEvent event) {
    event.registerCategory(SKRAM_CATEGORY);
    event.register(OPEN_SKILLS_UI.get());
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
      // Need to send a packet to handle the cast.
      ClientPacketDistributor.sendToServer(new SpellCastPacket(SpellMinorHeal.NAME));
    }
  }

  @SubscribeEvent
  public static void registerCommands(RegisterCommandsEvent event) {
    SkramCommands.register(event);
  }

  @SubscribeEvent
  public static void registerCustomAttributes(EntityAttributeModificationEvent event) {
    event.add(EntityTypes.PLAYER, SkramMagic.MAX_MANA);
    event.add(EntityTypes.PLAYER, SkramMagic.MANA_REGENERATION);
  }

  public static void overwriteVanilla() {
    // Check if the current registry being processed is the FluidType registry
    FluidType lavaType = Fluids.LAVA.getFluidType();
    try {
      ReflectionUtils.setFieldValue(lavaType, "supportsBoating", true);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.out.println("LAVA_BOATING FAILURE: " + e);
    }
  }



}
