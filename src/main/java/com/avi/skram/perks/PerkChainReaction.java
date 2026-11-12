package com.avi.skram.perks;

import com.avi.skram.blocks.BlockData;
import com.avi.skram.blocks.PlayerOwnedBlocks;
import com.avi.skram.blocks.PlayerOwnedData;
import com.avi.skram.skills.SkillFarming;
import com.avi.skram.utils.ReflectionUtils;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class PerkChainReaction extends Perk {

  public static final String NAME = "chain_reaction";
  public static final String DESCRIPTION = "Whenever one of your crops grows, it has a 60% chance to randomly tick again.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillFarming.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkChainReaction> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkChainReaction::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.NETHERITE_HOE, 1),
      new ItemStack(Items.HAY_BLOCK, 256),
      new ItemStack(Items.CARROT, 256),
      new ItemStack(Items.POTATO, 256),
      new ItemStack(Items.BEETROOT, 256)
    );
  }

  public PerkChainReaction() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkChainReaction(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  // The CropGrowEvent.Post is fired after a crop has successfully grown.
  public static void handleCropGrowth(CropGrowEvent.Post event) {
    // If PerkChainReaction is fully enabled, roll an additional chance to multi tick crops.
    ServerLevel level = (ServerLevel) event.getLevel();
    BlockPos pos = event.getPos();
    BlockState state = event.getState();
    ChunkAccess chunk = level.getChunk(pos);
    PlayerOwnedBlocks pob = chunk.getData(BlockData.PLAYER_OWNED);
    PlayerOwnedData playerData = pob.playerOwnedBlocks.get(pos);
    if (playerData == null) return;

    Player owner = playerData.getOwner(level);
    if (owner == null) return;

    PerkChainReaction chainReaction = owner.getData(Perks.CHAIN_REACTION);
    if (!chainReaction.isFullyEnabled()) return;
    if (SkillFarming.isPlantFullyGrown(state)) return;
    if (Math.random() > 0.6) return;

    try {
      // Bamboo blocks are called with the base position.
      if (state.getBlock() == Blocks.BAMBOO || state.getBlock() == Blocks.BAMBOO_SAPLING) {
        pos = pos.above();
        state = level.getBlockState(pos);
      }
      ReflectionUtils.invokeMethod(state.getBlock(), "randomTick", state, level, pos, level.getRandom());
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      System.out.println("Can't invoke randomTick: " + e);
    }
  }

}