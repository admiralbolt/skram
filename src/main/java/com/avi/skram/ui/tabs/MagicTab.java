package com.avi.skram.ui.tabs;

import com.avi.skram.magic.Spellbook;
import com.avi.skram.magic.SkramMagic;
import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.spells.Spell;
import com.avi.skram.magic.spells.Spells;
import com.avi.skram.network.SpellEquipPacket;
import com.avi.skram.utils.StringUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MagicTab {

  private static final Style LOCKED_SPELL_STYLE = Style.EMPTY
    .withFont(new FontDescription.Resource(Identifier.withDefaultNamespace("alt")))
    .withColor(ChatFormatting.GRAY);

  private MagicTab() {
  }

  public static List<TabEntry> entries() {
    Player player = Minecraft.getInstance().player;
    Spellbook spellbook = player.getData(SkramMagic.SPELLBOOK);
    List<TabEntry> entries = new ArrayList<>();

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);

    for (String spellName : Spells.SPELLS_BY_NAME.keySet().stream().sorted().toList()) {
      Spell spell = Spells.SPELLS_BY_NAME.get(spellName);
      boolean unlocked = spellbook.knownSpells.contains(spellName);
      LinearLayout indexContent = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
      indexContent.spacing(4).defaultCellSetting().alignVerticallyMiddle();
      indexContent.addChild(new ResizableTextureImageWidget(0, 0, 16, 16, spell.spellIcon, 16, 16));
      indexContent.addChild(new MultiLineTextWidget(
        spellIndexComponent(spellName, unlocked),
        Minecraft.getInstance().font
      ));

      entries.add(new TabEntry(
        spellName,
        indexContent,
        145,
        28,
        () -> renderSpellDetails(player, spell, unlocked, spell.name.equals(castingInfo.equippedSpellName))
      ));
    }

    return entries;
  }

  private static Layout renderSpellDetails(Player player, Spell spell, boolean unlocked, boolean equipped) {
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    layout.spacing(4).defaultCellSetting().padding(2);

    LinearLayout summary = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
    summary.spacing(5).defaultCellSetting().alignVerticallyTop();
    summary.addChild(new ResizableTextureImageWidget(0, 0, 32, 32, spell.spellIcon, 32, 32));

    LinearLayout summaryText = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    summaryText.spacing(2).defaultCellSetting();
    summaryText.addChild(textWidget(spellNameComponent(spell.name, unlocked)));
    summaryText.addChild(textWidget(spellInfoComponent("Required Magic Level: " + spell.requiredMagicLevel, unlocked)));
    summaryText.addChild(textWidget(spellInfoComponent("Mana Cost: " + spell.manaCost, unlocked)));
    summaryText.addChild(textWidget(spellInfoComponent(
      "Casting Time: " + String.format(Locale.ROOT, "%.1f seconds", spell.castingTimeTicks / 20.0),
      unlocked
    )));
    summary.addChild(summaryText);
    layout.addChild(summary);

    if (unlocked) {
      Button equipButton = Button.builder(
        Component.literal(equipped ? "Equipped" : "Equip"),
        button -> {
          ClientPacketDistributor.sendToServer(new SpellEquipPacket(spell.name));
          button.setMessage(Component.literal("Equipped"));
          button.active = false;
        }
      ).size(100, 20).build();
      equipButton.active = !equipped;
      layout.addChild(equipButton);
    }

    layout.addChild(textWidget(spellInfoComponent(spell.getDescription(player), unlocked)));
    return layout;
  }

  private static MultiLineTextWidget textWidget(Component text) {
    MultiLineTextWidget widget = new MultiLineTextWidget(text, Minecraft.getInstance().font);
    widget.setMaxWidth(180);
    return widget;
  }

  private static Component spellIndexComponent(String spellName, boolean unlocked) {
    String displayName = StringUtils.snakeToTitle(spellName);

    if (unlocked) return Component.literal(displayName);

    return Component.literal(displayName).withStyle(LOCKED_SPELL_STYLE);
  }

  private static Component spellNameComponent(String spellName, boolean unlocked) {
    String displayName = StringUtils.snakeToTitle(spellName);
    return (unlocked) ? Component.literal(displayName) : Component.literal(displayName).withStyle(LOCKED_SPELL_STYLE);
  }

  private static Component spellInfoComponent(String text, boolean unlocked) {
    return (unlocked) ? Component.literal(text) : Component.literal(text).withStyle(LOCKED_SPELL_STYLE);
  }

}
