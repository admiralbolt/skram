package com.avi.skram.perks;

import com.avi.skram.skills.SkillBrewing;
import com.avi.skram.skills.SkillDemolition;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.SkillRanged;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Map;

public class PerkClusterBomb extends Perk {
  public static final String NAME = "cluster_bomb";
  public static final String DESCRIPTION = "Create 2 additional copies of any splash potion thrown.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillBrewing.NAME, 60)
    .put(SkillMagic.NAME, 40)
    .put(SkillRanged.NAME, 30)
    .put(SkillDemolition.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkClusterBomb> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkClusterBomb::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.POTION, 2),
      new ItemStack(Items.REDSTONE, 128),
      new ItemStack(Items.GLOWSTONE_DUST, 128),
      new ItemStack(Items.GUNPOWDER, 128)
    );
  }

  public PerkClusterBomb() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkClusterBomb(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static AbstractThrownPotion createSplashPotion(ServerLevel level, LivingEntity owner, ItemStack itemStack) {
    return new ThrownSplashPotion(level, owner, itemStack);
  }

  public static AbstractThrownPotion createLingeringPotion(ServerLevel level, LivingEntity owner, ItemStack itemStack) {
    return new ThrownLingeringPotion(level, owner, itemStack);
  }

  public static void handleItemRightClick(PlayerInteractEvent.RightClickItem event) {
    if (event.getLevel().isClientSide()) return;

    Player player = event.getEntity();
    PerkClusterBomb perk = player.getData(Perks.CLUSTER_BOMB);
    if (!perk.isFullyEnabled()) return;

    Item item = event.getItemStack().getItem();
    if (!(item instanceof ThrowablePotionItem)) return;

    PerkGrenadier grenadier = player.getData(Perks.GRENADIER);
    float powerMult = (grenadier.isFullyEnabled()) ? 2.0F : 1.0F;

    Projectile.ProjectileFactory<?> factory = (item instanceof LingeringPotionItem) ? PerkClusterBomb::createLingeringPotion : PerkClusterBomb::createSplashPotion;

    Projectile.spawnProjectileFromRotation(factory, (ServerLevel) event.getLevel(), event.getItemStack(), event.getEntity(), -20.0F, powerMult * (0.57F + (float) (0.05F * Math.random())), 2.0F);
    Projectile.spawnProjectileFromRotation(factory, (ServerLevel) event.getLevel(), event.getItemStack(), event.getEntity(), -20.0F, powerMult * (0.4F + (float) (0.05F * Math.random())), 2.0F);
  }
}
