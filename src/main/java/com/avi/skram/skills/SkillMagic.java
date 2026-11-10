package com.avi.skram.skills;

import com.avi.skram.Skram;
import com.avi.skram.magic.SkramMagic;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;

public class SkillMagic extends Skill {

  public static final String NAME = "magic";
  public static final StreamCodec<ByteBuf, SkillMagic> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillMagic::new);

  public static final Identifier MANA_CAPACITY_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "magic_mana_capacity_boost");
  public static final Identifier MANA_REGENERATION_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "magic_mana_regeneration_boost");

  public static final int MANA_REGEN_INDEX = 0;
  public static final int MANA_CAPACITY_INDEX = 1;
  public static final int SPELL_POWER_INDEX = 2;

  public SkillMagic() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true, true};
  }

  public SkillMagic(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.MAGIC, player.getData(Skills.MAGIC));
  }

  public double getManaRegen() {
    return 0.02 * this.level;
  }

  public double getManaCapacity() {
    return 50 * this.level;
  }

  public double getSpellPower() {
    return 0.01 * this.level;
  }

  @Override
  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();

    AttributeInstance capacityBoost = attributes.getInstance(SkramMagic.MAX_MANA);
    if (capacityBoost != null) {
      if (this.enabledAbilities[MANA_CAPACITY_INDEX]) {
        capacityBoost.addOrReplacePermanentModifier(new AttributeModifier(
          MANA_CAPACITY_BOOST, this.getManaCapacity(), AttributeModifier.Operation.ADD_VALUE
        ));
      } else {
        capacityBoost.removeModifier(MANA_CAPACITY_BOOST);
      }
    }

    AttributeInstance regenerationBoost = attributes.getInstance(SkramMagic.MANA_REGENERATION);
    if (regenerationBoost != null) {
      if (this.enabledAbilities[MANA_REGEN_INDEX]) {
        regenerationBoost.addOrReplacePermanentModifier(new AttributeModifier(
          MANA_REGENERATION_BOOST, this.getManaRegen(), AttributeModifier.Operation.ADD_VALUE
        ));
      } else {
        regenerationBoost.removeModifier(MANA_REGENERATION_BOOST);
      }
    }
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(Checkbox.builder(
        Component.literal("Mana Regen: +" + String.format("%.2f", this.getManaRegen())),
        Minecraft.getInstance().font
      )
      .selected(this.enabledAbilities[MANA_REGEN_INDEX])
      .onValueChange((checkbox, value) -> this.toggleEnabled(MANA_REGEN_INDEX))
      .build());

    vertical.addChild(Checkbox.builder(
        Component.literal("Mana Capacity: +" + this.getManaCapacity()),
        Minecraft.getInstance().font
      )
      .selected(this.enabledAbilities[MANA_CAPACITY_INDEX])
      .onValueChange((checkbox, value) -> this.toggleEnabled(MANA_CAPACITY_INDEX))
      .build());

    vertical.addChild(Checkbox.builder(
        Component.literal("Spell Power: +" + (int) (100 * this.getSpellPower()) + "%"),
        Minecraft.getInstance().font
      )
      .selected(this.enabledAbilities[SPELL_POWER_INDEX])
      .onValueChange((checkbox, value) -> this.toggleEnabled(SPELL_POWER_INDEX))
      .build());

    return vertical;
  }
}