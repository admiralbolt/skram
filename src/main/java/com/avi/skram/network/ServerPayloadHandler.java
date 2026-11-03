package com.avi.skram.network;

import com.avi.skram.perks.Perk;
import com.avi.skram.perks.Perks;
import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Arrays;

public class ServerPayloadHandler {

  public static void handleSkillPacket(final SkillPacket packet, final IPayloadContext context) {
    Player player = context.player();
    Skill skill = (Skill) player.getData(Skills.SKILLS_BY_NAME.get(packet.name()));
    // Modify the skill values. We don't actually need to call "setData" here. It's assumed this method is invoked
    // because we need to sync client -> server, in which case the client already has the correct value.
    if (packet.level() != skill.level) {
      skill.level = packet.level();
      skill.onLevelChange(player);
    }
    skill.xp = packet.xp();
    skill.xpMultiplier = packet.xpMultiplier();
    if (!Arrays.equals(skill.enabledAbilities, packet.enabledAbilities())) {
      skill.enabledAbilities = packet.enabledAbilities();
      skill.applyModifiers(player);
    }
  }

  public static void handlePerkPacket(final PerkPacket packet, final IPayloadContext context) {
    Player player = context.player();
    Perk perk = (Perk) player.getData(Perks.PERKS_BY_NAME.get(packet.name()));
    perk.discovered = packet.discovered();
    if (perk.researched && packet.enabled() != perk.enabled) {
      perk.enabled = packet.enabled();
      perk.applyModifiers(player);
    }
  }

  public static void handlePerkResearchPacket(final PerkResearchPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      var perkAttachment = Perks.PERKS_BY_NAME.get(packet.name());
      if (perkAttachment == null) return;

      Player player = context.player();
      Perk perk = (Perk) player.getData(perkAttachment);
      if (perk.research(player)) {
        context.reply(packet);
      }
    });
  }

}
