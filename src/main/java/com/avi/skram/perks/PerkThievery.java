package com.avi.skram.perks;

import com.avi.skram.attachments.MiscAttachments;
import com.avi.skram.attachments.SkramEntityModifier;
import com.avi.skram.skills.SkillMercantile;
import com.avi.skram.skills.SkillStealth;
import com.avi.skram.skills.Skills;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Map;

public class PerkThievery extends Perk {

  public static final int COOLDOWN_MINUTES = 30;
  public static final int COOLDOWN_TICKS = 30 * 60 * 20;

  public static final String NAME = "thievery";
  public static final String DESCRIPTION = "Enables you to steal emeralds from Villagers. Cooldown of " + COOLDOWN_MINUTES + " minutes per villager.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillStealth.NAME, 30)
    .put(SkillMercantile.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkThievery> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkThievery::new);

  public static List<ItemStack> getResearchCost() {
    ItemStack invis = PotionContents.createItemStack(Items.POTION, Potions.INVISIBILITY);

    return List.of(
      new ItemStack(Items.EMERALD, 64),
      invis
    );
  }

  public PerkThievery() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkThievery(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleEntityInteract(PlayerInteractEvent.EntityInteract event) {
    if (event.isCanceled()) return;
    if (event.getLevel().isClientSide()) return;

    Entity entity = event.getTarget();
    if (!(entity instanceof Villager villager)) return;
    // Hand should be empty.
    if (!event.getItemStack().isEmpty()) return;

    Player player = event.getEntity();
    if (!player.isDiscrete()) return;

    PerkThievery perk = player.getData(Perks.THIEVERY);
    if (!perk.isFullyEnabled()) return;

    // We always cancel the event, even if we don't steal successfully.
    event.setCanceled(true);

    SkramEntityModifier modifier = villager.getData(MiscAttachments.SKRAM_ENTITY_MODIFIER);
    long currentTime = player.level().getGameTime();
    if ((currentTime - modifier.lastStealTime) < COOLDOWN_TICKS) {
      ((ServerLevel) event.getLevel()).sendParticles(
        ParticleTypes.SMOKE,
        villager.getX(),
        villager.getY() + villager.getBbHeight() + 0.5,
        villager.getZ(),
        5,
        0.5, 0.5, 0.5,
        0.05
      );
      return;
    }

    SkillStealth stealth = player.getData(Skills.STEALTH);
    player.getInventory().add(new ItemStack(Items.EMERALD, 1 + (stealth.level / 50)));
    stealth.addXp(player, 50);

    // Attach our entity modifier data.
    modifier.lastStealTime = currentTime;
    villager.setData(MiscAttachments.SKRAM_ENTITY_MODIFIER, modifier);

    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.7f, 0.25f);
  }

}
