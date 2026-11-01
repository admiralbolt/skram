package com.avi.skram.item;

import com.avi.skram.Skram;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class SkramItems {

  public static DeferredItem<Item> AWESOME_SNAKE;

  public static void register() {
    AWESOME_SNAKE = Skram.ITEMS.registerSimpleItem("awesome_snake", p -> p.food(new FoodProperties.Builder()
      .alwaysEdible().nutrition(1).saturationModifier(2f).build()));
  }

}
