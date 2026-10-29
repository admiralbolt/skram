package com.avi.skram.skills;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlocks;
import com.avi.skram.blocks.PlayerOwnedData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

import java.util.HashMap;
import java.util.Map;

public class SkillFarming extends Skill {

  public static final String NAME = "farming";
  public static final StreamCodec<ByteBuf, SkillFarming> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillFarming::new);
  public static Map<Block, Integer> XP_MAP;

  public static final int CROP_GROWTH_CHANCE_INDEX = 0;
  public static final int DOUBLE_CROP_CHANCE_INDEX = 1;

  public SkillFarming() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true};
  }

  public SkillFarming(ByteBuf buffer) {
    super(buffer);
  }

  public static void setupXPMap() {
    XP_MAP = new HashMap<>();

    // "Regular" crops.
    XP_MAP.put(Blocks.WHEAT, 25);
    XP_MAP.put(Blocks.CARROTS, 30);
    XP_MAP.put(Blocks.POTATOES, 30);
    XP_MAP.put(Blocks.BEETROOTS, 40);
    XP_MAP.put(Blocks.PUMPKIN, 50);
    XP_MAP.put(Blocks.MELON, 60);

    // Mushrooms.
    XP_MAP.put(Blocks.BROWN_MUSHROOM, 35);
    XP_MAP.put(Blocks.RED_MUSHROOM, 35);

    // Both a flower and a crop!
    XP_MAP.put(Blocks.PITCHER_CROP, 30);
    XP_MAP.put(Blocks.TORCHFLOWER_CROP, 30);

    // ???
    XP_MAP.put(Blocks.SWEET_BERRY_BUSH, 20);
    XP_MAP.put(Blocks.CAVE_VINES_PLANT, 20);
    XP_MAP.put(Blocks.CHORUS_FLOWER, 20);
    XP_MAP.put(Blocks.COCOA, 30);

    // Nether!
    XP_MAP.put(Blocks.NETHER_WART, 50);
    XP_MAP.put(Blocks.CRIMSON_FUNGUS, 30);
    XP_MAP.put(Blocks.WARPED_FUNGUS, 40);

    // Tall.
    XP_MAP.put(Blocks.BAMBOO, 10);
    XP_MAP.put(Blocks.SUGAR_CANE, 10);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.FARMING, player.getData(Skills.FARMING));
  }

  public float getCropGrowthChance() {
    return 1f * this.level;
  }

  public double getDoubleCropChance() {
    return 0.01 * this.level;
  }

  public static void handleCropGrowth(CropGrowEvent.Pre event) {
    // If the plant was going to grow anyway, do nothing.
    if (event.getResult() == CropGrowEvent.Pre.Result.GROW) return;

    // Saving this temporarily, it was a way of triggering randomTick() again!
//    System.out.println("Crop (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "): " + state.getBlock());


    ServerLevel level = (ServerLevel) event.getLevel();
    BlockPos pos = event.getPos();
    ChunkAccess chunk = level.getChunk(pos);
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);
    PlayerOwnedData playerData = pob.playerOwnedBlocks.get(pos);
    if (playerData == null) return;

    Player owner = playerData.getOwner((ServerLevel) level);
    float growthChance = playerData.growthChance;

    if (owner != null) {
      SkillFarming farming = owner.getData(Skills.FARMING);
      if (farming.enabledAbilities[CROP_GROWTH_CHANCE_INDEX]) {
        growthChance = farming.getCropGrowthChance();
      }
    }

    // Roll an additional chance to grow based on the block states growth chance.
    if (Math.random() < growthChance) {
      event.setResult(CropGrowEvent.Pre.Result.GROW);
    }

    // Roll an additional chance to multi tick crops.
//    if (Math.random() < 0.6) {
//      try {
//        System.out.println("  Invoking randomTick()!");
//        ReflectionUtils.invokeMethod(state.getBlock(), "randomTick", state, level, pos, random);
//        System.out.println("  Success!");
//      } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//        System.out.println("Can't invoke randomTick: " + e);
//      }
//    }

  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Bonus Crop Growth Chance: " + (int) (100 * this.getCropGrowthChance()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[CROP_GROWTH_CHANCE_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(CROP_GROWTH_CHANCE_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Double Crop Chance: " + (int) (100 * this.getDoubleCropChance()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[DOUBLE_CROP_CHANCE_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(DOUBLE_CROP_CHANCE_INDEX))
        .build()
    );

    return vertical;
  }
}