package com.avi.skram.perks;

import com.avi.skram.Skram;
import com.avi.skram.network.PerkPacket;
import com.avi.skram.skills.Skill;
import com.avi.skram.utils.MathUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Perks {

  public static Map<String, Supplier> PERKS_BY_NAME;

  public static Supplier<AttachmentType<PerkChainReaction>> CHAIN_REACTION;
  public static Supplier<AttachmentType<PerkHeartOfTheForge>> HEART_OF_THE_FORGE;
  public static Supplier<AttachmentType<PerkLuckyDog>> LUCKY_DOG;

  public static Map<String, Supplier<List<ItemStack>>> PERK_RESEARCH_COST_BY_NAME;
  public static Map<String, List<Supplier>> SKILLS_TO_PERKS;

  public static void register() {
    PERKS_BY_NAME = new HashMap<>();
    PERK_RESEARCH_COST_BY_NAME = new HashMap<>();

    CHAIN_REACTION = Skram.ATTACHMENT_TYPES.register(
      PerkChainReaction.NAME, () -> AttachmentType.serializable(() -> new PerkChainReaction())
        .sync(new PerkSyncHandler<>(PerkChainReaction.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkChainReaction.NAME, CHAIN_REACTION);
    PERK_RESEARCH_COST_BY_NAME.put(PerkChainReaction.NAME, PerkChainReaction::getResearchCost);
    PerkChainReaction.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(CHAIN_REACTION));

    HEART_OF_THE_FORGE = Skram.ATTACHMENT_TYPES.register(
      PerkHeartOfTheForge.NAME, () -> AttachmentType.serializable(() -> new PerkHeartOfTheForge())
        .sync(new PerkSyncHandler<>(PerkHeartOfTheForge.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkHeartOfTheForge.NAME, HEART_OF_THE_FORGE);
    PERK_RESEARCH_COST_BY_NAME.put(PerkHeartOfTheForge.NAME, PerkHeartOfTheForge::getResearchCost);
    PerkHeartOfTheForge.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(HEART_OF_THE_FORGE));

    LUCKY_DOG = Skram.ATTACHMENT_TYPES.register(
      PerkLuckyDog.NAME, () -> AttachmentType.serializable(() -> new PerkLuckyDog())
        .sync(new PerkSyncHandler<>(PerkLuckyDog.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkLuckyDog.NAME, LUCKY_DOG);
    PERK_RESEARCH_COST_BY_NAME.put(PerkLuckyDog.NAME, PerkLuckyDog::getResearchCost);

    PerkLuckyDog.REQUIRED_SKILLS.keySet().forEach((skillName) -> SKILLS_TO_PERKS.get(skillName).add(LUCKY_DOG));
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
    ;
    Supplier supplier = toDiscover.getSecond();
    if (syncToServer) {
      ClientPacketDistributor.sendToServer(new PerkPacket(perkToDiscover.name, true, false, true));
    } else {
      // This should sync back to the client automatically.
      perkToDiscover.discovered = true;
      player.setData(supplier, perkToDiscover);
    }
  }
}
