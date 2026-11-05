package com.avi.skram.ui;

import com.avi.skram.Skram;
import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.spells.Spells;
import com.avi.skram.utils.MathUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.GuiLayer;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.Objects;

public class SpellUI {

  public static Identifier SPELl_UI = Identifier.fromNamespaceAndPath(Skram.MODID, "spell_ui");

  public static ManaBarGuiLayer layer = new ManaBarGuiLayer();

  public static double maxMana = 1;
  public static SpellCastingInfo castingInfo;
  public static String spellName;
  public static Identifier spellIcon;

  public static Color foregroundColor = new Color(255, 255, 255, 200);

  public static void update(SpellCastingInfo newCastingInfo, double newMaxMana) {
    castingInfo = newCastingInfo;
    if (!Objects.equals(castingInfo.equippedSpellName, spellName)) {
      spellName = castingInfo.equippedSpellName;
      spellIcon = Spells.SPELLS_BY_NAME.get(spellName).spellIcon;
    }
    maxMana = newMaxMana;
  }

  public static class ManaBarGuiLayer implements GuiLayer {

    public static Color MANA_BAR_BACKGROUND_COLOR = new Color(20, 40, 80, 255);
    public static Color MANA_BAR_FILL_COLOR = new Color(50, 100, 180, 255);
    public static Color MANA_TEXT_COLOR = new Color(200, 200, 255, 240);
    public static Color MANA_TEXT_FAILED_COLOR = new Color(220, 150, 150, 240);

    public static int manaBarBackgroundColor = MathUtils.getColorValue(MANA_BAR_BACKGROUND_COLOR);
    public static int manaBarFillColor = MathUtils.getColorValue(MANA_BAR_FILL_COLOR);
    public static int manaTextColor = MathUtils.getColorValue(MANA_TEXT_COLOR);
    public static int manaTextFailedColor = MathUtils.getColorValue(MANA_TEXT_FAILED_COLOR);

    public static int offsetFromBottom = 20;
    public static int offsetFromLeft = 5;
    public static int iconSize = 12;

    public static int manaBarPaddingLeft = 2;
    public static int manaBarHeight = 10;
    public static int manaBarWidth = 80;

    public static float textScale = 0.6f;

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
      int baseY = graphics.guiHeight() - offsetFromBottom;
      int baseX = offsetFromLeft;

      graphics.blit(RenderPipelines.GUI_TEXTURED, spellIcon, baseX, baseY, 0, 0, iconSize, iconSize, iconSize, iconSize);

      baseX = baseX + iconSize + manaBarPaddingLeft;
      baseY = baseY + (iconSize - manaBarHeight) / 2;

      double percentFull = castingInfo.currentMana / maxMana;
      graphics.fill(baseX, baseY, baseX + manaBarWidth, baseY + manaBarHeight, manaBarBackgroundColor);
      graphics.fill(baseX, baseY, baseX + (int) (percentFull * manaBarWidth), baseY + manaBarHeight, manaBarFillColor);

      graphics.pose().pushMatrix();
      graphics.pose().scale(textScale);

      graphics.text(
        Minecraft.getInstance().font,
        Component.literal(String.format("%.1f / %d", castingInfo.currentMana, (int) maxMana)),
        5 + (int) ((baseX + iconSize) / textScale),
        1 + (int) (baseY / textScale) + (int) (manaBarHeight / (4 * textScale)),
        (castingInfo.failedCasting) ? manaTextFailedColor : manaTextColor
      );

      graphics.pose().popMatrix();

      // If we are currently casting, we want to render circular progress around our reticle.
      // Unfortunately rendering a circular progress bar is too difficult for both me AND AI.
      renderIsCasting(graphics, deltaTracker);
    }

    public static int castingWidth = 18;
    public static int castingHeight = 1;
    public static int verticalOffset = 6;

    public static Color CASTING_INDICATOR_COLOR = new Color(60, 140, 240, 255);
    public static int castingIndicatorColor = MathUtils.getColorValue(CASTING_INDICATOR_COLOR);

    public static void renderIsCasting(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
      if (!castingInfo.isCasting) return;

      int baseX = (graphics.guiWidth() / 2) - (castingWidth / 2);
      int baseY = (graphics.guiHeight() / 2) + verticalOffset;

      float progress = 1.0f - ((float) castingInfo.castingTimer / Spells.SPELLS_BY_NAME.get(castingInfo.spellName).castingTimeTicks);

      graphics.fill(baseX, baseY, baseX + castingWidth, baseY + castingHeight, manaBarBackgroundColor);
      graphics.fill(baseX, baseY, baseX + (int) (progress * castingWidth), baseY + castingHeight, castingIndicatorColor);
    }
  }
}
