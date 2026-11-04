package com.avi.skram.perks;

import com.avi.skram.skills.SkillAcrobatics;
import com.avi.skram.skills.SkillAthletics;
import com.avi.skram.skills.SkillFishing;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkBatman extends Perk {
  public static final String NAME = "batman";
  public static final String DESCRIPTION = "Your fishing rod can now be used as a grappling hook.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillFishing.NAME, 40)
    .put(SkillAthletics.NAME, 20)
    .put(SkillAcrobatics.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkBatman> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkBatman::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.FISHING_ROD, 8),
      new ItemStack(Items.BAT_SPAWN_EGG, 1)
    );
  }

  public PerkBatman() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkBatman(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
