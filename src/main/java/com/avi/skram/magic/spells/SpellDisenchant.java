package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.utils.MathUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Map;

public class SpellDisenchant extends Spell {

  public static String NAME = "disenchant";
  public static int REQUIRED_MAGIC_LEVEL = 23;
  public static int COST = 2000;
  public static int CASTING_TICKS = 100;

  public SpellDisenchant() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_GENERIC7);
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    ItemStack stack = player.getMainHandItem();
    if (!stack.isEnchanted()) return;

    ItemEnchantments originalEnchantments = stack.getAllEnchantments(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT));
    Map.Entry<Holder<Enchantment>, Integer> ench = originalEnchantments.entrySet().stream().skip(MathUtils.RANDOM.nextInt(originalEnchantments.size())).findFirst().get();

    ItemEnchantments.Mutable enchForBook = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
    enchForBook.set(ench.getKey(), ench.getValue());

    ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
    EnchantmentHelper.setEnchantments(enchantedBook, enchForBook.toImmutable());

    // Destroy item held in main hand.
    player.setItemInHand(InteractionHand.MAIN_HAND, enchantedBook);
  }

  @Override
  public String getDescription(Player player) {
    return "Destroy the currently selected item, gaining an enchanting book of a random enchantment from the item.";
  }
}
