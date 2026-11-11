package com.avi.skram.perks;

import com.avi.skram.skills.SkillMelee;
import com.avi.skram.skills.SkillStealth;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Map;

public class PerkHamstring extends Perk {
  public static final String NAME = "hamstring";
  public static final String DESCRIPTION = "Melee attacks inflict slowness. Duration is based on damage dealt.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMelee.NAME, 30)
    .put(SkillStealth.NAME, 20)
    .build();
  public static final StreamCodec<ByteBuf, PerkHamstring> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkHamstring::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.PORKCHOP, 128),
      new ItemStack(Items.STRING, 128)
    );
  }

  public PerkHamstring() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkHamstring(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    if (!(event.getSource().getDirectEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    PerkHamstring perk = player.getData(Perks.HAMSTRING);
    if (!perk.isFullyEnabled()) return;

    event.getEntity().addEffect(new MobEffectInstance(
      MobEffects.SLOWNESS, (int) (10 * event.getNewDamage()), 1, false, true
    ));
  }
}
