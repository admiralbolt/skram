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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.HashMap;
import java.util.Map;

public class SkillDigging extends Skill {

  public static final String NAME = "digging";
  public static final StreamCodec<ByteBuf, SkillDigging> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillDigging::new);
  public static Map<Block, Integer> XP_MAP;

  public static final int DIGGING_SPEED_INDEX = 0;

  public SkillDigging() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true};
  }

  public SkillDigging(ByteBuf buffer) {
    super(buffer);
  }

  public static void setupXPMap() {
    XP_MAP = new HashMap<>();

    XP_MAP.put(Blocks.DIRT, 30);
    XP_MAP.put(Blocks.GRASS_BLOCK, 40);
    XP_MAP.put(Blocks.COARSE_DIRT, 40);
    XP_MAP.put(Blocks.FARMLAND, 40);
    XP_MAP.put(Blocks.SAND, 35);
    XP_MAP.put(Blocks.GRAVEL, 50);
    XP_MAP.put(Blocks.RED_SAND, 45);
    XP_MAP.put(Blocks.ROOTED_DIRT, 85);
    XP_MAP.put(Blocks.MUD, 50);
    XP_MAP.put(Blocks.CLAY, 60);
    XP_MAP.put(Blocks.PODZOL, 70);
    XP_MAP.put(Blocks.MYCELIUM, 120);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.DIGGING, player.getData(Skills.DIGGING));
  }

  public float getSpeedBonus() {
    return 0.03f * this.level;
  }

  public static void applySpeed(PlayerEvent.BreakSpeed event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_SHOVEL)) return;

    SkillDigging digging = event.getEntity().getData(Skills.DIGGING);
    if (!digging.enabledAbilities[DIGGING_SPEED_INDEX]) return;

    event.setNewSpeed(event.getOriginalSpeed() * (1 + digging.getSpeedBonus()));
  }

  public static void gainXp(BreakBlockEvent event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_SHOVEL)) return;

    SkillDigging digging = event.getPlayer().getData(Skills.DIGGING);
    digging.addXp(event.getPlayer(), XP_MAP.getOrDefault(event.getState().getBlock(), 5));
  }

  public static void handleLeftClick(PlayerInteractEvent.LeftClickBlock event) {
    ItemStack usedItem = event.getItemStack();
    if (!usedItem.is(ItemTags.SHOVELS)) return;

    SkillDigging digging = event.getEntity().getData(Skills.DIGGING);
    digging.requiredLevelCheck(event);
  }

  public static void handleRightClick(PlayerInteractEvent.RightClickBlock event) {
    ItemStack usedItem = event.getItemStack();
    if (!usedItem.is(ItemTags.SHOVELS)) return;

    SkillDigging digging = event.getEntity().getData(Skills.DIGGING);
    digging.requiredLevelCheck(event);

    // Otherwise IF we successfully made a dirt path, gain some xp.
    if (event.isCanceled() || event.getLevel().isClientSide()) return;

    BlockPos pos = event.getHitVec().getBlockPos();
    BlockState state = event.getLevel().getBlockState(pos);
    if (!state.is(BlockTags.TURNS_INTO_DIRT_PATH)) return;

    digging.addXp(event.getEntity(), 5);
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Digging Speed: +" + (int) (100 * this.getSpeedBonus()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[DIGGING_SPEED_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(DIGGING_SPEED_INDEX))
        .build()
    );

    return vertical;
  }

}
