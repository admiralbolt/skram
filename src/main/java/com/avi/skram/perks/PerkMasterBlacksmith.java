package com.avi.skram.perks;

import com.avi.skram.skills.SkillSmithing;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

import java.util.List;
import java.util.Map;

public class PerkMasterBlacksmith extends Perk {

  public static final String NAME = "master_blacksmith";
  public static final String DESCRIPTION = "Significantly reduce prior work cost when repairing items on an Anvil.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillSmithing.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkMasterBlacksmith> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkMasterBlacksmith::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.COPPER_INGOT, 256),
      new ItemStack(Items.IRON_INGOT, 128),
      new ItemStack(Items.GOLD_INGOT, 64)
    );
  }

  public PerkMasterBlacksmith() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkMasterBlacksmith(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleAnvilUpdate(AnvilUpdateEvent event) {
    ItemStack left = event.getLeft();
    Player player = event.getPlayer();

    PerkMasterBlacksmith perk = player.getData(Perks.MASTER_BLACKSMITH);
    if (!perk.isFullyEnabled()) return;

    int baseCost = left.getOrDefault(DataComponents.REPAIR_COST, 0);
    event.getOutput().set(DataComponents.REPAIR_COST, baseCost + 1);
  }

}
