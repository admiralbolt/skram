package com.avi.skram.perks;

import com.avi.skram.entity.projectile.ThrownFlower;
import com.avi.skram.skills.SkillBotany;
import com.avi.skram.skills.SkillRanged;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Map;

public class PerkThornSpitter extends Perk {
  public static final String NAME = "thorn_spitter";
  public static final String DESCRIPTION = "Right click flowers to throw them.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillBotany.NAME, 30)
    .put(SkillRanged.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkThornSpitter> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkThornSpitter::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.DANDELION, 128),
      new ItemStack(Items.AZURE_BLUET, 128),
      new ItemStack(Items.ORANGE_TULIP, 64)
    );
  }

  public PerkThornSpitter() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkThornSpitter(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void throwFlower(PlayerInteractEvent.RightClickItem event) {
    if (event.getLevel().isClientSide()) return;

    ItemStack stack = event.getItemStack();
    if (!stack.is(Tags.BlockItems.FLOWERS_SMALL.item()) && !stack.is(Tags.BlockItems.FLOWERS_TALL.item())) return;

    Player player = event.getEntity();
    PerkThornSpitter perk = player.getData(Perks.THORN_SPITTER);
    if (!perk.isFullyEnabled()) return;

    Projectile.spawnProjectileFromRotation(ThrownFlower::new, (ServerLevel) event.getLevel(), event.getItemStack(), event.getEntity(), -10.0F, 0.9f, 0.0F);
  }


}
