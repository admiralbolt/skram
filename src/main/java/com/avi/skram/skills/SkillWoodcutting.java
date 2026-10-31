package com.avi.skram.skills;

import com.avi.skram.ui.GenericMessageToast;
import com.avi.skram.ui.ToastUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

import java.util.HashMap;
import java.util.Map;

public class SkillWoodcutting extends Skill {

  public static final String NAME = "woodcutting";
  public static final StreamCodec<ByteBuf, SkillWoodcutting> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillWoodcutting::new);
  public static Map<Block, Integer> XP_MAP;

  public static final int WOODCUTTING_SPEED_INDEX = 0;

  public SkillWoodcutting() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true};
  }

  public SkillWoodcutting(ByteBuf buffer) {
    super(buffer);
  }

  public static void setupXPMap() {
    XP_MAP = new HashMap<>();

    XP_MAP.put(Blocks.OAK_LOG, 60);

    XP_MAP.put(Blocks.ACACIA_LOG, 75);
    XP_MAP.put(Blocks.BIRCH_LOG, 75);
    XP_MAP.put(Blocks.CHERRY_LOG, 80);
    XP_MAP.put(Blocks.DARK_OAK_LOG, 75);
    XP_MAP.put(Blocks.JUNGLE_LOG, 75);
    XP_MAP.put(Blocks.MANGROVE_LOG, 75);
    XP_MAP.put(Blocks.SPRUCE_LOG, 75);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.WOODCUTTING, player.getData(Skills.WOODCUTTING));
  }

  public float getSpeedBonus() {
    return 0.03f * this.level;
  }

  public static void applySpeed(PlayerEvent.BreakSpeed event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_AXE)) return;

    SkillWoodcutting woodcutting = event.getEntity().getData(Skills.WOODCUTTING);
    if (!woodcutting.enabledAbilities[WOODCUTTING_SPEED_INDEX]) return;

    event.setNewSpeed(event.getOriginalSpeed() * (1 + woodcutting.getSpeedBonus()));
  }

  public static void gainXp(BreakBlockEvent event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_AXE) && !event.getState().is(BlockTags.LEAVES)) return;

    SkillWoodcutting woodcutting = event.getPlayer().getData(Skills.WOODCUTTING);
    woodcutting.addXp(event.getPlayer(), XP_MAP.getOrDefault(event.getState().getBlock(), 5));
  }

  public static void handleLeftClick(PlayerInteractEvent.LeftClickBlock event) {
    ItemStack usedItem = event.getItemStack();
    if (usedItem == ItemStack.EMPTY) return;
    if (!usedItem.is(ItemTags.AXES)) return;
    if (event.getEntity().isCreative()) return;

    SkillWoodcutting woodcutting = event.getEntity().getData(Skills.WOODCUTTING);
    int requiredLevel = Skills.requiredLevel(usedItem);
    if (woodcutting.level < requiredLevel) {
      event.setCanceled(true);
      if (event.getLevel().isClientSide()) {
        ToastManager toastManager = Minecraft.getInstance().gui.toastManager();
        if (!ToastUtils.hasGenericMessageToast(toastManager, woodcutting.getRequiredLevelToastName())) {
          toastManager.addToast(new GenericMessageToast(
            woodcutting.getRequiredLevelToastName(), Skills.SKILL_ICONS.getOrDefault(woodcutting.name, Skills.DEFAULT_ICON), "Using a " + usedItem.getItemName().getString() + " requires a " + SkillWoodcutting.NAME + " level of " + requiredLevel + "."
          ));
        }
      }
    }
  }

  public static void handleRightClick(PlayerInteractEvent.RightClickBlock event) {
    ItemStack usedItem = event.getItemStack();
    if (!usedItem.is(ItemTags.AXES)) return;

    SkillWoodcutting woodcutting = event.getEntity().getData(Skills.WOODCUTTING);
    woodcutting.requiredLevelCheck(event);

    // Otherwise, if we successfully scraped, waxed, or stripped, gain some xp.
    if (!event.isCanceled() && event.getLevel().isClientSide()) return;

    BlockPos pos = event.getHitVec().getBlockPos();
    BlockState state = event.getLevel().getBlockState(pos);

    // Removed weathering...
    if (state.getBlock() instanceof WeatheringCopper weatheringCopper) {
      WeatheringCopper.WeatherState weatherState = weatheringCopper.getAge();
      if (weatherState != WeatheringCopper.WeatherState.UNAFFECTED) {
        woodcutting.addXp(event.getEntity(), 5);
        return;
      }
    }

    // Stripped some logs...
    if (state.is(BlockTags.LOGS)) {
      woodcutting.addXp(event.getEntity(), 5);
      return;
    }

    // Wax OFF.
    if (state.getBlock().getName().toString().contains("waxed")) {
      woodcutting.addXp(event.getEntity(), 5);
      return;
    }
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Woodcutting Speed: +" + (int) (100 * this.getSpeedBonus()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[WOODCUTTING_SPEED_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(WOODCUTTING_SPEED_INDEX))
        .build()
    );

    return vertical;
  }

}