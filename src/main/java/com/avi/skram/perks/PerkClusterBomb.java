package com.avi.skram.perks;

import com.avi.skram.skills.SkillBrewing;
import com.avi.skram.skills.SkillDemolition;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.SkillRanged;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkClusterBomb extends Perk {
  public static final String NAME = "cluster_bomb";
  public static final String DESCRIPTION = "Create 2 additional copies of any splash potion thrown.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillBrewing.NAME, 60)
    .put(SkillMagic.NAME, 40)
    .put(SkillRanged.NAME, 30)
    .put(SkillDemolition.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkClusterBomb> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkClusterBomb::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.SPLASH_POTION, 12),
      new ItemStack(Items.REDSTONE, 64),
      new ItemStack(Items.GLOWSTONE_DUST, 64),
      new ItemStack(Items.GUNPOWDER, 64)
    );
  }

  public PerkClusterBomb() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkClusterBomb(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
