package com.avi.skram.perks;

import com.avi.skram.skills.SkillFishing;
import com.avi.skram.skills.SkillSailing;
import com.avi.skram.utils.MinecraftUtils;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PerkTrawler extends Perk {
  public static final String NAME = "trawler";
  public static final String DESCRIPTION = "While sailing, your boat magnetizes and murders fish & squid.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillFishing.NAME, 25)
    .put(SkillSailing.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkTrawler> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkTrawler::new);

  public static final double PULL_RADIUS = 10;
  public static final double PULL_STRENGTH = 3;

  public static final double GRIND_RADIUS = 1.5;
  public static final float GRIND_DAMAGE = 20;

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.WOOL.white(), 256),
      new ItemStack(Items.GRINDSTONE, 64)
    );
  }

  public PerkTrawler() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkTrawler(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void murderFishies(PlayerTickEvent.Pre event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) return;
    if (player.getVehicle() == null || !(player.getVehicle() instanceof AbstractBoat)) return;

    PerkTrawler trawler = player.getData(Perks.TRAWLER);
    if (!trawler.isFullyEnabled()) return;

    // Get all abstract fish and/or squids in the general vicinity, and start pulling 'em.
    AABB hitBox = player.getHitbox().inflate(PULL_RADIUS);

    List<AbstractFish> entities = player.level().getEntitiesOfClass(AbstractFish.class, hitBox);
    List<Squid> squids = player.level().getEntitiesOfClass(Squid.class, hitBox);

    for (Mob mob : Stream.concat(entities.stream(), squids.stream()).toList()) {
      MinecraftUtils.applyPull(mob, player.position(), PULL_STRENGTH / mob.distanceTo(player));
    }

    AABB grindBox = player.getHitbox().inflate(GRIND_RADIUS);
    entities = player.level().getEntitiesOfClass(AbstractFish.class, grindBox);
    squids = player.level().getEntitiesOfClass(Squid.class, grindBox);

    for (Mob mob : Stream.concat(entities.stream(), squids.stream()).toList()) {
      mob.hurtServer((ServerLevel) player.level(), mob.damageSources().cramming(), GRIND_DAMAGE);
    }
  }
}
