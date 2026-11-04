package com.avi.skram.perks;

import com.avi.skram.skills.SkillMelee;
import com.avi.skram.skills.SkillStealth;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Map;

public class PerkBackstab extends Perk {
  public static final String NAME = "backstab";
  public static final String DESCRIPTION = "Deal +50% melee damage to unaware entities.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillStealth.NAME, 30)
    .put(SkillMelee.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkBackstab> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkBackstab::new);

  public static List<ItemStack> getResearchCost() {
    ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
    Holder<Enchantment> swiftSneakHolder = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.SWIFT_SNEAK).get();
    enchantedBook.enchant(swiftSneakHolder, 3);

    return List.of(
      new ItemStack(Items.IRON_SWORD, 1),
      enchantedBook
    );
  }

  public PerkBackstab() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkBackstab(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    if (!SkillStealth.isSneakAttack(event)) return;
    if (!(event.getSource().getEntity() instanceof Player player)) return;

    PerkBackstab perk = player.getData(Perks.BACKSTAB);
    if (!perk.isFullyEnabled()) return;

    event.setNewDamage(event.getNewDamage() * 1.5f);
  }


}
