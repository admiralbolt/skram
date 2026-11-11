package com.avi.skram.inventory;

import com.avi.skram.Skram;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SkramMenus {

  private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Skram.MODID);
  public static final Supplier<MenuType<MegaChestMenu>> MEGA_CHEST = MENUS.register(
    "mega_chest",
    () -> IMenuTypeExtension.create((containerId, inventory, data) -> new MegaChestMenu(containerId, inventory))
  );

  public static void register(IEventBus modEventBus) {
    MENUS.register(modEventBus);
  }
}
