package com.avi.skram.skills;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlocks;
import com.avi.skram.blocks.PlayerOwnedData;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillFarming extends Skill {

  public static final String NAME = "farming";
  public static final StreamCodec<ByteBuf, SkillFarming> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillFarming::new);
  public static final String DESCRIPTION = """
    Farm dem plants!
    
    Crop Growth Chance is complicated. Crops only have a chance to grow *if* they get randomly ticked. The median time for a random tick to occur for any particular block is ~48 seconds.
    Once randomly ticked, each crop has it's own percent chance to actually grow e.g. nether wart has a ~10% chance to grow.
    The additional growth chance is rolled separately, and growth is successful if either the original roll succeeds OR the additional roll succeeds.
    
    XP values for farming:
    """;
  public static Map<Block, Integer> XP_MAP;

  public static final int CROP_GROWTH_CHANCE_INDEX = 0;
  public static final int FARMING_FORTUNE_INDEX = 1;

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
    XP_MAP.put(Blocks.WHEAT, 60);
    XP_MAP.put(Blocks.CARROTS, 80);
    XP_MAP.put(Blocks.POTATOES, 80);
    XP_MAP.put(Blocks.BEETROOTS, 90);
    XP_MAP.put(Blocks.PUMPKIN, 110);
    XP_MAP.put(Blocks.MELON, 120);

    // Mushrooms.
    XP_MAP.put(Blocks.BROWN_MUSHROOM, 40);
    XP_MAP.put(Blocks.BROWN_MUSHROOM_BLOCK, 60);
    XP_MAP.put(Blocks.RED_MUSHROOM, 40);
    XP_MAP.put(Blocks.RED_MUSHROOM_BLOCK, 60);


    // Both a flower and a crop!
    XP_MAP.put(Blocks.PITCHER_CROP, 50);
    XP_MAP.put(Blocks.TORCHFLOWER_CROP, 50);

    // ???
    XP_MAP.put(Blocks.SWEET_BERRY_BUSH, 60);
    XP_MAP.put(Blocks.CHORUS_FLOWER, 60);
    XP_MAP.put(Blocks.COCOA, 40);

    // Nether!
    XP_MAP.put(Blocks.NETHER_WART, 90);
    XP_MAP.put(Blocks.CRIMSON_FUNGUS, 60);
    XP_MAP.put(Blocks.WARPED_FUNGUS, 80);

    // Tall.
    XP_MAP.put(Blocks.BAMBOO, 20);
    XP_MAP.put(Blocks.SUGAR_CANE, 10);

    // Weeds!
    XP_MAP.put(Blocks.SHORT_GRASS, 1);
    XP_MAP.put(Blocks.SHORT_DRY_GRASS, 1);
    XP_MAP.put(Blocks.TALL_GRASS, 2);
    XP_MAP.put(Blocks.TALL_DRY_GRASS, 2);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.FARMING, player.getData(Skills.FARMING));
  }

  public float getCropGrowthChance() {
    return 0.02f * this.level;
  }

  public double getFortuneChance() {
    return 0.02 * this.level;
  }

  public int getFortuneAmount() {
    return 1 + (this.level / 40);
  }

  public static boolean isGrowablePlant(BlockState state) {
    Block block = state.getBlock();
    return (block instanceof CropBlock || block instanceof NetherWartBlock || block instanceof PitcherCropBlock || block instanceof TorchflowerCropBlock || block instanceof CocoaBlock);
  }

  public static boolean isPlantFullyGrown(BlockState state) {
    Block block = state.getBlock();

    // Check for the AGE property on applicable types.
    if (block instanceof BeetrootBlock) return state.getValue(BeetrootBlock.AGE) == BeetrootBlock.MAX_AGE;
    if (block instanceof CropBlock) return state.getValue(CropBlock.AGE) == CropBlock.MAX_AGE;
    if (block instanceof NetherWartBlock) return state.getValue(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE;
    if (block instanceof PitcherCropBlock) return state.getValue(PitcherCropBlock.AGE) == PitcherCropBlock.MAX_AGE;
    if (block instanceof TorchflowerCropBlock) return state.getValue(TorchflowerCropBlock.AGE) == TorchflowerCropBlock.MAX_AGE;
    if (block instanceof CocoaBlock) return state.getValue(CocoaBlock.AGE) == CocoaBlock.MAX_AGE;

    return false;
  }

  public static void gainXp(BreakBlockEvent event) {
    Block block = event.getState().getBlock();
    int baseXp = XP_MAP.getOrDefault(block, 0);
    if (baseXp == 0) return;

    SkillFarming farming = event.getPlayer().getData(Skills.FARMING);
    farming.addXp(event.getPlayer(), baseXp);
  }

  public static void applyFortune(BlockDropsEvent event) {
    if (!XP_MAP.containsKey(event.getState().getBlock())) return;
    if (!(event.getBreaker() instanceof Player player)) return;

    SkillFarming farming = player.getData(Skills.FARMING);
    if (!farming.enabledAbilities[FARMING_FORTUNE_INDEX]) return;

    if (Math.random() > farming.getFortuneChance()) return;

    List<ItemEntity> drops = event.getDrops();
    // Probably unnecessary, but I want to make sure we double ALL drops.
    // Loop through each value just to be safe.
    int dropSize = drops.size();
    for (int i = 0; i < dropSize; ++i) {
      ItemEntity drop = drops.get(i);
      ItemStack dupItem = new ItemStack(drop.getItem().getItem(), drop.getItem().count());
      // Doing this as a loop to make N additional drops instead of 1 additional drop with N items.
      for (int j = 0; j < farming.getFortuneAmount(); ++j) {
        drops.add(new ItemEntity(drop.level(), drop.getX(), drop.getY(), drop.getZ(), dupItem));
      }
    }
  }

  public static void handleCropGrowth(CropGrowEvent.Pre event) {
    // If the plant was going to grow anyway, do nothing.
    if (event.getResult() == CropGrowEvent.Pre.Result.GROW) return;

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
  }

  public static void handleTallPlants(CropGrowEvent.Post event) {
    if (event.getLevel().isClientSide()) return;
    if (event.getState().getBlock() != Blocks.SUGAR_CANE && event.getState().getBlock() != Blocks.BAMBOO) return;

    boolean isSugarCane = event.getState().getBlock() == Blocks.SUGAR_CANE;
    // The events fire with slightly different positions (sadly).
    BlockPos below = (isSugarCane) ? event.getPos().below() : event.getPos();
    BlockPos newBlock = (isSugarCane) ? event.getPos() : event.getPos().above();

    // Copy the block data from below to above.
    ChunkAccess chunk = event.getLevel().getChunk(below);
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);
    PlayerOwnedData playerData = pob.playerOwnedBlocks.get(below);
    if (playerData == null) return;

    pob.playerOwnedBlocks.put(newBlock, playerData);
    chunk.markUnsaved();
  }

  public static void handleRightClick(PlayerInteractEvent.RightClickBlock event) {
    ItemStack usedItem = event.getItemStack();
    if (!usedItem.is(ItemTags.HOES)) return;

    SkillFarming farming = event.getEntity().getData(Skills.FARMING);
    farming.requiredLevelCheck(event);

    // Otherwise IF we successfully made farmland, gain some xp.
    if (event.isCanceled() || event.getLevel().isClientSide()) return;

    BlockPos pos = event.getHitVec().getBlockPos();
    BlockState state = event.getLevel().getBlockState(pos);
    if (!state.is(BlockTags.TURNS_INTO_FARMLAND)) return;

    farming.addXp(event.getEntity(), 10);
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(
      new AbilityDescription("Bonus Crop Growth Chance: " + (int) (100 * this.getCropGrowthChance()) + "%.", CROP_GROWTH_CHANCE_INDEX),
      new AbilityDescription("Fortune: " + (int) (100 * this.getFortuneChance()) + "% for " + (1 + this.getFortuneAmount()) + "x items.", FARMING_FORTUNE_INDEX)
    );
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }

  @Override
  public Map<Block, Integer> getBlockXpMap() {
    return XP_MAP;
  }
}