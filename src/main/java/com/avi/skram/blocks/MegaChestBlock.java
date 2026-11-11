package com.avi.skram.blocks;

import com.avi.skram.blocks.entity.MegaChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class MegaChestBlock extends BaseEntityBlock {

  public static final String NAME = "mega_chest";

  public MegaChestBlock(Properties properties) {
    super(properties);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
    return new MegaChestBlockEntity(blockPos, blockState);
  }

  @Override
  protected @NonNull InteractionResult useWithoutItem(
    @NonNull BlockState state,
    @NonNull Level level,
    @NonNull BlockPos pos,
    @NonNull Player player,
    @NonNull BlockHitResult hitResult
  ) {
    if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MegaChestBlockEntity chest) {
      player.openMenu(chest);
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public boolean triggerEvent(
    @NonNull BlockState state,
    @NonNull Level level,
    @NonNull BlockPos pos,
    int id,
    int parameter
  ) {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    return blockEntity != null && blockEntity.triggerEvent(id, parameter);
  }

  @Override
  public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
    @NonNull Level level,
    @NonNull BlockState state,
    @NonNull BlockEntityType<T> type
  ) {
    return createTickerHelper(type, SkramBlocks.MEGA_CHEST_BLOCK_ENTITY.get(), MegaChestBlockEntity::tick);
  }
}
