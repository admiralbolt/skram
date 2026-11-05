package com.avi.skram.perks;

import com.avi.skram.skills.SkillAcrobatics;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkSpaceJump extends Perk {

  public static final String NAME = "space_jump";
  public static final String DESCRIPTION = "";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder().put(SkillAcrobatics.NAME, 50).build();
  public static final StreamCodec<ByteBuf, PerkSpaceJump> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkSpaceJump::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(new ItemStack(Items.GOLDEN_BOOTS, 24));
  }

  public PerkSpaceJump() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkSpaceJump(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }
}