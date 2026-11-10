package com.avi.skram.perks;

import com.avi.skram.Skram;
import com.avi.skram.attachments.MiscAttachments;
import com.avi.skram.attachments.SkramEntityModifier;
import com.avi.skram.network.PerkPacket;
import com.avi.skram.skills.Skill;
import com.avi.skram.ui.GenericMessageToast;
import com.avi.skram.ui.ToastUtils;
import com.avi.skram.utils.MathUtils;
import com.avi.skram.utils.StringUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Perks {

  public static final Identifier UNKNOWN_ICON = Identifier.fromNamespaceAndPath(Skram.MODID, "textures/perks/unknown.png");

  public static Map<String, Supplier> PERKS_BY_NAME;

  public static Supplier<AttachmentType<PerkBackstab>> BACKSTAB;
  public static Supplier<AttachmentType<PerkBatman>> BATMAN;
  public static Supplier<AttachmentType<PerkBountifulCatch>> BOUNTIFUL_CATCH;
  public static Supplier<AttachmentType<PerkChainReaction>> CHAIN_REACTION;
  public static Supplier<AttachmentType<PerkClusterBomb>> CLUSTER_BOMB;
  public static Supplier<AttachmentType<PerkDebonair>> DEBONAIR;
  public static Supplier<AttachmentType<PerkDefuser>> DEFUSER;
  public static Supplier<AttachmentType<PerkEggSnatcher>> EGG_SNATCHER;
  public static Supplier<AttachmentType<PerkExpertEnchanter>> EXPERT_ENCHANTER;
  public static Supplier<AttachmentType<PerkFlyingDutchman>> FLYING_DUTCHMAN;
  public static Supplier<AttachmentType<PerkGemini>> GEMINI;
  public static Supplier<AttachmentType<PerkGlutton>> GLUTTON;
  public static Supplier<AttachmentType<PerkGrenadier>> GRENADIER;
  public static Supplier<AttachmentType<PerkHamstring>> HAMSTRING;
  public static Supplier<AttachmentType<PerkHeartOfTheForge>> HEART_OF_THE_FORGE;
  public static Supplier<AttachmentType<PerkIronGut>> IRON_GUT;
  public static Supplier<AttachmentType<PerkJuggernaut>> JUGGERNAUT;
  public static Supplier<AttachmentType<PerkLuckyDog>> LUCKY_DOG;
  public static Supplier<AttachmentType<PerkMasterBlacksmith>> MASTER_BLACKSMITH;
  public static Supplier<AttachmentType<PerkPhotosynthesis>> PHOTOSYNTHESIS;
  public static Supplier<AttachmentType<PerkPreciseSmasher>> PRECISE_SMASHER;
  public static Supplier<AttachmentType<PerkPugilist>> PUGILIST;
  public static Supplier<AttachmentType<PerkSlayer>> SLAYER;
  public static Supplier<AttachmentType<PerkSniper>> SNIPER;
  public static Supplier<AttachmentType<PerkSpaceJump>> SPACE_JUMP;
  public static Supplier<AttachmentType<PerkSpeedEater>> SPEED_EATER;
  public static Supplier<AttachmentType<PerkSpinSlash>> SPIN_SLASH;
  public static Supplier<AttachmentType<PerkStalwartSpellcaster>> STALWART_SPELLCASTER;
  public static Supplier<AttachmentType<PerkSwiftStrafer>> SWIFT_STRAFER;
  public static Supplier<AttachmentType<PerkThievery>> THIEVERY;
  public static Supplier<AttachmentType<PerkVampirism>> VAMPIRISM;
  public static Supplier<AttachmentType<PerkVitalicBreathing>> VITALIC_BREATHING;
  public static Supplier<AttachmentType<PerkVolcanicSailor>> VOLCANIC_SAILOR;

  public static Map<String, Supplier<List<ItemStack>>> PERK_RESEARCH_COST_BY_NAME;
  public static Map<String, List<Supplier>> SKILLS_TO_PERKS;

  public static void register() {
    PERKS_BY_NAME = new HashMap<>();
    PERK_RESEARCH_COST_BY_NAME = new HashMap<>();

    BACKSTAB = Skram.ATTACHMENT_TYPES.register(
      PerkBackstab.NAME, () -> AttachmentType.serializable(() -> new PerkBackstab())
        .sync(new PerkSyncHandler<>(PerkBackstab.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkBackstab.NAME, BACKSTAB);
    PERK_RESEARCH_COST_BY_NAME.put(PerkBackstab.NAME, PerkBackstab::getResearchCost);
    PerkBackstab.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(BACKSTAB));

    BATMAN = Skram.ATTACHMENT_TYPES.register(
      PerkBatman.NAME, () -> AttachmentType.serializable(() -> new PerkBatman())
        .sync(new PerkSyncHandler<>(PerkBatman.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkBatman.NAME, BATMAN);
    PERK_RESEARCH_COST_BY_NAME.put(PerkBatman.NAME, PerkBatman::getResearchCost);
    PerkBatman.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(BATMAN));

    BOUNTIFUL_CATCH = Skram.ATTACHMENT_TYPES.register(
      PerkBountifulCatch.NAME, () -> AttachmentType.serializable(() -> new PerkBountifulCatch())
        .sync(new PerkSyncHandler<>(PerkBountifulCatch.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkBountifulCatch.NAME, BOUNTIFUL_CATCH);
    PERK_RESEARCH_COST_BY_NAME.put(PerkBountifulCatch.NAME, PerkBountifulCatch::getResearchCost);
    PerkBountifulCatch.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(BOUNTIFUL_CATCH));

    CHAIN_REACTION = Skram.ATTACHMENT_TYPES.register(
      PerkChainReaction.NAME, () -> AttachmentType.serializable(() -> new PerkChainReaction())
        .sync(new PerkSyncHandler<>(PerkChainReaction.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkChainReaction.NAME, CHAIN_REACTION);
    PERK_RESEARCH_COST_BY_NAME.put(PerkChainReaction.NAME, PerkChainReaction::getResearchCost);
    PerkChainReaction.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(CHAIN_REACTION));

    CLUSTER_BOMB = Skram.ATTACHMENT_TYPES.register(
      PerkClusterBomb.NAME, () -> AttachmentType.serializable(() -> new PerkClusterBomb())
        .sync(new PerkSyncHandler<>(PerkClusterBomb.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkClusterBomb.NAME, CLUSTER_BOMB);
    PERK_RESEARCH_COST_BY_NAME.put(PerkClusterBomb.NAME, PerkClusterBomb::getResearchCost);
    PerkClusterBomb.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(CLUSTER_BOMB));

    DEBONAIR = Skram.ATTACHMENT_TYPES.register(
      PerkDebonair.NAME, () -> AttachmentType.serializable(() -> new PerkDebonair())
        .sync(new PerkSyncHandler<>(PerkDebonair.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkDebonair.NAME, DEBONAIR);
    PERK_RESEARCH_COST_BY_NAME.put(PerkDebonair.NAME, PerkDebonair::getResearchCost);
    PerkDebonair.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(DEBONAIR));

    DEFUSER = Skram.ATTACHMENT_TYPES.register(
      PerkDefuser.NAME, () -> AttachmentType.serializable(() -> new PerkDefuser())
        .sync(new PerkSyncHandler<>(PerkDefuser.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkDefuser.NAME, DEFUSER);
    PERK_RESEARCH_COST_BY_NAME.put(PerkDefuser.NAME, PerkDefuser::getResearchCost);
    PerkDefuser.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(DEFUSER));

    EGG_SNATCHER = Skram.ATTACHMENT_TYPES.register(
      PerkEggSnatcher.NAME, () -> AttachmentType.serializable(() -> new PerkEggSnatcher())
        .sync(new PerkSyncHandler<>(PerkEggSnatcher.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkEggSnatcher.NAME, EGG_SNATCHER);
    PERK_RESEARCH_COST_BY_NAME.put(PerkEggSnatcher.NAME, PerkEggSnatcher::getResearchCost);
    PerkEggSnatcher.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(EGG_SNATCHER));

    EXPERT_ENCHANTER = Skram.ATTACHMENT_TYPES.register(
      PerkExpertEnchanter.NAME, () -> AttachmentType.serializable(() -> new PerkExpertEnchanter())
        .sync(new PerkSyncHandler<>(PerkExpertEnchanter.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkExpertEnchanter.NAME, EXPERT_ENCHANTER);
    PERK_RESEARCH_COST_BY_NAME.put(PerkExpertEnchanter.NAME, PerkExpertEnchanter::getResearchCost);
    PerkExpertEnchanter.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(EXPERT_ENCHANTER));

    FLYING_DUTCHMAN = Skram.ATTACHMENT_TYPES.register(
      PerkFlyingDutchman.NAME, () -> AttachmentType.serializable(() -> new PerkFlyingDutchman())
        .sync(new PerkSyncHandler<>(PerkFlyingDutchman.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkFlyingDutchman.NAME, FLYING_DUTCHMAN);
    PERK_RESEARCH_COST_BY_NAME.put(PerkFlyingDutchman.NAME, PerkFlyingDutchman::getResearchCost);
    PerkFlyingDutchman.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(FLYING_DUTCHMAN));

    GEMINI = Skram.ATTACHMENT_TYPES.register(
      PerkGemini.NAME, () -> AttachmentType.serializable(() -> new PerkGemini())
        .sync(new PerkSyncHandler<>(PerkGemini.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkGemini.NAME, GEMINI);
    PERK_RESEARCH_COST_BY_NAME.put(PerkGemini.NAME, PerkGemini::getResearchCost);
    PerkGemini.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(GEMINI));

    GLUTTON = Skram.ATTACHMENT_TYPES.register(
      PerkGlutton.NAME, () -> AttachmentType.serializable(() -> new PerkGlutton())
        .sync(new PerkSyncHandler<>(PerkGlutton.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkGlutton.NAME, GLUTTON);
    PERK_RESEARCH_COST_BY_NAME.put(PerkGlutton.NAME, PerkGlutton::getResearchCost);
    PerkGlutton.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(GLUTTON));

    GRENADIER = Skram.ATTACHMENT_TYPES.register(
      PerkGrenadier.NAME, () -> AttachmentType.serializable(() -> new PerkGrenadier())
        .sync(new PerkSyncHandler<>(PerkGrenadier.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkGrenadier.NAME, GRENADIER);
    PERK_RESEARCH_COST_BY_NAME.put(PerkGrenadier.NAME, PerkGrenadier::getResearchCost);
    PerkGrenadier.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(GRENADIER));

    HAMSTRING = Skram.ATTACHMENT_TYPES.register(
      PerkHamstring.NAME, () -> AttachmentType.serializable(() -> new PerkHamstring())
        .sync(new PerkSyncHandler<>(PerkHamstring.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkHamstring.NAME, HAMSTRING);
    PERK_RESEARCH_COST_BY_NAME.put(PerkHamstring.NAME, PerkHamstring::getResearchCost);
    PerkHamstring.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(HAMSTRING));

    HEART_OF_THE_FORGE = Skram.ATTACHMENT_TYPES.register(
      PerkHeartOfTheForge.NAME, () -> AttachmentType.serializable(() -> new PerkHeartOfTheForge())
        .sync(new PerkSyncHandler<>(PerkHeartOfTheForge.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkHeartOfTheForge.NAME, HEART_OF_THE_FORGE);
    PERK_RESEARCH_COST_BY_NAME.put(PerkHeartOfTheForge.NAME, PerkHeartOfTheForge::getResearchCost);
    PerkHeartOfTheForge.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(HEART_OF_THE_FORGE));

    IRON_GUT = Skram.ATTACHMENT_TYPES.register(
      PerkIronGut.NAME, () -> AttachmentType.serializable(() -> new PerkIronGut())
        .sync(new PerkSyncHandler<>(PerkIronGut.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkIronGut.NAME, IRON_GUT);
    PERK_RESEARCH_COST_BY_NAME.put(PerkIronGut.NAME, PerkIronGut::getResearchCost);
    PerkIronGut.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(IRON_GUT));

    JUGGERNAUT = Skram.ATTACHMENT_TYPES.register(
      PerkJuggernaut.NAME, () -> AttachmentType.serializable(() -> new PerkJuggernaut())
        .sync(new PerkSyncHandler<>(PerkJuggernaut.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkJuggernaut.NAME, JUGGERNAUT);
    PERK_RESEARCH_COST_BY_NAME.put(PerkJuggernaut.NAME, PerkJuggernaut::getResearchCost);
    PerkJuggernaut.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(JUGGERNAUT));

    LUCKY_DOG = Skram.ATTACHMENT_TYPES.register(
      PerkLuckyDog.NAME, () -> AttachmentType.serializable(() -> new PerkLuckyDog())
        .sync(new PerkSyncHandler<>(PerkLuckyDog.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkLuckyDog.NAME, LUCKY_DOG);
    PERK_RESEARCH_COST_BY_NAME.put(PerkLuckyDog.NAME, PerkLuckyDog::getResearchCost);

    PerkLuckyDog.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(LUCKY_DOG));

    MASTER_BLACKSMITH = Skram.ATTACHMENT_TYPES.register(
      PerkMasterBlacksmith.NAME, () -> AttachmentType.serializable(() -> new PerkMasterBlacksmith())
        .sync(new PerkSyncHandler<>(PerkMasterBlacksmith.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkMasterBlacksmith.NAME, MASTER_BLACKSMITH);
    PERK_RESEARCH_COST_BY_NAME.put(PerkMasterBlacksmith.NAME, PerkMasterBlacksmith::getResearchCost);
    PerkMasterBlacksmith.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(MASTER_BLACKSMITH));

    PHOTOSYNTHESIS = Skram.ATTACHMENT_TYPES.register(
      PerkPhotosynthesis.NAME, () -> AttachmentType.serializable(() -> new PerkPhotosynthesis())
        .sync(new PerkSyncHandler<>(PerkPhotosynthesis.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkPhotosynthesis.NAME, PHOTOSYNTHESIS);
    PERK_RESEARCH_COST_BY_NAME.put(PerkPhotosynthesis.NAME, PerkPhotosynthesis::getResearchCost);
    PerkPhotosynthesis.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(PHOTOSYNTHESIS));

    PRECISE_SMASHER = Skram.ATTACHMENT_TYPES.register(
      PerkPreciseSmasher.NAME, () -> AttachmentType.serializable(() -> new PerkPreciseSmasher())
        .sync(new PerkSyncHandler<>(PerkPreciseSmasher.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkPreciseSmasher.NAME, PRECISE_SMASHER);
    PERK_RESEARCH_COST_BY_NAME.put(PerkPreciseSmasher.NAME, PerkPreciseSmasher::getResearchCost);
    PerkPreciseSmasher.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(PRECISE_SMASHER));

    PUGILIST = Skram.ATTACHMENT_TYPES.register(
      PerkPugilist.NAME, () -> AttachmentType.serializable(() -> new PerkPugilist())
        .sync(new PerkSyncHandler<>(PerkPugilist.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkPugilist.NAME, PUGILIST);
    PERK_RESEARCH_COST_BY_NAME.put(PerkPugilist.NAME, PerkPugilist::getResearchCost);
    PerkPugilist.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(PUGILIST));

    SLAYER = Skram.ATTACHMENT_TYPES.register(
      PerkSlayer.NAME, () -> AttachmentType.serializable(() -> new PerkSlayer())
        .sync(new PerkSyncHandler<>(PerkSlayer.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkSlayer.NAME, SLAYER);
    PERK_RESEARCH_COST_BY_NAME.put(PerkSlayer.NAME, PerkSlayer::getResearchCost);
    PerkSlayer.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(SLAYER));

    SNIPER = Skram.ATTACHMENT_TYPES.register(
      PerkSniper.NAME, () -> AttachmentType.serializable(() -> new PerkSniper())
        .sync(new PerkSyncHandler<>(PerkSniper.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkSniper.NAME, SNIPER);
    PERK_RESEARCH_COST_BY_NAME.put(PerkSniper.NAME, PerkSniper::getResearchCost);
    PerkSniper.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(SNIPER));

    SPACE_JUMP = Skram.ATTACHMENT_TYPES.register(
      PerkSpaceJump.NAME, () -> AttachmentType.serializable(() -> new PerkSpaceJump())
        .sync(new PerkSyncHandler<>(PerkSpaceJump.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkSpaceJump.NAME, SPACE_JUMP);
    PERK_RESEARCH_COST_BY_NAME.put(PerkSpaceJump.NAME, PerkSpaceJump::getResearchCost);
    PerkSpaceJump.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(SPACE_JUMP));

    SPEED_EATER = Skram.ATTACHMENT_TYPES.register(
      PerkSpeedEater.NAME, () -> AttachmentType.serializable(() -> new PerkSpeedEater())
        .sync(new PerkSyncHandler<>(PerkSpeedEater.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkSpeedEater.NAME, SPEED_EATER);
    PERK_RESEARCH_COST_BY_NAME.put(PerkSpeedEater.NAME, PerkSpeedEater::getResearchCost);
    PerkSpeedEater.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(SPEED_EATER));

    SPIN_SLASH = Skram.ATTACHMENT_TYPES.register(
      PerkSpinSlash.NAME, () -> AttachmentType.serializable(() -> new PerkSpinSlash())
        .sync(new PerkSyncHandler<>(PerkSpinSlash.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkSpinSlash.NAME, SPIN_SLASH);
    PERK_RESEARCH_COST_BY_NAME.put(PerkSpinSlash.NAME, PerkSpinSlash::getResearchCost);
    PerkSpinSlash.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(SPIN_SLASH));

    STALWART_SPELLCASTER = Skram.ATTACHMENT_TYPES.register(
      PerkStalwartSpellcaster.NAME, () -> AttachmentType.serializable(() -> new PerkStalwartSpellcaster())
        .sync(new PerkSyncHandler<>(PerkStalwartSpellcaster.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkStalwartSpellcaster.NAME, STALWART_SPELLCASTER);
    PERK_RESEARCH_COST_BY_NAME.put(PerkStalwartSpellcaster.NAME, PerkStalwartSpellcaster::getResearchCost);
    PerkStalwartSpellcaster.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(STALWART_SPELLCASTER));

    SWIFT_STRAFER = Skram.ATTACHMENT_TYPES.register(
      PerkSwiftStrafer.NAME, () -> AttachmentType.serializable(() -> new PerkSwiftStrafer())
        .sync(new PerkSyncHandler<>(PerkSwiftStrafer.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkSwiftStrafer.NAME, SWIFT_STRAFER);
    PERK_RESEARCH_COST_BY_NAME.put(PerkSwiftStrafer.NAME, PerkSwiftStrafer::getResearchCost);
    PerkSwiftStrafer.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(SWIFT_STRAFER));

    THIEVERY = Skram.ATTACHMENT_TYPES.register(
      PerkThievery.NAME, () -> AttachmentType.serializable(() -> new PerkThievery())
        .sync(new PerkSyncHandler<>(PerkThievery.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkThievery.NAME, THIEVERY);
    PERK_RESEARCH_COST_BY_NAME.put(PerkThievery.NAME, PerkThievery::getResearchCost);
    PerkThievery.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(THIEVERY));

    VAMPIRISM = Skram.ATTACHMENT_TYPES.register(
      PerkVampirism.NAME, () -> AttachmentType.serializable(() -> new PerkVampirism())
        .sync(new PerkSyncHandler<>(PerkVampirism.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkVampirism.NAME, VAMPIRISM);
    PERK_RESEARCH_COST_BY_NAME.put(PerkVampirism.NAME, PerkVampirism::getResearchCost);
    PerkVampirism.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(VAMPIRISM));

    VITALIC_BREATHING = Skram.ATTACHMENT_TYPES.register(
      PerkVitalicBreathing.NAME, () -> AttachmentType.serializable(() -> new PerkVitalicBreathing())
        .sync(new PerkSyncHandler<>(PerkVitalicBreathing.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkVitalicBreathing.NAME, VITALIC_BREATHING);
    PERK_RESEARCH_COST_BY_NAME.put(PerkVitalicBreathing.NAME, PerkVitalicBreathing::getResearchCost);
    PerkVitalicBreathing.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(VITALIC_BREATHING));

    VOLCANIC_SAILOR = Skram.ATTACHMENT_TYPES.register(
      PerkVolcanicSailor.NAME, () -> AttachmentType.serializable(() -> new PerkVolcanicSailor())
        .sync(new PerkSyncHandler<>(PerkVolcanicSailor.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkVolcanicSailor.NAME, VOLCANIC_SAILOR);
    PERK_RESEARCH_COST_BY_NAME.put(PerkVolcanicSailor.NAME, PerkVolcanicSailor::getResearchCost);
    PerkVolcanicSailor.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(VOLCANIC_SAILOR));


    // Logging purposes.
    System.out.println("SKILLS_TO_PERKS");
    for (String skillName : SKILLS_TO_PERKS.keySet().stream().sorted().toList()) {
      System.out.println(skillName + ": " + SKILLS_TO_PERKS.get(skillName).size());
    }
  }

  public static void discoverRandomPerk(Player player, Skill skill, boolean syncToServer) {
    List<Pair<Perk, Supplier>> perks = new ArrayList<>();
    for (Supplier perkSupplier : SKILLS_TO_PERKS.get(skill.name)) {
      Perk perk = (Perk) player.getData(perkSupplier);
      if (perk.discovered) continue;

      perks.add(Pair.of(perk, perkSupplier));
    }

    if (perks.isEmpty()) return;

    // Otherwise, randomly choose one to discover.
    Pair<Perk, Supplier> toDiscover = perks.get(MathUtils.RANDOM.nextInt(perks.size()));
    Perk perkToDiscover = toDiscover.getFirst();
    perkToDiscover.discovered = true;

    Supplier supplier = toDiscover.getSecond();
    if (syncToServer) {
      ClientPacketDistributor.sendToServer(new PerkPacket(perkToDiscover.name, true, false, true));
      if (!ToastUtils.hasGenericMessageToast(Minecraft.getInstance().gui.toastManager(), perkToDiscover.name + ":discovered")) {
        Minecraft.getInstance().gui.toastManager().addToast(new GenericMessageToast(
          perkToDiscover.name + ":discovered",
          perkToDiscover.getIcon(),
          "Perk discovered: " + StringUtils.snakeToTitle(perkToDiscover.name)
        ));
      }
    } else {
      // This should sync back to the client automatically.
      player.setData(supplier, perkToDiscover);
    }
  }

  // We are doing this here in case we ever need to attach multiple entity modifier values.
  // Probably mildly overengineered, but eh.
  public static void applyEntityModifier(LivingDamageEvent.Pre event) {
    Player player = null;
    if (event.getSource().getEntity() instanceof Player) {
      player = (Player) event.getSource().getEntity();
    } else if (event.getSource().getDirectEntity() instanceof Player) {
      player = (Player) event.getSource().getDirectEntity();
    } else {
      return;
    }

    if (player == null) return;

    SkramEntityModifier modifier = new SkramEntityModifier();

    PerkDefuser perk = player.getData(Perks.DEFUSER);
    if (perk.isFullyEnabled() && (event.getEntity() instanceof Creeper)) {
      modifier.canExplode = false;
    }

    event.getEntity().setData(MiscAttachments.SKRAM_ENTITY_MODIFIER, modifier);
  }
}
