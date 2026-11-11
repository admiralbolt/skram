package com.avi.skram.perks;

import com.avi.skram.skills.SkillHusbandry;
import com.avi.skram.skills.SkillMelee;
import com.avi.skram.skills.SkillRanged;
import com.avi.skram.skills.SkillStealth;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PerkEggSnatcher extends Perk {
  public static final String NAME = "egg_snatcher";
  public static final String DESCRIPTION = "ANY entity killed has a 6% chance of dropping their spawn egg.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMelee.NAME, 30)
    .put(SkillRanged.NAME, 30)
    .put(SkillHusbandry.NAME, 40)
    .put(SkillStealth.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkEggSnatcher> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkEggSnatcher::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.EGG, 48),
      new ItemStack(Items.BROWN_EGG, 48),
      new ItemStack(Items.BLUE_EGG, 48),
      new ItemStack(Items.TURTLE_EGG, 1),
      new ItemStack(Items.SNIFFER_EGG, 1)
    );
  }

  public PerkEggSnatcher() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkEggSnatcher(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void harvestEggs(LivingDropsEvent event) {
    Player player = null;
    if (event.getSource().getEntity() instanceof Player) {
      player = (Player) event.getSource().getEntity();
    } else if (event.getSource().getDirectEntity() instanceof Player) {
      player = (Player) event.getSource().getDirectEntity();
    } else {
      return;
    }

    if (player.level().isClientSide()) return;

    PerkEggSnatcher perkEggSnatcher = player.getData(Perks.EGG_SNATCHER);
    if (!perkEggSnatcher.isFullyEnabled()) return;
    if (Math.random() > 0.06) return;

    Optional<Holder<Item>> holder = SpawnEggItem.byId(event.getEntity().getType());
    if (holder.isEmpty()) return;

    ItemStack stack = new ItemStack(holder.get().value());
    event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack));
  }

}
