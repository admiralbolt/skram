package com.avi.skram.skills;

import com.avi.skram.ui.GenericMessageToast;
import com.avi.skram.ui.ToastUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.List;

public class SkillMining extends Skill {

  public static final String NAME = "mining";
  public static final String REQUIRED_LEVEL_TOAST = "mining:req";

  public static final StreamCodec<ByteBuf, SkillMining> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillMining::new);

  public static final int MINING_SPEED_INDEX = 0;
  public static final int MINING_FORTUNE_INDEX = 1;

  public SkillMining() {
    super(NAME, 1, 0);

    // Abilities:
    //   0: Mining Speed.
    //   1: Fortune.
    this.enabledAbilities = new boolean[]{true, true};
  }

  public SkillMining(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.MINING, player.getData(Skills.MINING));
  }

  public float getSpeedBonus() {
    return 0.03f * this.level;
  }

  public double getFortuneChance() {
    return 0.01 * this.level;
  }

  public int getFortuneAmount() {
    return 1 + (this.level / 40);
  }

  public static void applySpeed(PlayerEvent.BreakSpeed event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_PICKAXE)) return;

    SkillMining mining = event.getEntity().getData(Skills.MINING);
    if (!mining.enabledAbilities[MINING_SPEED_INDEX]) return;

    event.setNewSpeed(event.getOriginalSpeed() * (1 + mining.getSpeedBonus()));
  }

  public static void gainXp(BreakBlockEvent event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_PICKAXE)) return;

    SkillMining mining = event.getPlayer().getData(Skills.MINING);
    mining.addXp(event.getPlayer(), 50000);
  }

  public static void applyFortune(BlockDropsEvent event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_PICKAXE) || !event.getState().is(BlockTags.ORES)) return;
    if (!(event.getBreaker() instanceof Player player)) return;

    SkillMining mining = player.getData(Skills.MINING);
    if (!mining.enabledAbilities[MINING_FORTUNE_INDEX]) return;
    if (Math.random() > mining.getFortuneChance()) return;

    List<ItemEntity> drops = event.getDrops();
    // Probably unnecessary, but I want to make sure we double ALL drops.
    // Loop through each value just to be safe.
    int dropSize = drops.size();
    for (int i = 0; i < dropSize; ++i) {
      ItemEntity drop = drops.get(i);
      ItemStack dupItem = new ItemStack(drop.getItem().getItem(), drop.getItem().count());
      // Doing this as a loop to make N additional drops instead of 1 additional drop with N items.
      for (int j = 0; j < mining.getFortuneAmount(); ++j) {
        drops.add(new ItemEntity(drop.level(), drop.getX(), drop.getY(), drop.getZ(), dupItem));
      }
    }
  }

  public static void handleLeftClick(PlayerInteractEvent.LeftClickBlock event) {
    ItemStack usedItem = event.getItemStack();
    if (usedItem == ItemStack.EMPTY) return;
    if (!usedItem.is(ItemTags.PICKAXES)) return;

    SkillMining mining = event.getEntity().getData(Skills.MINING);
    int requiredLevel = Skills.requiredLevel(usedItem);
    if (mining.level < requiredLevel) {
      event.setCanceled(true);
      if (event.getLevel().isClientSide()) {
        ToastManager toastManager = Minecraft.getInstance().gui.toastManager();
        if (!ToastUtils.hasGenericMessageToast(toastManager, REQUIRED_LEVEL_TOAST)) {
          toastManager.addToast(new GenericMessageToast(
            REQUIRED_LEVEL_TOAST, Skills.SKILL_ICONS.getOrDefault(mining.name, Skills.DEFAULT_ICON), "Using a " + usedItem.getItemName().getString() + " requires a " + SkillMining.NAME + " level of " + requiredLevel + "."
          ));
        }
      }
    }
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Mining Speed: +" + (int) (100 * this.getSpeedBonus()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[MINING_SPEED_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(MINING_SPEED_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Fortune: " + (int) (100 * this.getFortuneChance()) + "% for " + (1 + this.getFortuneAmount()) + "x items."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[MINING_SPEED_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(MINING_SPEED_INDEX))
        .build()
    );

    return vertical;
  }
}