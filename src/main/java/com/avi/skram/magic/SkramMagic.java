package com.avi.skram.magic;

import com.avi.skram.Skram;
import com.avi.skram.magic.spells.Spell;
import com.avi.skram.magic.spells.Spells;
import com.avi.skram.ui.SpellUI;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.function.Supplier;

public class SkramMagic {

  public static double DEFAULT_MAX_MANA = 2000.0;
  public static double DEFAULT_MANA_REGENERATION = 1.0;

  public static final Holder<Attribute> MAX_MANA = Skram.ATTRIBUTES.register("max_mana", () -> new RangedAttribute(
    "attributes.skram.max_mana",
    DEFAULT_MAX_MANA,
    0,
    DEFAULT_MAX_MANA * 1000
  ).setSyncable(true));

  public static final Holder<Attribute> MANA_REGENERATION = Skram.ATTRIBUTES.register("mana_regeneration", () -> new RangedAttribute(
    "attributes.skram.mana_regeneration",
    DEFAULT_MANA_REGENERATION,
    -1000,
    1000
  ).setSyncable(true));

  public static Supplier<AttachmentType<SpellCastingInfo>> SPELL_CASTING_INFO;
  public static Supplier<AttachmentType<Spellbook>> SPELLBOOK;

  public static void register() {
    SPELL_CASTING_INFO = Skram.ATTACHMENT_TYPES.register(
      SpellCastingInfo.NAME, () -> AttachmentType.serializable(() -> new SpellCastingInfo())
        .sync(SpellCastingInfo.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );

    SPELLBOOK = Skram.ATTACHMENT_TYPES.register(
      Spellbook.NAME, () -> AttachmentType.serializable(() -> new Spellbook())
        .sync(Spellbook.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );
  }

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
    if (!player.level().isClientSide()) return;

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

  public static SpellCastSoundHandler HANDLER = null;

  public static void handleTickPost(PlayerTickEvent.Post event) {
    Player player = event.getEntity();
    if (!player.level().isClientSide()) return;

    AttributeMap attributes = player.getAttributes();
    AttributeInstance maxManaInstance = attributes.getInstance(SkramMagic.MAX_MANA);
    AttributeInstance manaRegenInstance = attributes.getInstance(SkramMagic.MANA_REGENERATION);
    if (maxManaInstance == null || manaRegenInstance == null) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    SpellUI.update(castingInfo, maxManaInstance.getValue());

    if (HANDLER == null) {
      HANDLER = new SpellCastSoundHandler(player);
    }

    if (castingInfo.failedCasting) {
      HANDLER.failedCasting();
    } else  if (castingInfo.startCasting) {
      HANDLER.startCasting(castingInfo.castingTimer);
    } else if (!castingInfo.isCasting) {
      HANDLER.stopCasting();
    }

    HANDLER.tick();
  }

  public static void cancelSpell(AttackEntityEvent event) {
    Player player = event.getEntity();
    if (!player.level().isClientSide()) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    castingInfo.cancel = true;
    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

  public static void cancelSpell(PlayerInteractEvent event) {
    Player player = event.getEntity();
    if (!player.level().isClientSide()) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    castingInfo.cancel = true;
    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

  public static void cancelSpell(LivingDamageEvent.Pre event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (!player.level().isClientSide()) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    castingInfo.cancel = true;
    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

}
