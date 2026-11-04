package com.avi.skram.perks;

import com.avi.skram.skills.SkillMelee;
import com.avi.skram.skills.SkillRanged;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkSlayer extends Perk {
  public static final String NAME = "slayer";
  public static final String DESCRIPTION = "Gain +25% XP from slain mobs.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMelee.NAME, 30)
    .put(SkillRanged.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkSlayer> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkSlayer::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.ROTTEN_FLESH, 640),
      new ItemStack(Items.BONE, 320),
      new ItemStack(Items.GUNPOWDER, 128),
      new ItemStack(Items.WITHER_ROSE, 24)
    );
  }

  public PerkSlayer() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkSlayer(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

}
