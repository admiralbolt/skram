package com.avi.skram.perks;

import com.avi.skram.Skram;
import com.avi.skram.skills.SkillDigging;
import com.avi.skram.skills.SkillMining;
import com.avi.skram.skills.SkillSailing;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkDeepSeaDredger extends Perk {
  public static final String NAME = "deep_sea_dredger";
  public static final String DESCRIPTION = "You no longer have a speed penalty when mining underwater.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillSailing.NAME, 20)
    .put(SkillMining.NAME, 15)
    .put(SkillDigging.NAME, 15)
    .build();
  public static final StreamCodec<ByteBuf, PerkDeepSeaDredger> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkDeepSeaDredger::new);

  public static final Identifier PERK_DEEP_SEA_DREDGER = Identifier.fromNamespaceAndPath(Skram.MODID, "perk_deep_sea_dredger");

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.WATER_BUCKET, 6),
      new ItemStack(Items.WET_SPONGE, 12),
      new ItemStack(Items.DIAMOND_SHOVEL, 1),
      new ItemStack(Items.DIAMOND_PICKAXE, 1)
    );
  }

  public PerkDeepSeaDredger() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkDeepSeaDredger(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  @Override
  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();

    AttributeInstance submergedMiningSpeed = attributes.getInstance(Attributes.SUBMERGED_MINING_SPEED);
    if (submergedMiningSpeed == null) return;
    if (this.isFullyEnabled()) {
      submergedMiningSpeed.addOrReplacePermanentModifier(new AttributeModifier(
        PERK_DEEP_SEA_DREDGER, 0.8, AttributeModifier.Operation.ADD_VALUE
      ));
    } else {
      submergedMiningSpeed.removeModifier(PERK_DEEP_SEA_DREDGER);
    }
  }
}
