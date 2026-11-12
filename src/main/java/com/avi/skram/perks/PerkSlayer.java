package com.avi.skram.perks;

import com.avi.skram.skills.SkillMelee;
import com.avi.skram.skills.SkillRanged;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

import java.util.List;
import java.util.Map;

public class PerkSlayer extends Perk {
  public static final String NAME = "slayer";
  public static final String DESCRIPTION = "Gain +30% XP from slain mobs.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMelee.NAME, 30)
    .put(SkillRanged.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkSlayer> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkSlayer::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.ROTTEN_FLESH, 256),
      new ItemStack(Items.BONE, 128),
      new ItemStack(Items.GUNPOWDER, 64),
      new ItemStack(Items.WITHER_ROSE, 32)
    );
  }

  public PerkSlayer() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkSlayer(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void addMoreXP(LivingExperienceDropEvent event) {
    if (event.getAttackingPlayer() == null) return;
    if (event.getEntity().getType().getCategory().isFriendly()) return;

    PerkSlayer perk = event.getAttackingPlayer().getData(Perks.SLAYER);
    if (!perk.isFullyEnabled()) return;

    // Always add at least 1 additional XP.
    event.setDroppedExperience((int) Math.max(event.getDroppedExperience() + 1, 1.3 * event.getDroppedExperience()));
  }

}
