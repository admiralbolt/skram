package com.avi.skram.magic;

import com.avi.skram.Skram;
import com.avi.skram.perks.PerkStalwartSpellcaster;
import com.avi.skram.perks.Perks;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

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

  public static void cancelSpell(LivingDamageEvent.Pre event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    PerkStalwartSpellcaster perk = player.getData(Perks.STALWART_SPELLCASTER);
    if (perk.isFullyEnabled()) return;

    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    castingInfo.cancel = true;
    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

}
