package com.avi.skram.inventory;

import com.avi.skram.blocks.entity.MegaChestBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MegaChestMenu extends AbstractContainerMenu {

  public static final int COLUMNS = 23;
  public static final int ROWS = MegaChestBlockEntity.CONTAINER_SIZE / COLUMNS;
  private static final int CHEST_SLOT_COUNT = MegaChestBlockEntity.CONTAINER_SIZE;

  private final Container chest;

  public MegaChestMenu(int containerId, Inventory playerInventory) {
    this(containerId, playerInventory, new SimpleContainer(MegaChestBlockEntity.CONTAINER_SIZE));
  }

  public MegaChestMenu(int containerId, Inventory playerInventory, Container chest) {
    super(SkramMenus.MEGA_CHEST.get(), containerId);
    this.chest = chest;

    for (int row = 0; row < ROWS; ++row) {
      for (int column = 0; column < COLUMNS; ++column) {
        int slot = column + row * COLUMNS;
        this.addSlot(new Slot(chest, slot, 8 + column * 18, 27 + row * 18));
      }
    }

    this.addStandardInventorySlots(playerInventory, 134, 194);
    this.chest.startOpen(playerInventory.player);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slotIndex) {
    if (slotIndex < 0 || slotIndex >= this.slots.size()) return ItemStack.EMPTY;

    Slot slot = this.slots.get(slotIndex);
    if (!slot.hasItem()) return ItemStack.EMPTY;

    ItemStack original = slot.getItem();
    ItemStack moved = original.copy();

    if (slotIndex < CHEST_SLOT_COUNT) {
      if (!this.moveItemStackTo(original, CHEST_SLOT_COUNT, this.slots.size(), true)) {
        return ItemStack.EMPTY;
      }
    } else if (!this.insertIntoChest(original)) {
      return ItemStack.EMPTY;
    }

    if (original.isEmpty()) {
      slot.setByPlayer(ItemStack.EMPTY);
    } else {
      slot.setChanged();
    }
    slot.onTake(player, original);
    return moved;
  }

  private boolean insertIntoChest(ItemStack stack) {
    int originalCount = stack.getCount();

    for (int slot = 0; slot < this.chest.getContainerSize() && !stack.isEmpty(); ++slot) {
      ItemStack existing = this.chest.getItem(slot);
      if (existing.isEmpty() || !ItemStack.isSameItemSameComponents(existing, stack) || !this.chest.canPlaceItem(slot, stack)) {
        continue;
      }

      int maxCount = Math.min(this.chest.getMaxStackSize(stack), existing.getMaxStackSize());
      int transfer = Math.min(maxCount - existing.getCount(), stack.getCount());
      if (transfer > 0) {
        existing.grow(transfer);
        stack.shrink(transfer);
        this.chest.setChanged();
      }
    }

    for (int slot = 0; slot < this.chest.getContainerSize() && !stack.isEmpty(); ++slot) {
      if (!this.chest.getItem(slot).isEmpty() || !this.chest.canPlaceItem(slot, stack)) continue;

      int transfer = Math.min(this.chest.getMaxStackSize(stack), stack.getCount());
      this.chest.setItem(slot, stack.copyWithCount(transfer));
      stack.shrink(transfer);
    }

    return stack.getCount() < originalCount;
  }

  @Override
  public boolean clickMenuButton(Player player, int buttonId) {
    if (buttonId != 0 || !this.stillValid(player)) return false;
    if (this.chest instanceof MegaChestBlockEntity megaChest) {
      megaChest.sort();
      this.broadcastChanges();
    }
    return true;
  }

  @Override
  public boolean stillValid(Player player) {
    return this.chest.stillValid(player);
  }

  public boolean isFor(Container container) {
    return this.chest == container;
  }

  @Override
  public void removed(Player player) {
    super.removed(player);
    this.chest.stopOpen(player);
  }
}
