package com.avi.skram.perks;

import com.avi.skram.skills.SkillAthletics;
import com.avi.skram.skills.SkillMelee;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Map;

public class PerkPugilist extends Perk {
  public static final String NAME = "pugilist";
  public static final String DESCRIPTION = "Unarmed melee attacks deal +10 damage.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillAthletics.NAME, 40)
    .put(SkillMelee.NAME, 30)
    .build();
  public static final StreamCodec<ByteBuf, PerkPugilist> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkPugilist::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.LEATHER, 256)
    );
  }

  public PerkPugilist() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkPugilist(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    if (!(event.getSource().getDirectEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;
    if (player.getMainHandItem().getItem() != Items.AIR) return;

    PerkPugilist perk = player.getData(Perks.PUGILIST);
    if (!perk.isFullyEnabled()) return;

    event.setNewDamage(event.getNewDamage() + 10);
    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.7f, 0.25f);
  }
}
