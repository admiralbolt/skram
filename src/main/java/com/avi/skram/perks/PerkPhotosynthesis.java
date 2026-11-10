package com.avi.skram.perks;

import com.avi.skram.skills.SkillBotany;
import com.avi.skram.skills.SkillFarming;
import com.avi.skram.skills.SkillWoodcutting;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Map;

public class PerkPhotosynthesis extends Perk {

  public static final String NAME = "photosynthesis";
  public static final String DESCRIPTION = "Heal slowly while in sunlight.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillBotany.NAME, 25)
    .put(SkillFarming.NAME, 25)
    .put(SkillWoodcutting.NAME, 25)
    .build();
  public static final StreamCodec<ByteBuf, PerkPhotosynthesis> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkPhotosynthesis::new);

  public static final float HEAL_EVERY_SECOND = 30;
  public static final float HEAL_PER = 1.0f / (HEAL_EVERY_SECOND * 20);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.SUNFLOWER, 256)
    );
  }

  public PerkPhotosynthesis() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkPhotosynthesis(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void healInSunlight(PlayerTickEvent.Pre event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) return;

    PerkPhotosynthesis perk = player.getData(Perks.PHOTOSYNTHESIS);
    if (!perk.isFullyEnabled()) return;
    if (player.level().isDarkOutside()) return;

    BlockPos pos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
    if (!player.level().canSeeSky(pos)) return;

    player.heal(HEAL_PER);
  }
}
