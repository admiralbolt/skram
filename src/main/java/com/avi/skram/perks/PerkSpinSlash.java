package com.avi.skram.perks;

import com.avi.skram.skills.SkillAthletics;
import com.avi.skram.skills.SkillMelee;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkSpinSlash extends Perk {

  public static final String NAME = "spin_slash";
  public static final double INCREASE = 0.3;

  public static final String DESCRIPTION = "Increases range of sweep attacks by " + (int) (100 * INCREASE) + "%.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMelee.NAME, 25)
    .put(SkillAthletics.NAME, 15)
    .build();
  public static final StreamCodec<ByteBuf, PerkSpinSlash> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkSpinSlash::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.DIAMOND_SWORD, 8)
    );
  }

  public PerkSpinSlash() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkSpinSlash(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

}