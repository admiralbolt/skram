package com.avi.skram.network;

import com.avi.skram.skills.Skill;
import com.avi.skram.skills.SkillPacket;
import com.avi.skram.skills.Skills;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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
    skill.enabled = packet.enabled();
  }
}
