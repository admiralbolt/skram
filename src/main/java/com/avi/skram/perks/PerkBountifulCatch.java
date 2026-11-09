package com.avi.skram.perks;

import com.avi.skram.skills.SkillFishing;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

import java.util.List;
import java.util.Map;

public class PerkBountifulCatch extends Perk {
  public static final String NAME = "bountiful_catch";
  public static final String DESCRIPTION = "Fishing grants +3 XP per catch.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillFishing.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkBountifulCatch> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkBountifulCatch::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.COD, 128),
      new ItemStack(Items.SALMON, 128),
      new ItemStack(Items.PUFFERFISH, 64),
      new ItemStack(Items.TROPICAL_FISH, 64)
    );
  }

  public PerkBountifulCatch() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkBountifulCatch(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleCatchFish(ItemFishedEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) return;

    PerkBountifulCatch perk = player.getData(Perks.BOUNTIFUL_CATCH);
    if (!perk.isFullyEnabled()) return;

    player.level().addFreshEntity(
      new ExperienceOrb(player.level(), player.getX(), player.getY() + 0.5, player.getZ() + 0.5, 3)
    );
  }
}
