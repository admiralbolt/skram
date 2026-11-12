package com.avi.skram;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlocks;
import com.avi.skram.blocks.PlayerOwnedData;
import com.avi.skram.commands.SkramCommands;
import com.avi.skram.effect.FlankedEffect;
import com.avi.skram.effect.HexedEffect;
import com.avi.skram.magic.SkramMagic;
import com.avi.skram.network.*;
import com.avi.skram.perks.*;
import com.avi.skram.skills.*;
import com.avi.skram.ui.SkramScreen;
import com.avi.skram.utils.ReflectionUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Skram.MODID)
public class EventHandler {

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

    Skills.handleRequiredLevels(event);
  }

  @SubscribeEvent
  public static void ON_PLAYER_RIGHT_CLICK_ITEM(PlayerInteractEvent.RightClickItem event) {
    SkramMagic.cancelSpell(event);
    PerkBatman.handleBatman(event);
    PerkClusterBomb.handleItemRightClick(event);

    Skills.handleRequiredLevels(event);
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
  public static void ON_EFFECT_APPLIED(MobEffectEvent.Applicable event) {
    if (!(event.getEntity() instanceof Player player)) return;

    if (event.getEffectInstance().getEffect() == MobEffects.POISON) {
      PerkIronGut perk = player.getData(Perks.IRON_GUT);
      if (perk.isFullyEnabled()) {
        event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
      }
    }
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
    PerkGenealogy.makeHorseGoFast(event);
    PerkGemini.handleBreedEvent(event);
  }

  @SubscribeEvent
  public static void ON_LIVING_HEAL(LivingHealEvent event) {
    SkillHealth.handleHealEvent(event);
    HexedEffect.reduceHealing(event);
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
    // Increase damage from flat sources first.
    PerkPugilist.handleDamageEvent(event);

    // Increase damage from multipliers in no particular order.
    PerkBackstab.handleDamageEvent(event);
    PerkSniper.handleDamageEvent(event);
    SkillDemolition.applyBlastResistance(event);
    SkillMelee.handleDamageEvent(event);
    SkillRanged.handleDamageEvent(event);

    // Apply hexed effect after increasing damage from all sources, before applying XP.
    FlankedEffect.increaseDamage(event);
    HexedEffect.increaseDamage(event);

    // Gain XP from relevant skills after damage numbers have been updated.
    SkillDemolition.gainDemolitionXP(event);
    SkillMelee.gainMeleeXP(event);
    SkillRanged.gainRangedXP(event);
    SkillStealth.gainStealthXP(event);
    SkillHealth.gainHealthXP(event);

    // Misc effects that aren't order dependent.
    SkramMagic.cancelSpell(event);
    Perks.applyEntityModifier(event);
    PerkHamstring.handleDamageEvent(event);
    PerkJuggernaut.handleDamageEvent(event);
    PerkTeamwork.handleDamageEvent(event);
    PerkVitalicBreathing.handleDamageEvent(event);
  }

  @SubscribeEvent
  public static void ON_ENTITY_HURT_POST(LivingDamageEvent.Post event) {
    PerkPirateering.handleDamageEvent(event);
  }

  @SubscribeEvent
  public static void ON_GO_BOOM(ExplosionEvent.Detonate event) {
    // To properly track stuff for demolition we want to add players as the source for any explosion they cause.
    // This has an unintended effect of excluding the player from damage calculation in certain cases, due to
    // the explosion source being excluded by default in ServerExplosion:
    //   List<Entity> list = this.level.getEntities(this.source, new AABB((double)x0, (double)y0, (double)z0, (double)x1, (double)y1, (double)z1));
    // If this is player owned explosion, we add them back to the list of entities that should be affected.
    ServerExplosion explosion = event.getExplosion();
    Player player = null;
    if (explosion.getDirectSourceEntity() instanceof Player) {
      player = (Player) explosion.getDirectSourceEntity();
    } else if (explosion.getIndirectSourceEntity() instanceof Player) {
      player = (Player) explosion.getIndirectSourceEntity();
    }
    if (player == null) return;

    // Do a quick and dirty check just to make sure they're in range. This roughly follows the same logic of
    // ServerExplosion.
    if (explosion.center().distanceTo(player.position()) > (explosion.radius() * 2)) return;

    event.getAffectedEntities().add(player);
  }

  @SubscribeEvent
  public static void ON_CROP_GROW_PRE(CropGrowEvent.Pre event) {
    SkillFarming.handleCropGrowth(event);
  }

  @SubscribeEvent
  public static void ON_CROP_GROW_POST(CropGrowEvent.Post event) {
    SkillFarming.handleTallPlants(event);
    PerkChainReaction.handleCropGrowth(event);
  }

  @SubscribeEvent
  public static void ON_TICK(PlayerTickEvent.Pre event) {
    SkillAthletics.handleTick(event);
    SkramMagic.handleTick(event);
    PerkPhotosynthesis.healInSunlight(event);
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
    registrar.playToServer(
      CastingSyncServerPacket.TYPE,
      CastingSyncServerPacket.STREAM_CODEC,
      ServerPayloadHandler::handleCastingSyncPacket
    );
  }

  @SubscribeEvent
  public static void REGISTER_CLIENT_PAYLOAD_HANDLERS(RegisterClientPayloadHandlersEvent event) {
    event.register(SkillPacket.TYPE, ClientPayloadHandler::handleSkillPacket);
    event.register(PerkPacket.TYPE, ClientPayloadHandler::handlePerkPacket);
    event.register(PerkResearchPacket.TYPE, ClientPayloadHandler::handlePerkResearchPacket);
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
