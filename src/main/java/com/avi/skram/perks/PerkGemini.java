package com.avi.skram.perks;

import com.avi.skram.skills.SkillHusbandry;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkGemini extends Perk {
  public static final String NAME = "gemini";
  public static final String DESCRIPTION = "Whenever you breed animals, there is a 40% chance to breed twins.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillHusbandry.NAME, 40)
    .build();
  public static final StreamCodec<ByteBuf, PerkGemini> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkGemini::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.ECHO_SHARD, 2)
    );
  }

  public PerkGemini() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkGemini(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
