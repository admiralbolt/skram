package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public class SkillDigging extends Skill {

  public static final String NAME = "digging";
  public static StreamCodec<ByteBuf, SkillDigging> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillDigging::new);

  public SkillDigging() {
    super(NAME, 1, 0);
  }

  public SkillDigging(ByteBuf buffer) {
    super(buffer);
  }

  public float getSpeedBonus() {
    // return 0.01f * this.level;
    return 10f;
  }

  public static void applySpeed(PlayerEvent.BreakSpeed event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_SHOVEL)) return;

    SkillDigging digging = (SkillDigging) event.getEntity().getData(Skills.DIGGING);
    event.setNewSpeed(event.getOriginalSpeed() * (1 + digging.getSpeedBonus()));
  }

  public static void gainXp(BreakBlockEvent event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_SHOVEL)) return;

    SkillDigging digging = (SkillDigging) event.getPlayer().getData(Skills.DIGGING);
    digging.addXp(event.getPlayer(), 50);
  }

}
