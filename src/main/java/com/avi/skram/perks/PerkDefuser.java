package com.avi.skram.perks;

import com.avi.skram.skills.SkillDemolition;
import com.avi.skram.skills.SkillMelee;
import com.avi.skram.skills.SkillRanged;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Map;

public class PerkDefuser extends Perk {
  public static final String NAME = "defuser";
  public static final String DESCRIPTION = "Dealing damage to a creeper defuses it.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillDemolition.NAME, 30)
    .put(SkillMelee.NAME, 20)
    .put(SkillRanged.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkDefuser> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkDefuser::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.TRIPWIRE_HOOK, 128),
      new ItemStack(Items.CREEPER_HEAD, 3)
    );
  }

  public PerkDefuser() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkDefuser(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

}
