package com.avi.skram.perks;

import com.avi.skram.skills.SkillHusbandry;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;

import java.util.List;
import java.util.Map;

public class PerkGemini extends Perk {
  public static final String NAME = "gemini";
  public static final String DESCRIPTION = "Whenever you breed animals, breed twins.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillHusbandry.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkGemini> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkGemini::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.ECHO_SHARD, 2)
    );
  }

  public PerkGemini() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkGemini(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleBreedEvent(BabyEntitySpawnEvent event) {
    if (event.getCausedByPlayer() == null) return;

    Player player = event.getCausedByPlayer();
    if (player.level().isClientSide()) return;

    PerkGemini perkGemini = player.getData(Perks.GEMINI);
    if (!perkGemini.isFullyEnabled()) return;

    AgeableMob offspring = event.getChild();
    if (offspring == null) return;

    Animal primary = (Animal) event.getParentA();
    Animal partner = (Animal) event.getParentB();
    ServerLevel level = (ServerLevel) primary.level();

    Animal newBaby = (Animal) offspring.getType().create(level, EntitySpawnReason.BREEDING);
    newBaby.setBaby(true);
    newBaby.snapTo(primary.getX(), primary.getY(), primary.getZ(), 0.0F, 0.0F);
    primary.finalizeSpawnChildFromBreeding(level, partner, newBaby);
    level.addFreshEntityWithPassengers(newBaby);
  }

}
