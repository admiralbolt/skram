package com.avi.skram.perks;

import com.avi.skram.skills.SkillCooking;
import com.avi.skram.skills.SkillHealth;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

import java.util.List;
import java.util.Map;

public class PerkSpeedEater extends Perk {
  public static final String NAME = "speed_eater";
  public static final String DESCRIPTION = "Consume food twice as fast.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillCooking.NAME, 30)
    .put(SkillHealth.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkSpeedEater> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkSpeedEater::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.WATER_BUCKET, 12),
      new ItemStack(Items.BREAD, 64),
      new ItemStack(Items.COOKED_PORKCHOP, 32)
    );
  }

  public PerkSpeedEater() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkSpeedEater(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleEating(LivingEntityUseItemEvent.Tick event) {
    if (!(event.getEntity() instanceof Player player)) return;

    FoodProperties food = event.getItem().get(DataComponents.FOOD);
    if (food == null) return;

    PerkSpeedEater perk = player.getData(Perks.SPEED_EATER);
    if (!perk.isFullyEnabled()) return;

    event.setDuration(event.getDuration() - ((Math.random() <= 0.5) ? 1 : 0));
  }

}
