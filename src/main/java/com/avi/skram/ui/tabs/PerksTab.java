package com.avi.skram.ui.tabs;

import com.avi.skram.perks.Perk;
import com.avi.skram.perks.Perks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;

public final class PerksTab {

  private static final Identifier WHITE_BAR = Identifier.withDefaultNamespace("textures/gui/sprites/boss_bar/white_progress.png");

  private PerksTab() {}

  public static Layout render() {
    LinearLayout content = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    content.spacing(2).defaultCellSetting().paddingTop(4);

    for (String perkName : Perks.PERKS_BY_NAME.keySet().stream().sorted().toList()) {
      Perk perk = (Perk) Minecraft.getInstance().player.getData(Perks.PERKS_BY_NAME.get(perkName));
      content.addChild(perk.renderToLayout(Minecraft.getInstance().player));
      content.addChild(new ResizableTextureImageWidget(0, 0, 180, 3, WHITE_BAR, 182, 5));
    }

    return content;
  }
}
