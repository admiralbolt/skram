package com.avi.skram.perks;

import com.avi.skram.skills.SkillRanged;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Map;

public class PerkSniper extends Perk {
  public static final String NAME = "sniper";
  public static final String DESCRIPTION = "If you are >=30 blocks away from your target, deal +50% damage.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillRanged.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkSniper> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkSniper::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.SPYGLASS, 12),
      new ItemStack(Items.CROSSBOW, 12)
    );
  }

  public PerkSniper() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkSniper(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    if (!(event.getSource().getDirectEntity() instanceof Projectile)) return;
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    PerkSniper perk = player.getData(Perks.SNIPER);
    if (!perk.isFullyEnabled()) return;

    double dist = event.getEntity().distanceTo(player);
    if (dist < 30) return;

    event.setNewDamage(event.getNewDamage() * 1.5f);
  }
}
