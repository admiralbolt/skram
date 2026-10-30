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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class SkillMelee extends Skill {

  public static final String NAME = "melee";
  public static final StreamCodec<ByteBuf, SkillMelee> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillMelee::new);

  public static final Identifier ATTACK_SPEED_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "melee_attack_speed_boost");
  public static final float ATTACK_SPEED_BASE_VALUE = 1.6f;

  public static final int DAMAGE_BOOST_INDEX = 0;
  public static final int ATTACK_SPEED_INDEX = 1;

  public SkillMelee() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true};
  }

  public SkillMelee(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.MELEE, player.getData(Skills.MELEE));
  }

  public double getDamageBoost() {
    return 0.01f * this.level;
  }

  public double getAttackSpeed() {
    return 0.01f * this.level;
  }

  @Override
  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();

    AttributeInstance attackSpeed = attributes.getInstance(Attributes.ATTACK_SPEED);
    if (attackSpeed == null) return;
    if (this.enabledAbilities[ATTACK_SPEED_INDEX]) {
      attackSpeed.addOrReplacePermanentModifier(new AttributeModifier(
        ATTACK_SPEED_BOOST, this.getAttackSpeed(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
      ));
    } else {
      attackSpeed.removeModifier(ATTACK_SPEED_BOOST);
    }
  }

  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    if (!(event.getSource().getDirectEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    SkillMelee melee = player.getData(Skills.MELEE);
    if (melee.enabledAbilities[DAMAGE_BOOST_INDEX]) {
      event.setNewDamage(event.getOriginalDamage() * (float) (1 + melee.getDamageBoost()));
    }

    LivingEntity targetEntity = event.getEntity();
    double xpMult = (!targetEntity.getType().getCategory().isFriendly()) ? 5 : 1;

    melee.addXp(player, (int) (event.getNewDamage() * xpMult));
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
          Component.literal("Attack Speed: +" + (int) (100 * this.getAttackSpeed()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[ATTACK_SPEED_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(ATTACK_SPEED_INDEX))
        .build()
    );

    return vertical;
  }
}