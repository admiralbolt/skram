package com.avi.skram.perks;

import com.avi.skram.skills.SkillMelee;
import com.avi.skram.skills.SkillMercantile;
import com.avi.skram.skills.SkillSailing;
import com.avi.skram.skills.Skills;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Map;

public class PerkPirateering extends Perk {
  public static final String NAME = "pirateering";
  public static final String DESCRIPTION = "Damaging villagers increases your favor with them.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillSailing.NAME, 20)
    .put(SkillMercantile.NAME, 15)
    .put(SkillMelee.NAME, 15)
    .build();
  public static final StreamCodec<ByteBuf, PerkPirateering> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkPirateering::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.BURIED_TREASURE_MAP, 1),
      new ItemStack(Items.GLASS_BOTTLE, 1),
      new ItemStack(Items.PAPER, 1),
      new ItemStack(Items.IRON_SWORD, 1),
      new ItemStack(Items.OAK_BOAT, 1)
    );
  }

  public PerkPirateering() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkPirateering(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleDamageEvent(LivingDamageEvent.Post event) {
    if (!(event.getSource().getDirectEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;
    if (!(event.getEntity() instanceof Villager villager)) return;

    PerkPirateering perk = player.getData(Perks.PIRATEERING);
    if (!perk.isFullyEnabled()) return;

    GossipContainer gossip = villager.getGossips();
    // Remove ALL negatives gossips from the map!
    gossip.remove(player.getUUID(), GossipType.MINOR_NEGATIVE);
    gossip.remove(player.getUUID(), GossipType.MAJOR_NEGATIVE);

    gossip.add(player.getUUID(), GossipType.MINOR_POSITIVE, (int) (event.getInflictedDamage() * 2));
    ((ServerLevel) player.level()).sendParticles(
      ParticleTypes.ASH,
      villager.getX(),
      villager.getY() + villager.getBbHeight() + 0.5,
      villager.getZ(),
      10,
      0.5, 0.5, 0.5,
      0.05
    );

    SkillMelee melee = player.getData(Skills.MELEE);
    SkillMercantile mercantile = player.getData(Skills.MERCANTILE);
    SkillSailing sailing = player.getData(Skills.SAILING);

    melee.addXp(player, 10);
    mercantile.addXp(player, 15);
    sailing.addXp(player, 15);
  }
}
