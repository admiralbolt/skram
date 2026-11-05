package com.avi.skram.ui;

import com.avi.skram.Skram;
import com.avi.skram.utils.MathUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.GuiLayer;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class ManaBar {

  public static Identifier SPELl_UI = Identifier.fromNamespaceAndPath(Skram.MODID, "spell_ui");

  public static ManaBarGuiLayer layer = new ManaBarGuiLayer();

  public static double currentMana = 1;
  public static double maxMana = 1;
  public static Color foregroundColor = new Color(255, 255, 255);

  public static void updateMana(double newCurrentMana, double newMaxMana) {
    currentMana = newCurrentMana;
    maxMana = newMaxMana;
  }

  public static void updateMana(double newCurrentMana) {
    currentMana = newCurrentMana;
  }

  public static class ManaBarGuiLayer implements GuiLayer {

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
      int baseY = 180;
      int baseX = 20;

      graphics.text(
        Minecraft.getInstance().font,
        Component.literal(String.format("%.1f / %d", currentMana, (int) maxMana)),
        baseX,
        baseY,
        MathUtils.getColorValue(foregroundColor)
      );

    }
  }
}
