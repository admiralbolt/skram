package com.avi.skram.magic.spells;

import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.SkramMagic;
import net.minecraft.world.entity.player.Player;

public abstract class Spell {

  public String name;
  public int manaCost;
  public int castingTimeTicks;


  public Spell(String name, int manaCost, int castingTimeTicks) {
    this.name = name;
    this.manaCost = manaCost;
    this.castingTimeTicks = castingTimeTicks;
  }

  public boolean canCast(Player player) {
    SpellCastingInfo mana = player.getData(SkramMagic.SPELL_CASTING_INFO);
    return mana.currentMana >= this.manaCost;
  }

  public abstract void finishCast(Player player);

}
