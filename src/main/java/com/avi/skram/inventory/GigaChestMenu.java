package com.avi.skram.inventory;

import com.avi.skram.blocks.entity.GigaChestBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GigaChestMenu extends AbstractContainerMenu {

  public static final int COLUMNS = 23;
  public static final int ROWS = GigaChestTabs.SLOTS_PER_TAB / COLUMNS;
  private static final int CHEST_SLOT_COUNT = GigaChestTabs.SLOTS_PER_TAB;
  private static final int SORT_BUTTON_ID = GigaChestTabs.TABS.size();

  private final Container chest;
  private final TabContainer tabContainer;
  private int selectedTab;

  public GigaChestMenu(int containerId, Inventory playerInventory) {
    this(containerId, playerInventory, new SimpleContainer(GigaChestTabs.CONTAINER_SIZE));
  }

  public GigaChestMenu(int containerId, Inventory playerInventory, Container chest) {
    super(SkramMenus.GIGA_CHEST.get(), containerId);
    this.chest = chest;
    this.tabContainer = new TabContainer(chest);

    for (int row = 0; row < ROWS; ++row) {
      for (int column = 0; column < COLUMNS; ++column) {
        int slot = column + row * COLUMNS;
        this.addSlot(new Slot(this.tabContainer, slot, 8 + column * 18, 27 + row * 18));
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

    for (int slot = 0; slot < this.tabContainer.getContainerSize() && !stack.isEmpty(); ++slot) {
      ItemStack existing = this.tabContainer.getItem(slot);
      if (existing.isEmpty() || !ItemStack.isSameItemSameComponents(existing, stack) || !this.tabContainer.canPlaceItem(slot, stack)) {
        continue;
      }

      int maxCount = Math.min(this.tabContainer.getMaxStackSize(stack), existing.getMaxStackSize());
      int transfer = Math.min(maxCount - existing.getCount(), stack.getCount());
      if (transfer > 0) {
        existing.grow(transfer);
        stack.shrink(transfer);
        this.tabContainer.setChanged();
      }
    }

    for (int slot = 0; slot < this.tabContainer.getContainerSize() && !stack.isEmpty(); ++slot) {
      if (!this.tabContainer.getItem(slot).isEmpty() || !this.tabContainer.canPlaceItem(slot, stack)) continue;

      int transfer = Math.min(this.tabContainer.getMaxStackSize(stack), stack.getCount());
      this.tabContainer.setItem(slot, stack.copyWithCount(transfer));
      stack.shrink(transfer);
    }

    return stack.getCount() < originalCount;
  }

  @Override
  public boolean clickMenuButton(Player player, int buttonId) {
    if (!this.stillValid(player)) return false;
    if (buttonId >= 0 && buttonId < GigaChestTabs.TABS.size()) {
      this.setSelectedTab(buttonId);
      this.broadcastChanges();
      return true;
    }
    if (buttonId == SORT_BUTTON_ID && this.chest instanceof GigaChestBlockEntity gigaChest) {
      gigaChest.sortAllTabs(player);
      this.broadcastChanges();
      return true;
    }
    return false;
  }

  public void setSelectedTab(int tabIndex) {
    if (tabIndex < 0 || tabIndex >= GigaChestTabs.TABS.size()) return;
    this.selectedTab = tabIndex;
    this.tabContainer.setTab(tabIndex);
  }

  public int getSelectedTab() {
    return this.selectedTab;
  }

  public int getTabCount() {
    return GigaChestTabs.TABS.size();
  }

  public int getSortButtonId() {
    return SORT_BUTTON_ID;
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

  private static class TabContainer extends SimpleContainer {
    private final Container backing;
    private int tab;

    private TabContainer(Container backing) {
      super(GigaChestTabs.SLOTS_PER_TAB);
      this.backing = backing;
    }

    private void setTab(int tab) {
      this.tab = tab;
    }

    private int toBackingIndex(int slot) {
      if (slot < 0 || slot >= GigaChestTabs.SLOTS_PER_TAB) {
        throw new IndexOutOfBoundsException("Slot out of range: " + slot);
      }
      return this.tab * GigaChestTabs.SLOTS_PER_TAB + slot;
    }

    @Override
    public boolean isEmpty() {
      for (int slot = 0; slot < GigaChestTabs.SLOTS_PER_TAB; ++slot) {
        if (!this.getItem(slot).isEmpty()) return false;
      }
      return true;
    }

    @Override
    public ItemStack getItem(int slot) {
      return this.backing.getItem(this.toBackingIndex(slot));
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
      return this.backing.removeItem(this.toBackingIndex(slot), amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
      return this.backing.removeItemNoUpdate(this.toBackingIndex(slot));
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
      this.backing.setItem(this.toBackingIndex(slot), stack);
    }

    @Override
    public void setChanged() {
      this.backing.setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
      return this.backing.canPlaceItem(this.toBackingIndex(slot), stack);
    }

    @Override
    public boolean stillValid(Player player) {
      return this.backing.stillValid(player);
    }

    @Override
    public void clearContent() {
      for (int slot = 0; slot < GigaChestTabs.SLOTS_PER_TAB; ++slot) {
        this.setItem(slot, ItemStack.EMPTY);
      }
    }
  }
}
