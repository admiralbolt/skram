package com.avi.skram.perks;

import com.avi.skram.skills.SkillHusbandry;
import com.avi.skram.skills.SkillMelee;
import com.avi.skram.skills.SkillRanged;
import com.avi.skram.skills.SkillStealth;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkEggSnatcher extends Perk {
  public static final String NAME = "egg_snatcher";
  public static final String DESCRIPTION = "ANY entity killed has a 5% chance of dropping their spawn egg.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMelee.NAME, 40)
    .put(SkillRanged.NAME, 40)
    .put(SkillHusbandry.NAME, 40)
    .put(SkillStealth.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkEggSnatcher> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkEggSnatcher::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.EGG, 48),
      new ItemStack(Items.BROWN_EGG, 48),
      new ItemStack(Items.BLUE_EGG, 48),
      new ItemStack(Items.TURTLE_EGG, 1),
      new ItemStack(Items.SNIFFER_EGG, 1)
    );
  }

  public PerkEggSnatcher() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkEggSnatcher(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
