package com.avi.skram.perks;

import com.avi.skram.effect.SkramEffects;
import com.avi.skram.skills.SkillMelee;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Map;

public class PerkTeamwork extends Perk {
  public static final String NAME = "teamwork";
  public static final String DESCRIPTION = "Melee attacks inflict flanked, increasing damage from other players by 25%. Duration is based on damage dealt.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillMelee.NAME, 10)
    .build();
  public static final StreamCodec<ByteBuf, PerkTeamwork> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkTeamwork::new);

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.LEAD, 1)
    );
  }

  public PerkTeamwork() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkTeamwork(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    if (!(event.getSource().getDirectEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    PerkTeamwork perk = player.getData(Perks.TEAMWORK);
    if (!perk.isFullyEnabled()) return;

    event.getEntity().addEffect(new MobEffectInstance(
      SkramEffects.FLANKED, 80 + (int) (10 * event.getNewDamage()), 0, false, true
    ));
  }
}
