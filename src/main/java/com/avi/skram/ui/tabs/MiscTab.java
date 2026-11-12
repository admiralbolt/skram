package com.avi.skram.ui.tabs;

import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;

import java.util.List;
import java.util.function.Supplier;

public final class MiscTab {

  private MiscTab() {}

  public static List<TabEntry> entries() {
    return List.of(
      createGeneralEntry(),
      createSkillsEntry(),
      createPerksEntry(),
      createMagicEntry()
    );
  }

  private static MultiLineTextWidget textWidget(String text) {
    MultiLineTextWidget widget = new MultiLineTextWidget(Component.literal(text), Minecraft.getInstance().font);
    widget.setMaxWidth(220);
    return widget;
  }

  private static TabEntry createGeneralEntry() {
    return createEntry("general", "General", "textures/item/book.png", MiscTab::createGeneralDetails);
  }

  private static TabEntry createSkillsEntry() {
    return createEntry("skills", "Skills", "textures/item/iron_sword.png", MiscTab::createSkillsDetails);
  }

  private static TabEntry createPerksEntry() {
    return createEntry("perks", "Perks", "textures/item/diamond.png", MiscTab::createPerksDetails);
  }

  private static TabEntry createMagicEntry() {
    return createEntry("magic", "Magic", "textures/item/enchanted_book.png", MiscTab::createMagicDetails);
  }

  private static TabEntry createEntry(String key, String name, String icon, Supplier<Layout> details) {
    LinearLayout indexContent = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
    indexContent.spacing(4).defaultCellSetting().alignVerticallyMiddle();
    indexContent.addChild(new ResizableTextureImageWidget(
      0, 0, 16, 16, Identifier.withDefaultNamespace(icon), 16, 16
    ));
    indexContent.addChild(new MultiLineTextWidget(Component.literal(name), Minecraft.getInstance().font));
    return new TabEntry(
      key,
      indexContent,
      145,
      28,
      details
    );
  }

  private static Layout createGeneralDetails() {
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    layout.spacing(6);
    layout.addChild(new MultiLineTextWidget(
      Component.literal("Well, well, well."),
      Minecraft.getInstance().font
    ));
    layout.addChild(textWidget(
      "Welcome to Skram™. A few things have changed, but the gist is mostly the same. Skills provided permanent passive benefits, and can be leveled up by doing stuff! Perks have a chance to be discovered whenever you gain XP. Spells must be learned from spell scrolls, which you'll find out in the wild."
    ));
    layout.addChild(textWidget(
      "Enjoy! If you encounter any bugs, or have feedback please let me know!"
    ));
    layout.addChild(textWidget(
      "Some other general balance notes:\n* Healing from food is nerfed to 25% effectiveness.\n* Food exhaustion from all sources increased by 50%\n* Items & Armor have skill level requirements.\n* Infernal mobs is configured to higher than normal settings.\n* Waystones exist and are craftable, but have a high magic level requirement."
    ));
    return layout;
  }

  private static Layout createSkillsDetails() {
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    layout.spacing(6);
    layout.addChild(textWidget("There are currently: §6 " + Skills.SKILLS_BY_NAME.size() + "§f skills!"));
    layout.addChild(textWidget("XP per level is based on a cubic formula: " + Skill.XP_FACTOR + " * ((1/2) level^3 + level^2 + 2 * level)"));
    layout.addChild(textWidget("Skills gain XP bonus from two sources: Total Levels (1% bonus for ALL skills per 20 levels of ALL skills), and additional per-skill bonuses based on a completion log of activities. See the achievements for the full details."));
    layout.addChild(textWidget("Also note, that XP is renderer as ints but tracked with more precision. You CAN gain 1.1 XP, it just shows as 1 XP."));
    return layout;
  }

  private static Layout createPerksDetails() {
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    layout.spacing(6);
    layout.addChild(textWidget("Perks have a chance of being discovered every time you gain XP (the exact chance is [XP_GAINED / " + Skill.PERK_CHANCE + "]). Once discovered, perks can be researched if you have both the required levels and the required materials."));
    layout.addChild(textWidget("Not all perks are created equal."));
    return layout;
  }

  private static Layout createMagicDetails() {
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    layout.spacing(6);
    layout.addChild(textWidget("Casting spells is done with the 'c' key by default. Spell casting requires a lot of focus, and is interrupted by left/right clicking anything, or by taking damage."));
    layout.addChild(textWidget("Levelling up your magic increase your max mana, mana regeneration, and spell power of many spells. Spells must be discovered! They can be found in many chests, and as a drop from some mobs. You can't learn a spell from a scroll until you have the required magic level."));
    return layout;
  }
}
