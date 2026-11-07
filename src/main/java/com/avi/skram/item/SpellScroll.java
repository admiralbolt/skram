package com.avi.skram.item;

import com.avi.skram.magic.SkramMagic;
import com.avi.skram.magic.Spellbook;
import com.avi.skram.utils.StringUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class SpellScroll extends Item {

  public static final String NAME = "spell_scroll";

  public SpellScroll(Properties properties) {
    super(properties);
  }

  @Override
  public @NonNull Component getName(@NonNull ItemStack itemStack) {
    SpellNameDataComponent spellName = itemStack.get(SkramItems.SPELL_NAME_DATA_COMPONENT.get());
    if (spellName == null) return Component.literal("Empty Spell Scroll");

    return Component.literal("Spell Scroll of " + StringUtils.snakeToTitle(spellName.spellName()));
  }

  @Override
  public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
    builder.accept(Component.literal("Using this spell scroll unlocks the spell."));
    super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
  }

  @Override
  public boolean isFoil(@NonNull ItemStack itemStack) {
    return true;
  }

  @Override
  public @NonNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);

    if (!level.isClientSide()) {
      // Get our spell name from the attached data component.
      SpellNameDataComponent spellName = stack.get(SkramItems.SPELL_NAME_DATA_COMPONENT.get());
      if (spellName != null) {
        // If we don't have the spell unlocked, unlock it!
        Spellbook spellbook = player.getData(SkramMagic.SPELLBOOK);
        if (!spellbook.knownSpells.contains(spellName.spellName())) {
          spellbook.knownSpells.add(spellName.spellName());
          player.setData(SkramMagic.SPELLBOOK, spellbook);
          stack.hurtAndBreak(1, player, hand);
          return InteractionResult.SUCCESS_SERVER;
        }
      }
    }

    return InteractionResult.FAIL;
  }
}
