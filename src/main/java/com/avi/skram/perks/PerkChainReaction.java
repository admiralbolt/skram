package com.avi.skram.perks;

import com.avi.skram.skills.SkillFarming;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkChainReaction extends Perk {

  public static final String NAME = "chain_reaction";
  public static final String DESCRIPTION = "Whenever one of your crops grows, it has a 33% chance of growing again.";
  public static final Map<String, Integer> REQUIRED_SKILLS = Map.of(
    SkillFarming.NAME, 40
  );
  public static final StreamCodec<ByteBuf, PerkChainReaction> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkChainReaction::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.NETHERITE_HOE, 1),
      new ItemStack(Items.HAY_BLOCK, 256),
      new ItemStack(Items.CARROT, 256),
      new ItemStack(Items.POTATO, 256),
      new ItemStack(Items.BEETROOT, 256)
    );
  }

  public PerkChainReaction() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkChainReaction(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

}