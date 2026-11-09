package com.avi.skram.perks;

import com.avi.skram.skills.SkillBrewing;
import com.avi.skram.skills.SkillRanged;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkGrenadier extends Perk {
  public static final String NAME = "grenadier";
  public static final String DESCRIPTION = "Double the range and splash radius of thrown potions.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillRanged.NAME, 20)
    .put(SkillBrewing.NAME, 40)
    .build();
  public static final StreamCodec<ByteBuf, PerkGrenadier> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkGrenadier::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.SPLASH_POTION, 12),
      new ItemStack(Items.SPECTRAL_ARROW, 128)
    );
  }

  public PerkGrenadier() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkGrenadier(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }
}
