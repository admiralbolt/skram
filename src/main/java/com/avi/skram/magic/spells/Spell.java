package com.avi.skram.magic.spells;

import com.avi.skram.Skram;
import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.SkramMagic;
import com.avi.skram.skills.SkillMagic;
import com.avi.skram.skills.Skills;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public abstract class Spell {

  public String name;
  public int requiredMagicLevel;
  public int manaCost;
  public int castingTimeTicks;

  public Identifier spellIcon;
  public Holder<SoundEvent> spellSound;

  public Spell(String name, int requiredMagicLevel, int manaCost, int castingTimeTicks, Holder<SoundEvent> spellSound) {
    this.name = name;
    this.requiredMagicLevel = requiredMagicLevel;
    this.manaCost = manaCost;
    this.castingTimeTicks = castingTimeTicks;
    this.spellIcon = Identifier.fromNamespaceAndPath(Skram.MODID, "textures/spells/" + name + ".png");
    this.spellSound = spellSound;
  }

  public boolean canCast(Player player) {
    SpellCastingInfo mana = player.getData(SkramMagic.SPELL_CASTING_INFO);
    if (mana.currentMana < this.manaCost) return false;

    SkillMagic magic = player.getData(Skills.MAGIC);
    return magic.level >= this.requiredMagicLevel;
  }

  public void finishCast(Player player) {
    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), this.spellSound.value(), SoundSource.PLAYERS, 0.35f, 1.0f);
    SkillMagic magic = player.getData(Skills.MAGIC);
    magic.addXp(player, 10 + ((double) this.requiredMagicLevel / 3) + ((double) this.manaCost / 5));
  }

  // This is done via a method call that way we can correctly apply modifiers from the player.
  public abstract String getDescription(Player player);

}
