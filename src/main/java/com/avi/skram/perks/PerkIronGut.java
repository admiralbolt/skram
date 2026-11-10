package com.avi.skram.perks;

import com.avi.skram.skills.SkillBrewing;
import com.avi.skram.skills.SkillCooking;
import com.avi.skram.skills.SkillHealth;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkIronGut extends Perk {

  public static final String NAME = "iron_gut";
  public static final String DESCRIPTION = "Gain immunity to poison.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillBrewing.NAME, 25)
    .put(SkillCooking.NAME, 25)
    .put(SkillHealth.NAME, 25)
    .build();
  public static final StreamCodec<ByteBuf, PerkIronGut> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkIronGut::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.SPIDER_EYE, 128),
      new ItemStack(Items.STRING, 128),
      new ItemStack(Items.MILK_BUCKET, 4),
      new ItemStack(Items.HONEY_BOTTLE, 4)
    );
  }

  public PerkIronGut() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkIronGut(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }
}
