package com.avi.skram.effect;

import com.avi.skram.Skram;
import com.avi.skram.magic.SkramMagic;
import com.avi.skram.utils.MathUtils;
import com.avi.skram.utils.Palette;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.alchemy.Potion;

public class SkramEffects {

  public static Holder<MobEffect> BURNING_RUSH;
  public static Holder<MobEffect> FLANKED;
  public static Holder<MobEffect> HEXED;
  public static Holder<MobEffect> MANA_REGENERATION;

  public static final Holder<Potion> MANA_REGENERATION_POTION = Skram.POTIONS.register("mana_regeneration_potion", registryName -> new Potion(
    registryName.getPath(),
    new MobEffectInstance(MANA_REGENERATION, 3600)
  ));

  public static void register() {
    BURNING_RUSH = Skram.EFFECTS.register(
      BurningRushEffect.NAME, () -> new BurningRushEffect(
        MobEffectCategory.BENEFICIAL,
        MathUtils.getColorValue(Palette.FIRE)
      )
    );

    FLANKED = Skram.EFFECTS.register(
      FlankedEffect.NAME, () -> new FlankedEffect(
        MobEffectCategory.HARMFUL,
        MathUtils.getColorValue(Palette.INDIGO)
      )
    );

    HEXED = Skram.EFFECTS.register(
      HexedEffect.NAME, () -> new HexedEffect(
        MobEffectCategory.HARMFUL,
        MathUtils.getColorValue(Palette.OBSIDIAN)
      )
    );

    MANA_REGENERATION = Skram.EFFECTS.register(
      ManaRegenerationEffect.NAME, () -> new ManaRegenerationEffect(
        MobEffectCategory.BENEFICIAL,
        MathUtils.getColorValue(Palette.CERULEAN)
      ).addAttributeModifier(
        SkramMagic.MANA_REGENERATION,
        Identifier.fromNamespaceAndPath(Skram.MODID, "effect.mana_regeneration"),
        1.0,
        AttributeModifier.Operation.ADD_VALUE
      )
    );
  }

}
