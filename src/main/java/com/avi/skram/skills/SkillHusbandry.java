package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
    return 0.01 * this.level;
  }

  public int getFortuneAmount() {
    return 1 + (this.level / 20);
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

    husbandry.addXp(player, 100);
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
    if (Math.random() > husbandry.getFortuneChance()) return;

    List<ItemEntity> baseDrops = new ArrayList<>(event.getDrops());
    // Probably unnecessary, but I want to make sure we double ALL drops.
    // Loop through each value just to be safe.
    int dropSize = baseDrops.size();
    for (int i = 0; i < dropSize; ++i) {
      ItemEntity drop = baseDrops.get(i);
      ItemStack dupItem = new ItemStack(drop.getItem().getItem(), drop.getItem().count());
      // Doing this as a loop to make N additional drops instead of 1 additional drop with N items.
      for (int j = 0; j < husbandry.getFortuneAmount(); ++j) {
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
    husbandry.addXp(player, 10);
  }

  public static void handleAnimalTamed(AnimalTameEvent event) {
    Player player = event.getTamer();
    if (player.level().isClientSide()) return;

    SkillHusbandry husbandry = player.getData(Skills.HUSBANDRY);
    husbandry.addXp(player, 150);
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
      husbandry.addXp(player, 15);
      return;
    }

    // Shear dem sheepsies.
    if (entity instanceof Sheep sheep) {
      if (event.getItemStack().getItem() != Items.SHEARS) return;
      if (!sheep.readyForShearing()) return;

      Player player = event.getEntity();
      SkillHusbandry husbandry = player.getData(Skills.HUSBANDRY);
      husbandry.addXp(player, 5);
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
    husbandry.addXp(player, 40);
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Breeding Bonus XP: +" + this.getBonusXPOnBreed() + "."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[HUSBANDRY_BREEDING_XP_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(HUSBANDRY_BREEDING_XP_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Fortune: " + (int) (100 * this.getFortuneChance()) + "% for " + (1 + this.getFortuneAmount()) + "x items."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[HUSBANDRY_FORTUNE_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(HUSBANDRY_FORTUNE_INDEX))
        .build()
    );

    return vertical;
  }
}