package com.avi.skram.perks;

import com.avi.skram.skills.SkillBotany;
import com.avi.skram.skills.SkillMercantile;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkDebonair extends Perk {
  public static final String NAME = "debonair";
  public static final String DESCRIPTION = "Use flowers on villagers to increase your favor with them.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMercantile.NAME, 40)
    .put(SkillBotany.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkDebonair> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkDebonair::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.EMERALD, 64),
      new ItemStack(Items.POPPY, 640)
    );
  }

  public PerkDebonair() { super(NAME, DESCRIPTION, REQUIRED_SKILLS); }
  public PerkDebonair(ByteBuf buffer) { super(buffer, DESCRIPTION, REQUIRED_SKILLS); }
}
