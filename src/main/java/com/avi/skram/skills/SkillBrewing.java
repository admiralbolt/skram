package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class SkillBrewing extends Skill {

  public static final String NAME = "brewing";
  public static final StreamCodec<ByteBuf, SkillBrewing> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillBrewing::new);
  public static final String DESCRIPTION = """
    B R E W.
    
    Brewing Speed and XP gained for brewing are tied directly to the brewing stand itself. Brewing stands YOU place will be marked as yours, and will brew faster based on YOUR level and give YOU XP.
    If you brew in someone else's brewing stand, THEY will get XP.
    If you open the brewing stand interface and see your name, it is YOUR brewing stand.
    If you open the brewing stand interface and see someone else's name, it is THEIR brewing stand.
    
    XP:
     - 250XP per potion brewed (750 for a set of 3).
     - 40XP per potion used (drank or thrown).
    """;
  public static Map<String, Integer> XP_MAP;

  public static final int BREWING_SPEED_INDEX = 0;

  public SkillBrewing() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true};
  }

  public SkillBrewing(ByteBuf buffer) {
    super(buffer);
  }

  public static void registerXPMap() {
    XP_MAP = new HashMap<>();
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.BREWING, player.getData(Skills.BREWING));
  }

  public void addXpForItems(Player player, NonNullList<ItemStack> items) {
    int totalXp = 0;
    for (ItemStack stack : items) {
      PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
      if (contents == null) continue;

      PotionItem potionItem = (PotionItem) stack.getItem();
      Component name = potionItem.getName(stack);
      if (!(name.getContents() instanceof TranslatableContents translatableContents)) continue;

      totalXp += XP_MAP.getOrDefault(translatableContents.getKey(), 250);
    }
    this.addXp(player, totalXp);
  }

  public static void handleDrinkPotion(LivingEntityUseItemEvent.Finish event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    PotionContents contents = event.getItem().get(DataComponents.POTION_CONTENTS);
    if (contents == null) return;

    SkillBrewing brewing = player.getData(Skills.BREWING);
    brewing.addXp(player, 40);
  }

  public static void handleThrownPotion(PlayerInteractEvent.RightClickItem event) {
    if (event.getLevel().isClientSide()) return;

    Item item = event.getItemStack().getItem();
    if (!(item instanceof ThrowablePotionItem)) return;

    Player player = event.getEntity();
    SkillBrewing brewing = player.getData(Skills.BREWING);
    brewing.addXp(player, 40);
  }

  public float getBrewingSpeed() {
    return 0.03f * this.level;
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(new AbilityDescription(
      "Brewing Speed: +" + (int) (100 * this.getBrewingSpeed()) + "%.",
      BREWING_SPEED_INDEX
    ));
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}
