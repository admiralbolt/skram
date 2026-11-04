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

public class PerkPugilist extends Perk {
  public static final String NAME = "pugilist";
  public static final String DESCRIPTION = "Unarmed melee attacks deal +10 damage.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMelee.NAME, 60)
    .put(SkillAthletics.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkPugilist> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkPugilist::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.LEATHER, 256)
    );
  }

  public PerkPugilist() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkPugilist(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
