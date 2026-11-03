package com.avi.skram.perks;

import com.avi.skram.skills.SkillAthletics;
import com.avi.skram.skills.SkillHealth;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Map;

public class PerkJuggernaut extends Perk {

  public static final String NAME = "juggernaut";
  public static final String DESCRIPTION = "While sprinting, take 50% less damage.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillAthletics.NAME, 30)
    .put(SkillHealth.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkJuggernaut> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkJuggernaut::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.GOLDEN_BOOTS, 10),
      new ItemStack(Items.COPPER_HELMET, 10),
      new ItemStack(Items.STONE_BRICK_WALL, 256)
    );
  }

  public PerkJuggernaut() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkJuggernaut(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (!player.isSprinting()) return;

    PerkJuggernaut perk = player.getData(Perks.JUGGERNAUT);
    if (!perk.isFullyEnabled()) return;

    event.setNewDamage(event.getNewDamage() * 0.5f);
  }

}