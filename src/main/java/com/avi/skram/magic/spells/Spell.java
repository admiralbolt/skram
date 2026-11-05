package com.avi.skram.magic.spells;

import com.avi.skram.Skram;
import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.SkramMagic;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public abstract class Spell {

  public String name;
  public int manaCost;
  public int castingTimeTicks;

  public Identifier spellIcon;
  public Holder<SoundEvent> spellSound;

  public Spell(String name, int manaCost, int castingTimeTicks, Holder<SoundEvent> spellSound) {
    this.name = name;
    this.manaCost = manaCost;
    this.castingTimeTicks = castingTimeTicks;
    this.spellIcon = Identifier.fromNamespaceAndPath(Skram.MODID, "textures/spells/" + name + ".png");
    this.spellSound = spellSound;
  }

  public boolean canCast(Player player) {
    SpellCastingInfo mana = player.getData(SkramMagic.SPELL_CASTING_INFO);
    return mana.currentMana >= this.manaCost;
  }

  public void finishCast(Player player) {
    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), this.spellSound.value(), SoundSource.PLAYERS, 0.4f, 1.0f);
  }

}
