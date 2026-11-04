package com.avi.skram.perks;

import com.avi.skram.skills.SkillMercantile;
import com.avi.skram.skills.SkillStealth;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.List;
import java.util.Map;

public class PerkThievery extends Perk {
  public static final String NAME = "thievery";
  public static final String DESCRIPTION = "Enables you to steal emeralds from Villagers.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillStealth.NAME, 40)
    .put(SkillMercantile.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkThievery> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkThievery::new);

  public static List<ItemStack> getResearchCost() {
    ItemStack invis = PotionContents.createItemStack(Items.POTION, Potions.INVISIBILITY);

    return List.of(
      new ItemStack(Items.EMERALD, 64),
      invis
    );
  }

  public PerkThievery() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkThievery(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
