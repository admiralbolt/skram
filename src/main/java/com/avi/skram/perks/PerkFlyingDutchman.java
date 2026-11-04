package com.avi.skram.perks;

import com.avi.skram.skills.SkillAcrobatics;
import com.avi.skram.skills.SkillSailing;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkFlyingDutchman extends Perk {
  public static final String NAME = "flying_dutchman";
  public static final String DESCRIPTION = "You can now jump while controlling a boat.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillAcrobatics.NAME, 20)
    .put(SkillSailing.NAME, 50)
    .build();
  public static final StreamCodec<ByteBuf, PerkFlyingDutchman> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkFlyingDutchman::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.JUNGLE_BOAT, 1),
      new ItemStack(Items.MANGROVE_BOAT, 1),
      new ItemStack(Items.CHERRY_BOAT, 1),
      new ItemStack(Items.PALE_OAK_BOAT, 1),
      new ItemStack(Items.POPLAR_BOAT, 1),
      new ItemStack(Items.FEATHER, 640)
    );
  }

  public PerkFlyingDutchman() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkFlyingDutchman(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }
}
