package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public class SkillMagic extends Skill {

  public static final String NAME = "magic";
  public static final StreamCodec<ByteBuf, SkillMagic> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillMagic::new);

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
    return 0.1 * this.level;
  }

  public int getManaCapacity() {
    return 10 * this.level;
  }

  public double getSpellPower() {
    return 0.01 * this.level;
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(Checkbox.builder(
        Component.literal("Mana Regen: +" + this.getManaRegen()),
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