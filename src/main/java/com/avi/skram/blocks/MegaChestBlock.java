package com.avi.skram.blocks;

import com.avi.skram.blocks.entity.MegaChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class MegaChestBlock extends BaseEntityBlock {

  public static final VoxelShape SHAPE = Block.column((double)14.0F, (double)0.0F, (double)14.0F);
  public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
  public static final String NAME = "mega_chest";

  public MegaChestBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  @Override
  public @Nullable BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  protected @NonNull BlockState rotate(@NonNull BlockState state, @NonNull Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  protected @NonNull BlockState mirror(@NonNull BlockState state, @NonNull Mirror mirror) {
    return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
    return new MegaChestBlockEntity(blockPos, blockState);
  }

  @Override
  protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
    return SHAPE;
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
