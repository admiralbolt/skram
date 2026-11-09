package com.avi.skram.perks;

import com.avi.skram.skills.SkillBotany;
import com.avi.skram.skills.SkillMercantile;
import com.avi.skram.skills.Skills;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Map;

public class PerkDebonair extends Perk {
  public static final String NAME = "debonair";
  public static final String DESCRIPTION = "Use flowers on villagers to increase your favor with them. Gain +1 favor for each 5 levels of Botany OR Mercantile you have.";
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

  public PerkDebonair() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkDebonair(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleEntityInteract(PlayerInteractEvent.EntityInteract event) {
    if (event.getLevel().isClientSide()) return;

    Entity entity = event.getTarget();
    if (!(entity instanceof Villager villager)) return;

    // Make sure we use some kind of flower.
    if (!event.getItemStack().is(BlockItemTags.FLOWERS.item())) return;

    // Make sure we have the perk in question.
    Player player = event.getEntity();
    PerkDebonair perk = player.getData(Perks.DEBONAIR);
    if (!perk.isFullyEnabled()) return;

    GossipContainer gossip = villager.getGossips();
    // Only add a minor positive gossip if we don't have one already.
    Object2IntMap<GossipType> playerGossipEntries = gossip.getGossipEntries().get(player.getUUID());
    if (playerGossipEntries != null && playerGossipEntries.containsKey(GossipType.MINOR_POSITIVE)) {
      // Can only be seduced once!
      ((ServerLevel) event.getLevel()).sendParticles(
        ParticleTypes.SMOKE,
        villager.getX(),
        villager.getY() + villager.getBbHeight() + 0.5,
        villager.getZ(),
        5,
        0.5, 0.5, 0.5,
        0.05
      );

      // Still taking that flower though >:)
      event.getItemStack().shrink(1);

      event.setCanceled(true);
      return;
    }

    SkillBotany botany = player.getData(Skills.BOTANY);
    SkillMercantile mercantile = player.getData(Skills.MERCANTILE);

    gossip.add(player.getUUID(), GossipType.MINOR_POSITIVE, (botany.level + mercantile.level) / 5);
    ((ServerLevel) event.getLevel()).sendParticles(
      ParticleTypes.HEART,
      villager.getX(),
      villager.getY() + villager.getBbHeight() + 0.5,
      villager.getZ(),
      10,
      0.5, 0.5, 0.5,
      0.05
    );

    // Damage the flower.
    event.getItemStack().shrink(1);

    // Add some XP
    botany.addXp(player, 30);
    mercantile.addXp(player, 30);

    // Finally, if it worked, we should cancel the event:
    event.setCanceled(true);
  }

}
