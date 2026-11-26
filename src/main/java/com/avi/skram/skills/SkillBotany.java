package com.avi.skram.skills;

import com.avi.skram.utils.MathUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.List;

public class SkillBotany extends Skill {

  public static final String NAME = "botany";
  public static final StreamCodec<ByteBuf, SkillBotany> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillBotany::new);
  public static final String DESCRIPTION = """
    Gain XP by breaking flowers!
    
    Base XP rates are:
     - Tall flowers: 400
     - Small flowers: 200
     - Other flowers: 30
    """;

  public static final int BOTANY_FORTUNE_INDEX = 0;

  public SkillBotany() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true};
  }

  public SkillBotany(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.BOTANY, player.getData(Skills.BOTANY));
  }

  public double getFortuneChance() {
    return 0.03 * this.level;
  }

  public static void gainXp(BreakBlockEvent event) {
    BlockState state = event.getState();
    if (!state.is(BlockTags.FLOWERS)) return;

    int xp = 30;
    // We want to give more XP for tall flowers and small flowers.
    if (state.is(Tags.BlockItems.FLOWERS_TALL.block())) {
      xp = 400;
    } else if (state.is(Tags.BlockItems.FLOWERS_SMALL.block())) {
      xp = 200;
    }

    SkillBotany botany = event.getPlayer().getData(Skills.BOTANY);
    botany.addXp(event.getPlayer(), xp);
  }

  public static void applyFortune(BlockDropsEvent event) {
    if (!event.getState().is(BlockTags.FLOWERS)) return;
    if (!(event.getBreaker() instanceof Player player)) return;

    SkillBotany botany = player.getData(Skills.BOTANY);
    if (!botany.enabledAbilities[BOTANY_FORTUNE_INDEX]) return;

    int fortuneAmount = MathUtils.getScalingRandom(botany.getFortuneChance());
    if (fortuneAmount == 1) return;

    List<ItemEntity> drops = event.getDrops();
    // Probably unnecessary, but I want to make sure we double ALL drops.
    // Loop through each value just to be safe.
    int dropSize = drops.size();
    for (int i = 0; i < dropSize; ++i) {
      ItemEntity drop = drops.get(i);
      ItemStack dupItem = new ItemStack(drop.getItem().getItem(), drop.getItem().count());
      // Doing this as a loop to make N additional drops instead of 1 additional drop with N items.
      for (int j = 0; j < fortuneAmount - 1; ++j) {
        drops.add(new ItemEntity(drop.level(), drop.getX(), drop.getY(), drop.getZ(), dupItem));
      }
    }
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(
      new AbilityDescription(MathUtils.formatFortune(this.getFortuneChance()), BOTANY_FORTUNE_INDEX)
    );
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}