package com.avi.skram.ui;

import com.avi.skram.utils.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class AddSkillButtonToInventory {

  public static boolean RECIPE_BOOK_OPEN = false;
  public static Button BUTTON_REFERENCE = null;

  public static boolean getRecipeBookOpen(InventoryScreen inventoryScreen) {
    try {
      Object bookComponent = ReflectionUtils.getFieldValue(inventoryScreen, "recipeBookComponent");
      RecipeBookComponent book = (RecipeBookComponent) bookComponent;
      return book.isVisible();
    } catch (NoSuchFieldException | IllegalAccessException e) {
      return false;
    }
  }

  public static void addButtonOnInit(ScreenEvent.Init.Post event) {
    if (!(event.getScreen() instanceof InventoryScreen inventoryScreen)) return;

    RECIPE_BOOK_OPEN = getRecipeBookOpen(inventoryScreen);

    Button customButton = Button.builder(Component.literal("Skram"), button -> {
        Minecraft.getInstance().setScreenAndShow(new SkillScreen());
      })
      .pos(inventoryScreen.getLeftPos() + 128, inventoryScreen.getTopPos() + 62)
      .size(40, 20)
      .build();

    BUTTON_REFERENCE = customButton;
    event.addListener(customButton);
  }

  public static void adjustButtonOnReRender(ScreenEvent.Render.Post event) {
    if (!(event.getScreen() instanceof InventoryScreen inventoryScreen)) return;

    // This could probably be checked in a better way, only getting updated when the recipe book is actually
    // clicked, but I can't figure out how to listen for such a thing.
    boolean recipeBookOpen = getRecipeBookOpen(inventoryScreen);
    if (recipeBookOpen == RECIPE_BOOK_OPEN) return;

    RECIPE_BOOK_OPEN = recipeBookOpen;
    // If we've made it this far the recipe book open HAS changed states.
    BUTTON_REFERENCE.setPosition(inventoryScreen.getLeftPos() + 128, inventoryScreen.getTopPos() + 62);
  }

}
