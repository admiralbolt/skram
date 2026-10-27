package com.avi.skram.ui;

import com.avi.skram.skills.Skills;
import com.avi.skram.utils.MathUtils;
import com.avi.skram.utils.ReflectionUtils;
import com.avi.skram.utils.StringUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.GuiLayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XPGainGui {

  public static int TIME_UNTIL_FADE_MS = 1000;
  public static int FADE_TIME_MS = 600;
  public static int TOTAL_TIME_MS = TIME_UNTIL_FADE_MS + FADE_TIME_MS;

  public static XPGainGuiLayer layer = new XPGainGuiLayer();
  public static List<XPGainContainer> notifications = new ArrayList<>();
  public static Map<String, Integer> notificationsBySkill = new HashMap<>();

  public static void notify(String skillName, double xp) {
    // If this is not a skill currently in our set of notifications, we should add it.
    int index = notificationsBySkill.getOrDefault(skillName, -1);
    if (index == -1) {
      notifications.add(new XPGainContainer(skillName, xp, TOTAL_TIME_MS));
      notificationsBySkill.put(skillName, notifications.size() - 1);
      return;
    }

    // Otherwise, refresh the duration, and increment xp.
    XPGainContainer container = notifications.get(index);
    container.totalXp += xp;
    container.currentTicks = TOTAL_TIME_MS;
  }

  public static class XPGainContainer {

    public String skillName;
    public double totalXp;
    public float currentTicks;

    public XPGainContainer(String skillName, double totalXp, float currentTicks) {
      this.skillName = skillName;
      this.totalXp = totalXp;
      this.currentTicks = currentTicks;
    }

  }

  public static class XPGainGuiLayer implements GuiLayer {

    public static float SCALE = 0.5f;
    public static float ADDITIONAL_ICON_SCALE = 0.8f;
    public static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/advancement");

    public static final int PADDING_LEFT = 3;
    public static final int PADDING_TOP = 3;
    public static final int PADDING_BETWEEN = 4;
    public static final int TEXT_OFFSET_TOP = 4;
    public static final int TEXT_OFFSET_LEFT = 17;
    public static final int ICON_OFFSET_TOP = 2;
    public static final int ICON_OFFSET_LEFT = 3;
    public static final int BANNER_WIDTH = 120;
    public static final int BANNER_HEIGHT = 16;


    @Override
    public void render(@NotNull GuiGraphicsExtractor graphics, @NotNull DeltaTracker deltaTracker) {
      // No notifications means there's nothing to render.
      if (notifications.isEmpty()) return;

      // Otherwise, iterate through our notifications and render them.
      graphics.pose().pushMatrix();
      graphics.pose().scale(SCALE);
      List<Integer> deletionIndices = new ArrayList<>();
      for (int i = 0; i < notifications.size(); ++i) {
        XPGainContainer xpGain = notifications.get(i);

        int baseY = PADDING_TOP + (PADDING_BETWEEN + BANNER_HEIGHT) * i;
        Color backgroundColor = new Color(255, 255, 255, (int) MathUtils.linearRescale(xpGain.currentTicks, 0, FADE_TIME_MS, 0, 128));
        Color foregroundColor = new Color(255, 255, 255, (int) MathUtils.linearRescale(xpGain.currentTicks, 0, FADE_TIME_MS, 0, 255));

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, PADDING_LEFT, baseY, BANNER_WIDTH, BANNER_HEIGHT, MathUtils.getColorValue(backgroundColor));

        graphics.pose().scale(ADDITIONAL_ICON_SCALE);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Skills.SKILL_ICONS.getOrDefault(xpGain.skillName, Skills.DEFAULT_ICON), (int) ((PADDING_LEFT + ICON_OFFSET_LEFT) * (1 / ADDITIONAL_ICON_SCALE)), (int) ((baseY + ICON_OFFSET_TOP) * (1 / ADDITIONAL_ICON_SCALE)), 0, 0, 16, 16, 16, 16, MathUtils.getColorValue(foregroundColor));
        graphics.pose().scale(1 / ADDITIONAL_ICON_SCALE);

        graphics.text(
          Minecraft.getInstance().font,
          Component.literal(StringUtils.capitalize(xpGain.skillName) + " +" + String.format("%,d", (int) xpGain.totalXp)),
          PADDING_LEFT + TEXT_OFFSET_LEFT,
          baseY + TEXT_OFFSET_TOP,
          MathUtils.getColorValue(foregroundColor)
        );

        xpGain.currentTicks -= deltaTracker.getRealtimeDeltaTicks() * ReflectionUtils.getOrDefault(deltaTracker, "msPerTick", 50f);

        // Mark any indices for deletion.
        if (xpGain.currentTicks <= 0) {
          deletionIndices.add(i);
        }
      }
      graphics.pose().popMatrix();

      // Iterate through our deletion indices and remove them from notifications.
      // Do it in reverse order to avoid issues with updating indexes.
      for (Integer i : deletionIndices.reversed()) {
        XPGainContainer xpGain = notifications.get(i);
        notificationsBySkill.remove(xpGain.skillName);
        notifications.remove((int) i);
      }

      // Then re update our mappings.
      for (int i = 0; i < notifications.size(); ++i) {
        notificationsBySkill.put(notifications.get(i).skillName, i);
      }
    }
  }
}
