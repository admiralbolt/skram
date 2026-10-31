package com.avi.skram.skills;

import com.avi.skram.utils.MathUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public class SkillRanged extends Skill {

  public static final String NAME = "ranged";
  public static final StreamCodec<ByteBuf, SkillRanged> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillRanged::new);

  public static final int DAMAGE_BOOST_INDEX = 0;
  public static final int DRAW_SPEED_INDEX = 1;

  public SkillRanged() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true};
  }

  public SkillRanged(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.RANGED, player.getData(Skills.RANGED));
  }

  public double getDamageBoost() {
    return 0.01f * this.level;
  }

  public double getDrawSpeed() {
    return 0.01 * this.level;
  }

  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    if (!(event.getSource().getDirectEntity() instanceof Projectile)) return;
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    SkillRanged ranged = player.getData(Skills.RANGED);
    if (ranged.enabledAbilities[DAMAGE_BOOST_INDEX]) {
      event.setNewDamage(event.getOriginalDamage() * (float) (1 + ranged.getDamageBoost()));
    }

    LivingEntity targetEntity = event.getEntity();
    double xpMult = (!targetEntity.getType().getCategory().isFriendly()) ? 10 : 2;

    ranged.addXp(player, event.getNewDamage() * xpMult);
  }

  public static void applyDrawSpeed(LivingEntityUseItemEvent.Tick event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (!(event.getItem().getItem() instanceof ProjectileWeaponItem)) return;

    SkillRanged ranged = player.getData(Skills.RANGED);
    if (!ranged.enabledAbilities[DRAW_SPEED_INDEX]) return;

    event.setDuration(event.getDuration() - MathUtils.getScalingRandom(ranged.getDrawSpeed()));
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Damage Boost: +" + (int) (100 * this.getDamageBoost()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[DAMAGE_BOOST_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(DAMAGE_BOOST_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Draw Speed: +" + (int) (100 * this.getDrawSpeed()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[DRAW_SPEED_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(DRAW_SPEED_INDEX))
        .build()
    );

    return vertical;
  }
}
