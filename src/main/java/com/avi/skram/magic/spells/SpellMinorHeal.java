package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import net.minecraft.world.entity.player.Player;

public class SpellMinorHeal extends Spell {

  public static String NAME = "minor_heal";
  public static int REQUIRED_MAGIC_LEVEL = 1;
  public static int COST = 100;
  public static int CASTING_TICKS = 40;

  public static double BASE_HEAL = 3.0;

  public SpellMinorHeal() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void finishCast(Player player) {
    super.finishCast(player);
    SkillMagic magic = player.getData(Skills.MAGIC);
    player.heal((float) (BASE_HEAL * (1 + magic.getSpellPower())));
  }

  @Override
  public String getDescription(Player player) {
    SkillMagic magic = player.getData(Skills.MAGIC);
    return String.format("Heal yourself for %.1f hearts.", (float) (BASE_HEAL * (1 + magic.getSpellPower())));
  }
}
