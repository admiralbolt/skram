package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.AnvilCraftEvent;

import java.util.HashMap;
import java.util.Map;

public class SkillSmithing extends Skill {

  public static final String NAME = "smithing";
  public static final StreamCodec<ByteBuf, SkillSmithing> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillSmithing::new);
  public static Map<Item, Integer> XP_MAP;

  public static final int SMITHING_SPEED_INDEX = 0;
  public static final int DOUBLE_PRODUCTION_INDEX = 1;
  public static final int ENHANCE_REPAIR_INDEX = 2;

  public SkillSmithing() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true, true};
  }

  public SkillSmithing(ByteBuf buffer) {
    super(buffer);
  }

  public static void setupXPMap() {
    XP_MAP = new HashMap<>();

    // Ingots.
    XP_MAP.put(Items.COPPER_INGOT, 100);
    XP_MAP.put(Items.IRON_INGOT, 120);
    XP_MAP.put(Items.GOLD_INGOT, 170);
    XP_MAP.put(Items.NETHERITE_SCRAP, 240);

    // Nuggets, from smelting things down.
    XP_MAP.put(Items.COPPER_NUGGET, 20);
    XP_MAP.put(Items.IRON_NUGGET, 30);
    XP_MAP.put(Items.GOLD_NUGGET, 40);

    // Stone.
    XP_MAP.put(Items.STONE, 20);
    XP_MAP.put(Items.SMOOTH_STONE, 20);
    XP_MAP.put(Items.CRACKED_STONE_BRICKS, 20);
    XP_MAP.put(Items.DEEPSLATE, 20);
    XP_MAP.put(Items.CRACKED_DEEPSLATE_BRICKS, 20);
    XP_MAP.put(Items.CRACKED_DEEPSLATE_TILES, 20);
    XP_MAP.put(Items.SMOOTH_SANDSTONE, 40);
    XP_MAP.put(Items.SMOOTH_RED_SANDSTONE, 60);
    XP_MAP.put(Items.CRACKED_NETHER_BRICKS, 40);
    XP_MAP.put(Items.SMOOTH_BASALT, 20);
    XP_MAP.put(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS, 20);
    XP_MAP.put(Items.SMOOTH_QUARTZ, 20);
    XP_MAP.put(Items.TERRACOTTA, 40);

    for (Item item : Items.GLAZED_TERRACOTTA.asList()) {
      XP_MAP.put(item, 60);
    }

    // Misc.
    XP_MAP.put(Items.GLASS, 30);
    XP_MAP.put(Items.BRICK, 40);
    XP_MAP.put(Items.NETHER_BRICK, 50);
    XP_MAP.put(Items.RESIN_BRICK, 60);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.SMITHING, player.getData(Skills.SMITHING));
  }

  public float getSmithingSpeed() {
    return 0.03f * this.level;
  }

  public double getDoubleProductionChance() {
    return 0.01 * this.level;
  }

  public double getRepairBonus() {
    return 0.02 * this.level;
  }

  public static void handleAnvilUpdate(AnvilUpdateEvent event) {
    // Check to see if it's a repair event.
    ItemStack left = event.getLeft();
    ItemStack right = event.getRight();
    if (!left.isValidRepairItem(right)) return;

    Player player = event.getPlayer();
    SkillSmithing smithing = player.getData(Skills.SMITHING);
    ItemStack output = event.getOutput();
    if (smithing.enabledAbilities[ENHANCE_REPAIR_INDEX]) {
      int additionalRepair = (int) ((left.getDamageValue() - output.getDamageValue()) * smithing.getRepairBonus());
      output.setDamageValue(output.getDamageValue() - additionalRepair);
    }
  }

  public static void handleAnvilCraft(AnvilCraftEvent.Pre event) {
    // Make sure it's a repair event!
    ItemStack left = event.getLeft();
    ItemStack right = event.getRight();
    if (!left.isValidRepairItem(right)) return;

    Player player = event.getEntity();
    SkillSmithing smithing = player.getData(Skills.SMITHING);
    ItemStack output = event.getOutput();

    if (player.level().isClientSide()) return;

    smithing.addXp(player, left.getDamageValue() - output.getDamageValue());
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Furnace Speed: +" + (int) (100 * this.getSmithingSpeed()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[SMITHING_SPEED_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(SMITHING_SPEED_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Double Production Chance: " + (int) (100 * this.getDoubleProductionChance()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[DOUBLE_PRODUCTION_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(DOUBLE_PRODUCTION_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Enhance Repairs: +" + (int) (100 * this.getRepairBonus()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[ENHANCE_REPAIR_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(ENHANCE_REPAIR_INDEX))
        .build()
    );

    return vertical;
  }

}
