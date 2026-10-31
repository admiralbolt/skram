package com.avi.skram.skills;

import com.avi.skram.Skram;
import com.avi.skram.utils.ReflectionUtils;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class SkillStealth extends Skill {

  public static final String NAME = "stealth";
  public static final StreamCodec<ByteBuf, SkillStealth> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillStealth::new);

  public static final Identifier SNEAKING_SPEED_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "stealth_sneaking_speed_boost");
  public static final double MAX_SNEAKING_SPEED = 1.0;
  public static final double BASE_SNEAKING_SPEED = 0.3;

  public static final int SNEAKING_SPEED_INDEX = 0;
  public static final int VISIBILITY_INDEX = 1;

  public SkillStealth() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true};
  }

  public SkillStealth(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.STEALTH, player.getData(Skills.STEALTH));
  }

  public double getSneakingSpeedBoost() {
    return 0.02 * this.level;
  }

  public double getVisibilityModifier() {
    return 0.04 * this.level;
  }

  @Override
  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();
    AttributeInstance sneakingSpeed = attributes.getInstance(Attributes.SNEAKING_SPEED);
    if (sneakingSpeed == null) return;

    if (this.enabledAbilities[SNEAKING_SPEED_INDEX]) {
      double sneakBoost = (MAX_SNEAKING_SPEED - BASE_SNEAKING_SPEED) * (1 - (1 / (1 + this.getSneakingSpeedBoost())));
      sneakingSpeed.addOrReplacePermanentModifier(new AttributeModifier(
        SNEAKING_SPEED_BOOST, sneakBoost, AttributeModifier.Operation.ADD_VALUE
      ));
    } else {
      sneakingSpeed.removeModifier(SNEAKING_SPEED_BOOST);
    }
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

    // Check to see if the target was unaware of us. Only Mobs have targeting information.
    if (!(event.getEntity() instanceof Mob mob)) return;
    // No stealth XP for friendly mobs.
    if (mob.getType().getCategory().isFriendly()) return;
    if (mob.getTarget() != null && mob.getTarget().equals(player)) return;

    // Get the IntSet "seen" from mob sensing.
    try {
      IntSet seen = (IntSet) ReflectionUtils.getFieldValue(mob.getSensing(), "seen");
      if (seen.contains(player.getId())) return;
    } catch (NoSuchFieldException | IllegalAccessException e) {
      // Die.
      return;
    }

    SkillStealth stealth = player.getData(Skills.STEALTH);
    // Modify the XP gained based on the distance between the player and the mob they hit.
    // Nerfing stealth archer even in pre-release.
    float distanceBetween = mob.distanceTo(player);
    double xpMult = 12 / Math.max(1, distanceBetween);

    if (event.getSource().is(DamageTypes.PLAYER_EXPLOSION)) {
      xpMult *= SkillDemolition.EXPLOSION_DAMAGE_XP_MODIFIER;
    }

    stealth.addXp(player, event.getNewDamage() * xpMult);
  }

  public static void applyVisibility(LivingEvent.LivingVisibilityEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (!player.isDiscrete()) return;

    SkillStealth stealth = player.getData(Skills.STEALTH);
    if (!stealth.enabledAbilities[VISIBILITY_INDEX]) return;
    event.modifyVisibility(1.0 / (1 + stealth.getVisibilityModifier()));
  }

  @Override
  public void onLevelChange(Player player) {
    super.onLevelChange(player);
    this.applyModifiers(player);
  }

  @Override
  public Layout renderSkillAbilities() {
    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(1).defaultCellSetting().paddingBottom(2);

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Sneaking Speed: +" + (int) (100 * this.getSneakingSpeedBoost()) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[SNEAKING_SPEED_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(SNEAKING_SPEED_INDEX))
        .build()
    );

    vertical.addChild(
      Checkbox.builder(
          Component.literal("Detection Range: " + (int) (100.0 / (1 + this.getVisibilityModifier())) + "%."),
          Minecraft.getInstance().font
        )
        .selected(this.enabledAbilities[VISIBILITY_INDEX])
        .onValueChange((checkbox, value) -> this.toggleEnabled(VISIBILITY_INDEX))
        .build()
    );

    return vertical;
  }
}
