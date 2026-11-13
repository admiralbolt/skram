package com.avi.skram.client;

import com.avi.skram.SkramClient;
import com.avi.skram.magic.SkramMagic;
import com.avi.skram.magic.SpellCastSoundHandler;
import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.spells.Spell;
import com.avi.skram.magic.spells.Spells;
import com.avi.skram.ui.SpellUI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class SkramMagicClient {

  public static SpellCastSoundHandler HANDLER = null;

  public static void castStart() {
    Player player = Minecraft.getInstance().player;
    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    Spell spell = Spells.SPELLS_BY_NAME.get(castingInfo.equippedSpellName);
    if (spell == null) return;

    if (castingInfo.isCasting) {
      castingInfo.cancel = true;
    } else {
      if (spell.canCast(player)) {
        // If we can cast it, do so!
        castingInfo.currentMana -= spell.manaCost;
        castingInfo.isCasting = true;
        castingInfo.castingTimer = spell.castingTimeTicks;
        castingInfo.spellName = castingInfo.equippedSpellName;
        castingInfo.startCasting = true;
      } else {
        // Otherwise, mark that we failed to cast a spell.
        castingInfo.failedCasting = true;
        // Rather than using a separate var to track failed casting, we reuse the same timer.
        castingInfo.castingTimer = 20;
      }
    }

    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

  public static void handleTick(PlayerTickEvent.Pre event) {
    Player player = event.getEntity();
    if (!SkramClient.isLocalPlayer(player)) return;

    AttributeMap attributes = player.getAttributes();
    AttributeInstance maxManaInstance = attributes.getInstance(SkramMagic.MAX_MANA);
    AttributeInstance manaRegenInstance = attributes.getInstance(SkramMagic.MANA_REGENERATION);
    if (maxManaInstance == null || manaRegenInstance == null) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    Spell spell = Spells.SPELLS_BY_NAME.get(castingInfo.spellName);

    // First, we increase our mana based on our mana regen value.
    castingInfo.currentMana = Math.min(maxManaInstance.getValue(), castingInfo.currentMana + manaRegenInstance.getValue());

    // If we are currently casting, decrease the timer.
    if (castingInfo.isCasting) {
      // Reset cancel status if we've just started casting.
      if (castingInfo.startCasting) {
        castingInfo.cancel = false;
      }

      // Wait a tick before consuming startCasting().
      if (castingInfo.startCasting && (spell.castingTimeTicks - castingInfo.castingTimer) >= 1) {
        castingInfo.startCasting = false;
      }
      castingInfo.castingTimer--;

      if (castingInfo.cancel) {
        // If we got the cancel signal from somewhere, cancel the spell.
        // We refund mana based on time left.
        double refund = ((double) castingInfo.castingTimer / spell.castingTimeTicks) * spell.manaCost * 0.8;
        castingInfo.isCasting = false;
        castingInfo.castingTimer = 0;
        castingInfo.currentMana += refund;
        castingInfo.spellName = "";
        castingInfo.cancel = false;
      } else if (castingInfo.castingTimer <= 0) {
        // Reset our values, and cast our spell.
        castingInfo.isCasting = false;
        castingInfo.castingTimer = 0;

        spell.sendCastPacket(player);
      }
    }

    // If we failed casting.
    if (castingInfo.failedCasting) {
      if (castingInfo.isCasting) {
        castingInfo.failedCasting = false;
      } else {
        castingInfo.castingTimer--;
        if (castingInfo.castingTimer <= 0) {
          castingInfo.failedCasting = false;
        }
      }
    }

    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

  public static void handleTickPost(PlayerTickEvent.Post event) {
    Player player = event.getEntity();
    if (!SkramClient.isLocalPlayer(player)) return;

    AttributeMap attributes = player.getAttributes();
    AttributeInstance maxManaInstance = attributes.getInstance(SkramMagic.MAX_MANA);
    AttributeInstance manaRegenInstance = attributes.getInstance(SkramMagic.MANA_REGENERATION);
    if (maxManaInstance == null || manaRegenInstance == null) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    SpellUI.update(castingInfo, maxManaInstance.getValue());

    if (HANDLER == null || HANDLER.player != player) {
      HANDLER = new SpellCastSoundHandler(player);
    }

    if (castingInfo.failedCasting) {
      HANDLER.failedCasting();
    } else if (castingInfo.startCasting) {
      HANDLER.startCasting(castingInfo.castingTimer);
    } else if (!castingInfo.isCasting) {
      HANDLER.stopCasting();
    }

    HANDLER.tick();
  }

  public static void cancelSpell(AttackEntityEvent event) {
    Player player = event.getEntity();
    if (!SkramClient.isLocalPlayer(player)) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    castingInfo.cancel = true;
    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

  public static void cancelSpell(PlayerInteractEvent event) {
    Player player = event.getEntity();
    if (!SkramClient.isLocalPlayer(player)) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    castingInfo.cancel = true;
    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }
}
