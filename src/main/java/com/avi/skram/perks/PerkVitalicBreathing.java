package com.avi.skram.perks;

import com.avi.skram.skills.SkillAthletics;
import com.avi.skram.skills.SkillDigging;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Map;

public class PerkVitalicBreathing extends Perk {
  public static final String NAME = "vitalic_breathing";
  public static final String DESCRIPTION = "No longer take suffocation damage from being trapped in walls.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillDigging.NAME, 30)
    .put(SkillAthletics.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkVitalicBreathing> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkVitalicBreathing::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.COPPER_SHOVEL, 9),
      new ItemStack(Items.SAND, 512),
      new ItemStack(Items.GRAVEL, 512)
    );
  }

  public PerkVitalicBreathing() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkVitalicBreathing(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (!event.getSource().is(DamageTypes.IN_WALL)) return;

    PerkVitalicBreathing perk = player.getData(Perks.VITALIC_BREATHING);
    if (!perk.isFullyEnabled()) return;

    event.setNewDamage(0);
  }


}
