package com.avi.skram.perks;

import com.avi.skram.skills.SkillSailing;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkCaptainOfLegend extends Perk {
  public static final String NAME = "captain_of_legend";
  public static final String DESCRIPTION = "Boats you are in can no longer break.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillSailing.NAME, 50)
    .build();
  public static final StreamCodec<ByteBuf, PerkCaptainOfLegend> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkCaptainOfLegend::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.OAK_BOAT, 1),
      new ItemStack(Items.SPRUCE_BOAT, 1),
      new ItemStack(Items.BIRCH_BOAT, 1),
      new ItemStack(Items.JUNGLE_BOAT, 1),
      new ItemStack(Items.ACACIA_BOAT, 1),
      new ItemStack(Items.DARK_OAK_BOAT, 1),
      new ItemStack(Items.MANGROVE_BOAT, 1),
      new ItemStack(Items.CHERRY_BOAT, 1),
      new ItemStack(Items.PALE_OAK_BOAT, 1),
      new ItemStack(Items.POPLAR_BOAT, 1)
    );
  }

  public PerkCaptainOfLegend() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkCaptainOfLegend(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
