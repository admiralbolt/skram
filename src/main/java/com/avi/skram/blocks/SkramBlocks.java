package com.avi.skram.blocks;

import com.avi.skram.Skram;
import com.avi.skram.blocks.entity.GigaChestBlockEntity;
import com.avi.skram.blocks.entity.MegaChestBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

public class SkramBlocks {

  public static DeferredBlock<GigaChestBlock> GIGA_CHEST;
  public static DeferredItem<BlockItem> GIGA_CHEST_ITEM;
  public static DeferredBlock<MegaChestBlock> MEGA_CHEST;
  public static DeferredItem<BlockItem> MEGA_CHEST_ITEM;

  public static Supplier<BlockEntityType<GigaChestBlockEntity>> GIGA_CHEST_BLOCK_ENTITY;
  public static Supplier<BlockEntityType<MegaChestBlockEntity>> MEGA_CHEST_BLOCK_ENTITY;

  public static void registerBlocks() {
    GIGA_CHEST = Skram.BLOCKS.register(
      GigaChestBlock.NAME, (registryName) -> new GigaChestBlock(
        BlockBehaviour.Properties.of()
          .destroyTime(12.0f)
          .explosionResistance(1200.0f)
          .noOcclusion()
          .sound(SoundType.WOOD)
          .setId(ResourceKey.create(Registries.BLOCK, registryName))
      )
    );
    GIGA_CHEST_ITEM = Skram.ITEMS.registerSimpleBlockItem(GIGA_CHEST);

    MEGA_CHEST = Skram.BLOCKS.register(
      MegaChestBlock.NAME, (registryName) -> new MegaChestBlock(
        BlockBehaviour.Properties.of()
          .destroyTime(6.0f)
          .explosionResistance(50.0f)
          .noOcclusion()
          .sound(SoundType.WOOD)
          .setId(ResourceKey.create(Registries.BLOCK, registryName))
      )
    );

    MEGA_CHEST_ITEM = Skram.ITEMS.registerSimpleBlockItem(MEGA_CHEST);
  }

  public static void registerBlockEntities() {
    GIGA_CHEST_BLOCK_ENTITY = Skram.BLOCK_ENTITY_TYPES.register(
      GigaChestBlockEntity.NAME, () -> new BlockEntityType<>(
        GigaChestBlockEntity::new,
        false,
        GIGA_CHEST.get()
      )
    );

    MEGA_CHEST_BLOCK_ENTITY = Skram.BLOCK_ENTITY_TYPES.register(
      MegaChestBlockEntity.NAME, () -> new BlockEntityType<>(
        MegaChestBlockEntity::new,
        false,
        MEGA_CHEST.get()
      )
    );
  }


}
