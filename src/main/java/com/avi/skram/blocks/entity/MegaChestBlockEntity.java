package com.avi.skram.blocks.entity;

import com.avi.skram.blocks.SkramBlocks;
import com.avi.skram.inventory.MegaChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MegaChestBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {

  public static final String NAME = "mega_chest_block_entity";
  public static final int CONTAINER_SIZE = 207;
  private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
  private final ChestLidController lidController = new ChestLidController();
  private int openCount;

  private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
    @Override
    protected void onOpen(Level level, BlockPos pos, BlockState state) {
      playChestSound(level, pos, SoundEvents.CHEST_OPEN);
    }

    @Override
    protected void onClose(Level level, BlockPos pos, BlockState state) {
      playChestSound(level, pos, SoundEvents.CHEST_CLOSE);
    }

    @Override
    protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
      level.blockEvent(pos, state.getBlock(), 1, newCount);
    }

    @Override
    public boolean isOwnContainer(Player player) {
      return player.containerMenu instanceof MegaChestMenu menu && menu.isFor(MegaChestBlockEntity.this);
    }
  };

  public MegaChestBlockEntity(BlockPos worldPosition, BlockState blockState) {
    super(SkramBlocks.MEGA_CHEST_BLOCK_ENTITY.get(), worldPosition, blockState);
  }

  private static void playChestSound(Level level, BlockPos pos, net.minecraft.sounds.SoundEvent sound) {
    level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
  }

  public static void tick(Level level, BlockPos pos, BlockState state, MegaChestBlockEntity blockEntity) {
    if (!level.isClientSide()) {
      blockEntity.openersCounter.recheckOpeners(level, pos, state);
    }

    blockEntity.lidController.shouldBeOpen(blockEntity.openCount > 0);
    blockEntity.lidController.tickLid();
  }

  @Override
  protected Component getDefaultName() {
    return Component.translatable("container.mega_chest");
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return this.items;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> items) {
    this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    for (int slot = 0; slot < Math.min(items.size(), CONTAINER_SIZE); ++slot) {
      this.items.set(slot, items.get(slot));
    }
  }

  @Override
  protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
    super.loadAdditional(input);
    ContainerHelper.loadAllItems(input, this.items);
  }

  @Override
  protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
    super.saveAdditional(output);
    ContainerHelper.saveAllItems(output, this.items);
  }

  @Override
  protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
    return new MegaChestMenu(containerId, inventory, this);
  }

  @Override
  public int getContainerSize() {
    return CONTAINER_SIZE;
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    return slot >= 0 && slot < CONTAINER_SIZE;
  }

  public void sort() {
    List<ItemStack> sorted = this.items.stream()
      .filter(stack -> !stack.isEmpty())
      .map(ItemStack::copy)
      .sorted(Comparator.comparing(stack -> stack.getHoverName().getString(), String.CASE_INSENSITIVE_ORDER))
      .toList();
    List<ItemStack> compacted = new ArrayList<>();

    for (ItemStack source : sorted) {
      for (ItemStack target : compacted) {
        if (!ItemStack.isSameItemSameComponents(source, target)) continue;

        int transfer = Math.min(target.getMaxStackSize() - target.getCount(), source.getCount());
        if (transfer > 0) {
          target.grow(transfer);
          source.shrink(transfer);
        }
        if (source.isEmpty()) break;
      }

      while (!source.isEmpty()) {
        int stackSize = Math.min(source.getCount(), source.getMaxStackSize());
        compacted.add(source.copyWithCount(stackSize));
        source.shrink(stackSize);
      }
    }

    this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    for (int slot = 0; slot < compacted.size(); ++slot) {
      this.items.set(slot, compacted.get(slot));
    }
    this.setChanged();
  }

  @Override
  public void startOpen(ContainerUser user) {
    if (user instanceof Player player && this.level != null && !this.level.isClientSide()) {
      this.openersCounter.incrementOpeners(player, this.level, this.worldPosition, this.getBlockState(), 4.0);
    }
  }

  @Override
  public void stopOpen(ContainerUser user) {
    if (user instanceof Player player && this.level != null && !this.level.isClientSide()) {
      this.openersCounter.decrementOpeners(player, this.level, this.worldPosition, this.getBlockState());
    }
  }

  @Override
  public boolean triggerEvent(int id, int parameter) {
    if (id == 1) {
      this.openCount = parameter;
      return true;
    }
    return super.triggerEvent(id, parameter);
  }

  @Override
  public float getOpenNess(float partialTick) {
    return this.lidController.getOpenness(partialTick);
  }
}
