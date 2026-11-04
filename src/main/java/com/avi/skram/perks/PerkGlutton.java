package com.avi.skram.perks;

import com.avi.skram.skills.SkillCooking;
import com.avi.skram.skills.SkillHealth;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkGlutton extends Perk {
  public static final String NAME = "glutton";
  public static final String DESCRIPTION = "Eating food now applies regeneration and hunger.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillCooking.NAME, 40)
    .put(SkillHealth.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkGlutton> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkGlutton::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.BREAD, 640),
      new ItemStack(Items.BAKED_POTATO, 640),
      new ItemStack(Items.COOKED_BEEF, 256)
    );
  }

  public PerkGlutton() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkGlutton(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
