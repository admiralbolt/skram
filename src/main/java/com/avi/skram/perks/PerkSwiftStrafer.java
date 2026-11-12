package com.avi.skram.perks;

import com.avi.skram.skills.SkillAthletics;
import com.avi.skram.skills.SkillRanged;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkSwiftStrafer extends Perk {
  public static final String NAME = "swift_strafer";
  public static final String DESCRIPTION = "Move at full speed while aiming down sights.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillRanged.NAME, 20)
    .put(SkillAthletics.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkSwiftStrafer> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkSwiftStrafer::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.GOLDEN_BOOTS, 12),
      new ItemStack(Items.BOW, 12)
    );
  }

  public PerkSwiftStrafer() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkSwiftStrafer(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }


}
