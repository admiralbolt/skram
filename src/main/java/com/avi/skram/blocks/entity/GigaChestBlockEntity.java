package com.avi.skram.blocks.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.avi.skram.blocks.SkramBlocks;
import com.avi.skram.inventory.GigaChestMenu;
import com.avi.skram.inventory.GigaChestTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
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

public class GigaChestBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {

  public static final String NAME = "giga_chest_block_entity";
  private static final Codec<StoredItem> STORED_ITEM_CODEC = RecordCodecBuilder.create(instance -> instance.group(
    Codec.INT.fieldOf("Slot").forGetter(StoredItem::slot),
    ItemStack.MAP_CODEC.forGetter(StoredItem::stack)
  ).apply(instance, StoredItem::new));
  private NonNullList<ItemStack> items = NonNullList.withSize(GigaChestTabs.CONTAINER_SIZE, ItemStack.EMPTY);
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
      return player.containerMenu instanceof GigaChestMenu menu && menu.isFor(GigaChestBlockEntity.this);
    }
  };

  public GigaChestBlockEntity(BlockPos worldPosition, BlockState blockState) {
    super(SkramBlocks.GIGA_CHEST_BLOCK_ENTITY.get(), worldPosition, blockState);
  }

  private static void playChestSound(Level level, BlockPos pos, SoundEvent sound) {
    level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
  }

  public static void tick(Level level, BlockPos pos, BlockState state, GigaChestBlockEntity blockEntity) {
    if (!level.isClientSide()) {
      blockEntity.openersCounter.recheckOpeners(level, pos, state);
    }

    blockEntity.lidController.shouldBeOpen(blockEntity.openCount > 0);
    blockEntity.lidController.tickLid();
  }

  @Override
  protected Component getDefaultName() {
    return Component.translatable("container.giga_chest");
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return this.items;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> items) {
    this.items = NonNullList.withSize(GigaChestTabs.CONTAINER_SIZE, ItemStack.EMPTY);
    for (int slot = 0; slot < Math.min(items.size(), GigaChestTabs.CONTAINER_SIZE); ++slot) {
      this.items.set(slot, items.get(slot));
    }
  }

  @Override
  protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
    super.loadAdditional(input);
    this.items = NonNullList.withSize(GigaChestTabs.CONTAINER_SIZE, ItemStack.EMPTY);
    for (StoredItem item : input.listOrEmpty(ContainerHelper.TAG_ITEMS, STORED_ITEM_CODEC)) {
      if (item.slot() >= 0 && item.slot() < this.items.size()) {
        this.items.set(item.slot(), item.stack());
      }
    }
  }

  @Override
  protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
    super.saveAdditional(output);
    net.minecraft.world.level.storage.ValueOutput.TypedOutputList<StoredItem> savedItems =
      output.list(ContainerHelper.TAG_ITEMS, STORED_ITEM_CODEC);
    for (int slot = 0; slot < this.items.size(); ++slot) {
      ItemStack stack = this.items.get(slot);
      if (!stack.isEmpty()) {
        savedItems.add(new StoredItem(slot, stack));
      }
    }
  }

  @Override
  protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
    return new GigaChestMenu(containerId, inventory, this);
  }

  @Override
  public int getContainerSize() {
    return GigaChestTabs.CONTAINER_SIZE;
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    return slot >= 0 && slot < GigaChestTabs.CONTAINER_SIZE;
  }

  public void sortAllTabs(Player player) {
    if (this.level == null || this.level.isClientSide()) return;

    GigaChestTabs.rebuildDisplayItems(player);
    List<List<ItemStack>> tabItems = new ArrayList<>(GigaChestTabs.TABS.size());
    for (int tabIndex = 0; tabIndex < GigaChestTabs.TABS.size(); ++tabIndex) {
      tabItems.add(new ArrayList<>());
    }

    for (int slot = 0; slot < this.items.size(); ++slot) {
      ItemStack stack = this.items.get(slot);
      if (stack.isEmpty()) continue;

      int destinationTab = GigaChestTabs.findTabIndex(stack);
      tabItems.get(destinationTab).add(stack.copy());
    }

    for (int tabIndex = 0; tabIndex < tabItems.size(); ++tabIndex) {
      tabItems.set(tabIndex, sortAndCompact(tabItems.get(tabIndex)));
    }

    List<ItemStack> overflow = new ArrayList<>();
    for (List<ItemStack> itemsInTab : tabItems) {
      if (itemsInTab.size() <= GigaChestTabs.SLOTS_PER_TAB) continue;
      overflow.addAll(itemsInTab.subList(GigaChestTabs.SLOTS_PER_TAB, itemsInTab.size()));
      itemsInTab.subList(GigaChestTabs.SLOTS_PER_TAB, itemsInTab.size()).clear();
    }

    for (ItemStack stack : overflow) {
      List<ItemStack> availableTab = tabItems.stream()
        .filter(itemsInTab -> itemsInTab.size() < GigaChestTabs.SLOTS_PER_TAB)
        .min(Comparator.comparingInt(List::size))
        .orElseThrow(() -> new IllegalStateException("Giga Chest overflow exceeds total capacity"));
      availableTab.add(stack);
    }

    NonNullList<ItemStack> sortedItems = NonNullList.withSize(GigaChestTabs.CONTAINER_SIZE, ItemStack.EMPTY);
    for (int tabIndex = 0; tabIndex < tabItems.size(); ++tabIndex) {
      List<ItemStack> compacted = sortAndCompact(tabItems.get(tabIndex));
      int start = tabIndex * GigaChestTabs.SLOTS_PER_TAB;
      for (int slot = 0; slot < compacted.size(); ++slot) {
        sortedItems.set(start + slot, compacted.get(slot));
      }
    }

    this.items = sortedItems;
    this.setChanged();
  }

  private static List<ItemStack> sortAndCompact(List<ItemStack> items) {
    List<ItemStack> sorted = items.stream()
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

    return compacted;
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

  private record StoredItem(int slot, ItemStack stack) {
  }
}
