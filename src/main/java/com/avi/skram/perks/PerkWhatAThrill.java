package com.avi.skram.perks;

import com.avi.skram.skills.SkillAcrobatics;
import com.avi.skram.skills.SkillAthletics;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkWhatAThrill extends Perk {
  public static final String NAME = "what_a_thrill";
  public static final String DESCRIPTION = "Climb 3x as fast.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillAthletics.NAME, 20)
    .put(SkillAcrobatics.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkWhatAThrill> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkWhatAThrill::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.LADDER, 256)
    );
  }

  public PerkWhatAThrill() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkWhatAThrill(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }
}
