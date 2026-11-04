package com.avi.skram.perks;

import com.avi.skram.skills.SkillMagic;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkExpertEnchanter extends Perk {
  public static final String NAME = "expert_enchanter";
  public static final String DESCRIPTION = "Whenever you enchant an item, you have a 25% chance to pay no XP.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMagic.NAME, 40)
    .build();
  public static final StreamCodec<ByteBuf, PerkExpertEnchanter> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkExpertEnchanter::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.LAPIS_LAZULI, 256),
      new ItemStack(Items.OBSIDIAN, 64)
    );
  }

  public PerkExpertEnchanter() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkExpertEnchanter(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
