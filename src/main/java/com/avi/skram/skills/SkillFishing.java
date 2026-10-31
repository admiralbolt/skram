package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

import java.util.List;

public class SkillFishing extends Skill {

  public static final String NAME = "fishing";
  public static final StreamCodec<ByteBuf, SkillFishing> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillFishing::new);

  public static final int FISHING_SPEED_INDEX = 0;
  public static final int CATCH_TREASURE_INDEX = 1;

  public SkillFishing() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true};
  }

  public SkillFishing(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.FISHING, player.getData(Skills.FISHING));
  }

  public float getSpeedBonus() {
    return 0.8f * this.level;
  }

  public double catchTreasureChance() {
    return 0.5 * this.level;
  }

  public static void handleCatchFish(ItemFishedEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) return;

    SkillFishing fishing = player.getData(Skills.FISHING);

    if (fishing.enabledAbilities[SkillFishing.CATCH_TREASURE_INDEX] && Math.random() < fishing.catchTreasureChance()) {
      // Item spawning / physics code copied from FishingHook.retrieve().
      FishingHook hook = event.getHookEntity();
      LootParams params = new LootParams.Builder((ServerLevel) player.level())
        .withParameter(LootContextParams.ORIGIN, player.position()).withParameter(LootContextParams.TOOL, player.getActiveItem())
        .withParameter(LootContextParams.THIS_ENTITY, hook)
        .withParameter(LootContextParams.ATTACKING_ENTITY, player)
        .withLuck(player.getLuck())
        .create(LootContextParamSets.FISHING);

      LootTable lootTable = player.level().getServer().reloadableRegistries().getLootTable(BuiltInLootTables.BURIED_TREASURE);
      List<ItemStack> items = lootTable.getRandomItems(params);

      ItemEntity entity = new ItemEntity(hook.level(), hook.getX(), hook.getY(), hook.getZ(), items.getFirst());
      double xa = player.getX() - hook.getX();
      double ya = player.getY() - hook.getY();
      double za = player.getZ() - hook.getZ();
      entity.setDeltaMovement(xa * 0.1, ya * 0.1 + Math.sqrt(Math.sqrt(xa * xa + ya * ya + za * za)) * 0.08, za * 0.1);
      player.level().addFreshEntity(entity);
    }

    fishing.addXp(player, 200);
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Fishing Speed: +" + (int) (100 * this.getSpeedBonus()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[FISHING_SPEED_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(FISHING_SPEED_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Treasure Chance: " + (int) (100 * this.catchTreasureChance()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[CATCH_TREASURE_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(CATCH_TREASURE_INDEX))
        .build()
    );

    return vertical;
  }
}