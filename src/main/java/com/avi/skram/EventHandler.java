package com.avi.skram;

import com.avi.skram.skills.Skill;
import com.avi.skram.skills.SkillDigging;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

@EventBusSubscriber(modid = Skram.MODID)
public class EventHandler {

  @SubscribeEvent
  public static void ON_BREAKING_BLOCK(PlayerEvent.BreakSpeed event) {
    SkillDigging.applySpeed(event);
  }

  @SubscribeEvent
  public static void ON_BLOCK_BROKEN(BreakBlockEvent event) {
    if (event.isCanceled()) return;

    SkillDigging.gainXp(event);
  }
}
