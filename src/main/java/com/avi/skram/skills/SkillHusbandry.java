package com.avi.skram.skills;

import com.avi.skram.utils.MathUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class SkillHusbandry extends Skill {

  public static final String NAME = "husbandry";
  public static final StreamCodec<ByteBuf, SkillHusbandry> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillHusbandry::new);
  public static final String DESCRIPTION = """
    Its own skill!!!
    
    You get husbandry XP for a wide variety of animal related activities, including:
     - Breeding: 220xp.
     - Taming: 350xp.
     - Gathering Honey: 100xp.
     - Getting Milk: 40xp.
     - Shearing Sheep: 40xp.
     - Harvesting Animals: 30xp.
    """;

  public static final int HUSBANDRY_BREEDING_XP_INDEX = 0;
  public static final int HUSBANDRY_FORTUNE_INDEX = 1;

  public SkillHusbandry() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true};
  }

  public SkillHusbandry(ByteBuf buffer) {
    super(buffer);
  }

  public int getBonusXPOnBreed() {
    return 2 + (this.level / 4);
  }

  public double getFortuneChance() {
    return 0.025 * this.level;
  }

  public static void handleBreedEvent(BabyEntitySpawnEvent event) {
    if (event.getCausedByPlayer() == null) return;

    Player player = event.getCausedByPlayer();
    if (player.level().isClientSide()) return;

    SkillHusbandry husbandry = player.getData(Skills.HUSBANDRY);
    if (husbandry.enabledAbilities[HUSBANDRY_BREEDING_XP_INDEX]) {
      ExperienceOrb orb = new ExperienceOrb(player.level(), player.getX(), player.getY(), player.getZ(), husbandry.getBonusXPOnBreed());
      player.level().addFreshEntity(orb);
    }

    husbandry.addXp(player, 220);
  }

  public static void applyFortune(LivingDropsEvent event) {
    Player player = null;
    if (event.getSource().getEntity() instanceof Player) {
      player = (Player) event.getSource().getEntity();
    } else if (event.getSource().getDirectEntity() instanceof Player) {
      player = (Player) event.getSource().getDirectEntity();
    } else {
      return;
    }

    if (player.level().isClientSide()) return;
    if (!(event.getEntity() instanceof Animal)) return;

    SkillHusbandry husbandry = player.getData(Skills.HUSBANDRY);
    if (!husbandry.enabledAbilities[HUSBANDRY_FORTUNE_INDEX]) return;

    int fortuneAmount = MathUtils.getScalingRandom(husbandry.getFortuneChance());
    if (fortuneAmount == 0) return;

    List<ItemEntity> baseDrops = new ArrayList<>(event.getDrops());
    // Probably unnecessary, but I want to make sure we double ALL drops.
    // Loop through each value just to be safe.
    int dropSize = baseDrops.size();
    for (int i = 0; i < dropSize; ++i) {
      ItemEntity drop = baseDrops.get(i);
      ItemStack dupItem = new ItemStack(drop.getItem().getItem(), drop.getItem().count());
      // Doing this as a loop to make N additional drops instead of 1 additional drop with N items.
      for (int j = 0; j < fortuneAmount; ++j) {
        event.getDrops().add(new ItemEntity(drop.level(), drop.getX(), drop.getY(), drop.getZ(), dupItem));
      }
    }
  }

  public static void handleAnimalKilled(LivingDeathEvent event) {
    Player player = null;
    if (event.getSource().getEntity() instanceof Player) {
      player = (Player) event.getSource().getEntity();
    } else if (event.getSource().getDirectEntity() instanceof Player) {
      player = (Player) event.getSource().getDirectEntity();
    } else {
      return;
    }

    if (player.level().isClientSide()) return;
    if (!(event.getEntity() instanceof Animal)) return;

    SkillHusbandry husbandry = player.getData(Skills.HUSBANDRY);
    husbandry.addXp(player, 30);
  }

  public static void handleAnimalTamed(AnimalTameEvent event) {
    Player player = event.getTamer();
    if (player.level().isClientSide()) return;

    SkillHusbandry husbandry = player.getData(Skills.HUSBANDRY);
    husbandry.addXp(player, 350);
  }

  public static void handleEntityInteract(PlayerInteractEvent.EntityInteract event) {
    if (event.getLevel().isClientSide()) return;
    // We can get milk from cows, mooshrooms, and goats.
    Entity entity = event.getTarget();
    if (entity instanceof Cow || entity instanceof MushroomCow || entity instanceof Goat) {
      // We can only get milk when using a bucket.
      if (event.getItemStack().getItem() != Items.BUCKET) return;

      Player player = event.getEntity();
      SkillHusbandry husbandry = player.getData(Skills.HUSBANDRY);
      husbandry.addXp(player, 40);
      return;
    }

    // Shear dem sheepsies.
    if (entity instanceof Sheep sheep) {
      if (event.getItemStack().getItem() != Items.SHEARS) return;
      if (!sheep.readyForShearing()) return;

      Player player = event.getEntity();
      SkillHusbandry husbandry = player.getData(Skills.HUSBANDRY);
      husbandry.addXp(player, 40);
    }

  }

  public static void handleBlockInteract(PlayerInteractEvent.RightClickBlock event) {
    if (!event.isCanceled() && event.getLevel().isClientSide()) return;

    ItemStack usedItem = event.getItemStack();
    if (usedItem.getItem() != Items.GLASS_BOTTLE) return;

    BlockPos pos = event.getPos();
    BlockState state = event.getLevel().getBlockState(pos);
    if (!state.is(BlockTags.BEEHIVES)) return;

    if (state.getValue(BeehiveBlock.HONEY_LEVEL) < 5) return;

    Player player = event.getEntity();
    SkillHusbandry husbandry = player.getData(Skills.HUSBANDRY);
    husbandry.addXp(player, 100);
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(
      new AbilityDescription("Breeding Bonus XP: +" + this.getBonusXPOnBreed() + ".", HUSBANDRY_BREEDING_XP_INDEX),
      new AbilityDescription(MathUtils.formatFortune(this.getFortuneChance()), HUSBANDRY_FORTUNE_INDEX)
    );
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}