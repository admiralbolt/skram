package com.avi.skram.perks;

import com.avi.skram.skills.*;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;

public class PerkBlastMining extends Perk {

  public static final int XP_PERCENT = 20;

  public static final String NAME = "blast_mining";
  public static final String DESCRIPTION = "Gain " + XP_PERCENT + "% of the normal XP for each block you blow up.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillDemolition.NAME, 30)
    .put(SkillDigging.NAME, 20)
    .put(SkillMining.NAME, 20)
    .put(SkillWoodcutting.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkBlastMining> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkBlastMining::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Blocks.TNT, 64),
      new ItemStack(Items.DIAMOND_PICKAXE, 4),
      new ItemStack(Items.DIAMOND_SHOVEL, 4),
      new ItemStack(Items.DIAMOND_AXE, 4)
    );
  }

  public PerkBlastMining() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkBlastMining(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleBlockExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
    Player player = null;
    if (explosion.getDirectSourceEntity() instanceof Player) {
      player = (Player) explosion.getDirectSourceEntity();
    } else if (explosion.getIndirectSourceEntity() instanceof Player) {
      player = (Player) explosion.getIndirectSourceEntity();
    }
    if (player == null) return;

    PerkBlastMining perk = player.getData(Perks.BLAST_MINING);
    if (!perk.isFullyEnabled()) return;

    SkillDigging digging = player.getData(Skills.DIGGING);
    SkillMining mining = player.getData(Skills.MINING);
    SkillWoodcutting woodcutting = player.getData(Skills.WOODCUTTING);

    double mult = ((double) XP_PERCENT) / 100.0;
    Block block = level.getBlockState(pos).getBlock();
    digging.addXp(player, SkillDigging.XP_MAP.getOrDefault(block, 0) * mult);
    mining.addXp(player, SkillMining.XP_MAP.getOrDefault(block, 0) * mult);
    woodcutting.addXp(player,  SkillWoodcutting.XP_MAP.getOrDefault(block, 0) * mult);
  }

}
