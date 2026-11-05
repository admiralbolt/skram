package com.avi.skram.magic.spells;

import com.avi.skram.Skram;
import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.SkramMagic;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public abstract class Spell {

  public String name;
  public int manaCost;
  public int castingTimeTicks;
  public Identifier spellIcon;

  public Spell(String name, int manaCost, int castingTimeTicks) {
    this.name = name;
    this.manaCost = manaCost;
    this.castingTimeTicks = castingTimeTicks;
    this.spellIcon = Identifier.fromNamespaceAndPath(Skram.MODID, "textures/spells/" + name + ".png");
  }

  public boolean canCast(Player player) {
    SpellCastingInfo mana = player.getData(SkramMagic.SPELL_CASTING_INFO);
    return mana.currentMana >= this.manaCost;
  }

  public abstract void finishCast(Player player);

}
