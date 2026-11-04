package com.avi.skram.perks;

import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.SkillMelee;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.List;
import java.util.Map;

public class PerkVampirism extends Perk {
  public static final String NAME = "vampirism";
  public static final String DESCRIPTION = "Whenever you kill a mob with a melee attack, restore 1/2 heart.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMelee.NAME, 40)
    .put(SkillMagic.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkVampirism> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkVampirism::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.DIAMOND_SWORD, 4),
      PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING)
    );
  }

  public PerkVampirism() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkVampirism(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
