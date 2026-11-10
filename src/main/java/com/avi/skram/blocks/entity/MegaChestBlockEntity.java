package com.avi.skram.blocks.entity;

import com.avi.skram.blocks.SkramBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MegaChestBlockEntity extends BaseContainerBlockEntity {

  public static final String NAME = "mega_chest_block_entity";

  public MegaChestBlockEntity(BlockPos worldPosition, BlockState blockState) {
    super(SkramBlocks.MEGA_CHEST_BLOCK_ENTITY.get(), worldPosition, blockState);
  }

  @Override
  protected Component getDefaultName() {
    return null;
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return null;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> nonNullList) {

  }

  @Override
  protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
    return null;
  }

  @Override
  public int getContainerSize() {
    return 0;
  }
}
