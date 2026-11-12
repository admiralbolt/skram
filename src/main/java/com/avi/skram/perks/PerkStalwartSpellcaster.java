package com.avi.skram.perks;

import com.avi.skram.skills.SkillHealth;
import com.avi.skram.skills.SkillMagic;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkStalwartSpellcaster extends Perk {

  public static final String NAME = "stalwart_spellcaster";
  public static final String DESCRIPTION = "Taking damage no longer interrupts spellcasting.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMagic.NAME, 15)
    .put(SkillHealth.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkStalwartSpellcaster> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkStalwartSpellcaster::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.SHIELD, 10),
      new ItemStack(Items.LAPIS_LAZULI, 128)
    );
  }

  public PerkStalwartSpellcaster() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkStalwartSpellcaster(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

}
