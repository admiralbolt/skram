package com.avi.skram.perks;

import com.avi.skram.Skram;
import com.avi.skram.skills.SkillHealth;
import com.avi.skram.skills.SkillSmithing;
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

public class PerkHeartOfTheForge extends Perk {

  public static final String NAME = "heart_of_the_forge";
  public static final String DESCRIPTION = "Reduces time spent burning by 50%";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillSmithing.NAME, 30)
    .put(SkillHealth.NAME, 20)
    .build();
  public static StreamCodec<ByteBuf, PerkHeartOfTheForge> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkHeartOfTheForge::new);

  public static final Identifier PERK_HEART_BURN_TIME = Identifier.fromNamespaceAndPath(Skram.MODID, "perk_heart_burn_time");

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.IRON_INGOT, 256),
      new ItemStack(Items.BLAZE_ROD, 128)
    );
  }

  @Override
  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();

    AttributeInstance burnTime = attributes.getInstance(Attributes.BURNING_TIME);
    if (burnTime == null) return;
    if (this.isFullyEnabled()) {
      burnTime.addOrReplacePermanentModifier(new AttributeModifier(
        PERK_HEART_BURN_TIME, -0.5, AttributeModifier.Operation.ADD_VALUE
      ));
    } else {
      burnTime.removeModifier(PERK_HEART_BURN_TIME);
    }
  }

  public PerkHeartOfTheForge() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkHeartOfTheForge(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

}
