package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
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
  public static final String DESCRIPTION = """
    Fishy fishy!
    
    Fishing additional loot is rolled from the Buried Treasure loot table, excluding the Heart of the Sea.
    You gain 200 XP per fish caught.
    """;

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
    return 0.02f * this.level;
  }

  public double catchTreasureChance() {
    return 0.01 * this.level;
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

      ItemEntity entity = new ItemEntity(hook.level(), hook.getX(), hook.getY(), hook.getZ(), items.get((items.size() >= 2) ? 1 : 0));
      double xa = player.getX() - hook.getX();
      double ya = player.getY() - hook.getY();
      double za = player.getZ() - hook.getZ();
      entity.setDeltaMovement(xa * 0.1, ya * 0.1 + Math.sqrt(Math.sqrt(xa * xa + ya * ya + za * za)) * 0.08, za * 0.1);
      player.level().addFreshEntity(entity);
    }

    fishing.addXp(player, 200);
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(
      new AbilityDescription("Fishing Speed: +" + (int) (100 * this.getSpeedBonus()) + "%.", FISHING_SPEED_INDEX),
      new AbilityDescription("Treasure Chance: " + (int) (100 * this.catchTreasureChance()) + "%.", CATCH_TREASURE_INDEX)
    );
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}