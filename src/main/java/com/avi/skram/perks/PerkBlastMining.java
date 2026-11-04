package com.avi.skram.perks;

import com.avi.skram.skills.SkillDemolition;
import com.avi.skram.skills.SkillDigging;
import com.avi.skram.skills.SkillMining;
import com.avi.skram.skills.SkillWoodcutting;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkBlastMining extends Perk {
  public static final String NAME = "blast_mining";
  public static final String DESCRIPTION = "Your TNT has a 100% chance to drop broken blocks.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillDemolition.NAME, 30)
    .put(SkillMining.NAME, 20)
    .put(SkillDigging.NAME, 20)
    .put(SkillWoodcutting.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkBlastMining> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkBlastMining::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.TNT, 64),
      new ItemStack(Items.DIAMOND_PICKAXE, 1),
      new ItemStack(Items.DIAMOND_SHOVEL, 1),
      new ItemStack(Items.DIAMOND_AXE, 1)
    );
  }

  public PerkBlastMining() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkBlastMining(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
