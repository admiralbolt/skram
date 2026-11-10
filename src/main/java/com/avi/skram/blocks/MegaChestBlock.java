package com.avi.skram.blocks;

import com.avi.skram.blocks.entity.MegaChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
}
