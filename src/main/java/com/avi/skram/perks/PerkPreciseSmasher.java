package com.avi.skram.perks;

import com.avi.skram.skills.SkillDigging;
import com.avi.skram.skills.SkillMining;
import com.avi.skram.skills.SkillWoodcutting;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkPreciseSmasher extends Perk {
  public static final String NAME = "precise_smasher";
  public static final String DESCRIPTION = "Gain a 10% chance to not damage any tool when breaking a block. (stacks with unbreaking)";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillDigging.NAME, 20)
    .put(SkillMining.NAME, 20)
    .put(SkillWoodcutting.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkPreciseSmasher> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkPreciseSmasher::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.OBSIDIAN, 256)
    );
  }

  public PerkPreciseSmasher() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkPreciseSmasher(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

}
