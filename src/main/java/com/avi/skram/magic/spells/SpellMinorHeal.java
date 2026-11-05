package com.avi.skram.magic.spells;

import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import net.minecraft.world.entity.player.Player;

public class SpellMinorHeal extends Spell {

  public static String NAME = "minor_heal";
  public static int COST = 300;
  public static int CASTING_TICKS = 40;

  public static double BASE_HEAL = 2.0;

  public SpellMinorHeal() {
    super(NAME, COST, CASTING_TICKS);
  }

  @Override
  public void finishCast(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    player.heal((float) (BASE_HEAL * (1 + magic.getSpellPower())));
  }
}
