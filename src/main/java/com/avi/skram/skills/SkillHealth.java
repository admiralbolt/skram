package com.avi.skram.skills;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class SkillHealth extends Skill {

  public static final String NAME = "health";
  public static final StreamCodec<ByteBuf, SkillHealth> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillHealth::new);

  public static final Identifier BONUS_HEALTH = Identifier.fromNamespaceAndPath(Skram.MODID, "health_bonus");
  public static final int BONUS_HEALTH_INDEX = 0;

  public SkillHealth() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true};
  }

  public SkillHealth(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.HEALTH, player.getData(Skills.HEALTH));
  }

  public int getBonusHealth() {
    return (this.level / 5);
  }

  @Override
  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();
    AttributeInstance maxHealth = attributes.getInstance(Attributes.MAX_HEALTH);
    if (maxHealth == null) return;

    if (this.enabledAbilities[BONUS_HEALTH_INDEX]) {
      maxHealth.addOrReplacePermanentModifier(new AttributeModifier(
        BONUS_HEALTH, this.getBonusHealth(), AttributeModifier.Operation.ADD_VALUE
      ));
    } else {
      maxHealth.removeModifier(BONUS_HEALTH);
    }
  }

  @Override
  public void onLevelChange(Player player) {
    super.onLevelChange(player);
    this.applyModifiers(player);
  }

  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    Player player = null;
    if (event.getSource().getEntity() instanceof Player) {
      player = (Player) event.getSource().getEntity();
    } else if (event.getSource().getDirectEntity() instanceof Player) {
      player = (Player) event.getSource().getDirectEntity();
    } else {
      return;
    }

    if (player.level().isClientSide()) return;

    SkillHealth health = player.getData(Skills.HEALTH);

    LivingEntity targetEntity = event.getEntity();
    double xpMult = (!targetEntity.getType().getCategory().isFriendly()) ? 2 : 0.4;

    health.addXp(player, event.getNewDamage() * xpMult);
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Bonus Health: +" + this.getBonusHealth() + " health."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[BONUS_HEALTH_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(BONUS_HEALTH_INDEX))
        .build()
    );

    return vertical;
  }
}
