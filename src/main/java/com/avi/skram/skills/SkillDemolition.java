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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class SkillDemolition extends Skill {

  public static final String NAME = "demolition";
  public static final StreamCodec<ByteBuf, SkillDemolition> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillDemolition::new);
  public static final Identifier EXPLOSION_KNOCKBACK_RESISTANCE = Identifier.fromNamespaceAndPath(Skram.MODID, "demolition_knockback_resistance");
  // Explosions deal hella damage, and can hit lots of stuff. Attempting to pre-nerf this.
  public static final double EXPLOSION_DAMAGE_XP_MODIFIER = 0.1;

  public static final int BLAST_POWER_INDEX = 0;
  public static final int BLAST_RESISTANCE_INDEX = 1;
  public static final int BLAST_KNOCKBACK_RESISTANCE_INDEX = 2;

  public SkillDemolition() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true, true};
  }

  public SkillDemolition(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.DEMOLITION, player.getData(Skills.DEMOLITION));
  }

  public float getBlastPower() {
    return 0.04f * this.level;
  }

  public double getBlastResistance() {
    return 1.0 / (1 + 0.04 * this.level);
  }

  public double getBlastKnockbackResistance() {
    return 1 - 1.0 / (1 + 0.02 * this.level);
  }

  public static void applyBlastResistance(LivingDamageEvent.Pre event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (!event.getSource().is(DamageTypes.EXPLOSION) && !event.getSource().is(DamageTypes.PLAYER_EXPLOSION)) return;

    SkillDemolition demolition = player.getData(Skills.DEMOLITION);
    if (!demolition.enabledAbilities[BLAST_RESISTANCE_INDEX]) return;

    event.setNewDamage((float) (event.getNewDamage() * demolition.getBlastResistance()));
  }

  // Encourage the players to blow up entities :)
  public static void handleDamageEvent(LivingDamageEvent.Pre event) {
    if (!event.getSource().is(DamageTypes.PLAYER_EXPLOSION)) return;
    if (!(event.getSource().getEntity() instanceof Player player)) return;
    // No XP for blowing up friendly things.
    if (event.getEntity().getType().getCategory().isFriendly()) return;

    SkillDemolition demolition = player.getData(Skills.DEMOLITION);
    demolition.addXp(player, event.getNewDamage());
  }

  @Override
  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();
    AttributeInstance knockbackResistance = attributes.getInstance(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE);
    if (knockbackResistance == null) return;

    if (this.enabledAbilities[BLAST_KNOCKBACK_RESISTANCE_INDEX]) {
      knockbackResistance.addOrReplacePermanentModifier(new AttributeModifier(
        EXPLOSION_KNOCKBACK_RESISTANCE, this.getBlastKnockbackResistance(), AttributeModifier.Operation.ADD_VALUE
      ));
    } else {
      knockbackResistance.removeModifier(EXPLOSION_KNOCKBACK_RESISTANCE);
    }
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Explosion Power: +" + (int) (100 * this.getBlastPower()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[BLAST_POWER_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(BLAST_POWER_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Explosion Damage Taken: " + (int) (100 * this.getBlastResistance()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[BLAST_RESISTANCE_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(BLAST_RESISTANCE_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Blast Knockback Resistance: " + (int) (100 * this.getBlastKnockbackResistance()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[BLAST_KNOCKBACK_RESISTANCE_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(BLAST_KNOCKBACK_RESISTANCE_INDEX))
        .build()
    );

    return vertical;
  }
}