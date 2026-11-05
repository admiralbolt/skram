package com.avi.skram.magic;

import com.avi.skram.Skram;
import com.avi.skram.magic.spells.Spells;
import com.avi.skram.ui.ManaBar;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.function.Supplier;

public class SkramMagic {

  public static double DEFAULT_MAX_MANA = 1000.0;
  public static double DEFAULT_MANA_REGENERATION = 2.0;

  public static final Holder<Attribute> MAX_MANA = Skram.ATTRIBUTES.register("max_mana", () -> new RangedAttribute(
    "attributes.skram.max_mana",
    DEFAULT_MAX_MANA,
    0,
    DEFAULT_MAX_MANA * 1000
  ));

  public static final Holder<Attribute> MANA_REGENERATION = Skram.ATTRIBUTES.register("mana_regeneration", () -> new RangedAttribute(
    "attributes.skram.mana_regeneration",
    DEFAULT_MANA_REGENERATION,
    -1000,
    1000
  ));

  public static Supplier<AttachmentType<SpellCastingInfo>> SPELL_CASTING_INFO;

  public static void register() {
    SPELL_CASTING_INFO = Skram.ATTACHMENT_TYPES.register(
      SpellCastingInfo.NAME, () -> AttachmentType.serializable(() -> new SpellCastingInfo())
        .sync(SpellCastingInfo.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );
  }

  public static void handleTick(PlayerTickEvent.Pre event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) return;

    AttributeMap attributes = player.getAttributes();
    AttributeInstance maxManaInstance = attributes.getInstance(SkramMagic.MAX_MANA);
    AttributeInstance manaRegenInstance = attributes.getInstance(SkramMagic.MANA_REGENERATION);
    if (maxManaInstance == null || manaRegenInstance == null) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    // First, we increase our mana based on our mana regen value.
    castingInfo.currentMana = Math.min(maxManaInstance.getValue(), castingInfo.currentMana + manaRegenInstance.getValue());

    // If we are currently casting, decrease the timer.
    if (castingInfo.isCasting) {
      castingInfo.castingTimer--;

      if (castingInfo.cancel) {
        // If we got the cancel signal from somewhere, cancel the spell.
        // We refund mana based on time left.
        castingInfo.isCasting = false;
        double refund = ((double) castingInfo.castingTimer / Spells.SPELLS_BY_NAME.get(castingInfo.spellName).castingTimeTicks) * Spells.SPELLS_BY_NAME.get(castingInfo.spellName).manaCost * 0.8;
        castingInfo.castingTimer = 0;
        castingInfo.currentMana += refund;
        castingInfo.spellName = "";
        castingInfo.cancel = false;
      } else if (castingInfo.castingTimer <= 0) {
        // Reset our values, and cast our spell.
        castingInfo.isCasting = false;
        castingInfo.castingTimer = 0;

        if (!player.level().isClientSide()) {
          Spells.SPELLS_BY_NAME.get(castingInfo.spellName).finishCast(player);
        }
      }
    }

    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

  public static void handleTickPost(PlayerTickEvent.Post event) {
    Player player = event.getEntity();
    if (!player.level().isClientSide()) return;

    AttributeMap attributes = player.getAttributes();
    AttributeInstance maxManaInstance = attributes.getInstance(SkramMagic.MAX_MANA);
    AttributeInstance manaRegenInstance = attributes.getInstance(SkramMagic.MANA_REGENERATION);
    if (maxManaInstance == null || manaRegenInstance == null) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);

    ManaBar.updateMana(castingInfo.currentMana, maxManaInstance.getValue());
  }

  public static void cancelSpell(AttackEntityEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    castingInfo.cancel = true;
    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

  public static void cancelSpell(PlayerInteractEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    castingInfo.cancel = true;
    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

}
