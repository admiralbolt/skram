package com.avi.skram.perks;

import com.avi.skram.skills.SkillHealth;
import com.avi.skram.skills.SkillSmithing;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkHeartOfTheForge extends Perk {

  public static final String NAME = "heart_of_the_forge";
  public static final String DESCRIPTION = "Reduces time spent burning by 50%";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillSmithing.NAME, 40)
    .put(SkillHealth.NAME, 30)
    .build();
  public static StreamCodec<ByteBuf, PerkHeartOfTheForge> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkHeartOfTheForge::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.IRON_INGOT, 64),
      new ItemStack(Items.BLAZE_ROD, 64)
    );
  }

  public PerkHeartOfTheForge() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkHeartOfTheForge(ByteBuf buffer) {
    super(buffer);
  }

}
