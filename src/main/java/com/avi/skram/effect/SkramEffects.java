package com.avi.skram.effect;

import com.avi.skram.Skram;
import com.avi.skram.utils.MathUtils;
import com.avi.skram.utils.Palette;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SkramEffects {

  public static Holder<MobEffect> BURNING_RUSH;
  public static Holder<MobEffect> HEXED;

  public static void register() {
    BURNING_RUSH = Skram.EFFECTS.register(
      BurningRushEffect.NAME, () -> new BurningRushEffect(
        MobEffectCategory.BENEFICIAL,
        MathUtils.getColorValue(Palette.FIRE)
      )
    );

    HEXED = Skram.EFFECTS.register(
      HexedEffect.NAME, () -> new HexedEffect(
        MobEffectCategory.HARMFUL,
        MathUtils.getColorValue(Palette.OBSIDIAN)
      )
    );
  }

}
