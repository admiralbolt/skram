package com.avi.skram.perks;

import com.avi.skram.skills.SkillRanged;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkSniper extends Perk {
  public static final String NAME = "sniper";
  public static final String DESCRIPTION = "Deal up to +50% damage depending on how far away you are from the target.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillRanged.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkSniper> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkSniper::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.SPYGLASS, 12),
      new ItemStack(Items.CROSSBOW, 12)
    );
  }

  public PerkSniper() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkSniper(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
