package com.avi.skram.perks;

import com.avi.skram.skills.SkillHealth;
import com.avi.skram.skills.SkillSailing;
import com.avi.skram.skills.SkillSmithing;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkVolcanicSailor extends Perk {

  public static final String NAME = "volcanic_sailor";
  public static final String DESCRIPTION = "Safely sail on lava. While in a boat on lava, both you and your boat are immune to fire.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillSailing.NAME, 40)
    .put(SkillSmithing.NAME, 30)
    .put(SkillHealth.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkVolcanicSailor> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkVolcanicSailor::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.OAK_LOG, 64),
      new ItemStack(Items.IRON_INGOT, 64),
      new ItemStack(Items.OBSIDIAN, 64)
    );
  }

  public PerkVolcanicSailor() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkVolcanicSailor(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }
}