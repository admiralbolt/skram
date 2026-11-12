package com.avi.skram.perks;

import com.avi.skram.skills.SkillCooking;
import com.avi.skram.skills.SkillHealth;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

import java.util.List;
import java.util.Map;

public class PerkGlutton extends Perk {
  public static final String NAME = "glutton";
  public static final String DESCRIPTION = "Eating food now applies regeneration and hunger.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillCooking.NAME, 20)
    .put(SkillHealth.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkGlutton> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkGlutton::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.BREAD, 320),
      new ItemStack(Items.BAKED_POTATO, 320),
      new ItemStack(Items.COOKED_BEEF, 128)
    );
  }

  public PerkGlutton() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkGlutton(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleEat(LivingEntityUseItemEvent.Finish event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;
    FoodProperties food = event.getItem().get(DataComponents.FOOD);
    if (food == null) return;

    PerkGlutton perk = player.getData(Perks.GLUTTON);
    if (!perk.isFullyEnabled()) return;

    double durationMultiplier = Math.max(0.5, (food.nutrition() + food.saturation()) / 15.0);
    double hungerMultiplier = Math.max(1.0, (food.nutrition() + food.saturation()) / 20.0);

    player.addEffect(new MobEffectInstance(MobEffects.HUNGER, (int) (120 * durationMultiplier), (int) (hungerMultiplier * 40), false, true));
    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, (int) (80 * durationMultiplier), ((food.nutrition() + food.saturation()) > 10) ? 1 : 0, false, true));
  }
}
