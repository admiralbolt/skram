package com.avi.skram.skills;

import com.avi.skram.Skram;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

public class SkillHealth extends Skill {

  public static final String NAME = "health";
  public static final StreamCodec<ByteBuf, SkillHealth> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillHealth::new);
  public static final String DESCRIPTION = """
    Health!
    
    More health levels means more health AND more healing from ALL sources.
    Wearing armor also has a health level requirement!
    XP sources include:
     - 4x damage you deal to other entities (10x less for friendly mobs).
     - Any healing you do.
     - 10x damage blocked if you use a shield.
    """;

  public static final Identifier BONUS_HEALTH = Identifier.fromNamespaceAndPath(Skram.MODID, "health_bonus");
  public static final int BONUS_HEALTH_INDEX = 0;
  public static final int BONUS_HEALING_INDEX = 1;

  public SkillHealth() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true};
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
    return (this.level / 3);
  }

  public double getBonusHealing() {
    return 1 - 100.0 / (100 + this.level);
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

  public static void gainHealthXP(LivingDamageEvent.Pre event) {
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
    double xpMult = (!targetEntity.getType().getCategory().isFriendly()) ? 4 : 0.4;
    if (event.getSource().is(DamageTypes.PLAYER_EXPLOSION)) {
      xpMult *= SkillDemolition.EXPLOSION_DAMAGE_XP_MODIFIER;
    }

    health.addXp(player, event.getNewDamage() * xpMult);
  }

  public static void handleShieldBlockEvent(LivingShieldBlockEvent event) {
    if (event.getEntity().level().isClientSide()) return;
    if (!(event.getEntity() instanceof Player player)) return;
    if (event.getBlockedDamage() <= 0) return;

    SkillHealth health = player.getData(Skills.HEALTH);
    health.addXp(player, event.getBlockedDamage() * 10);
  }

  public static void handleHealEvent(LivingHealEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    SkillHealth health = player.getData(Skills.HEALTH);

    if (health.enabledAbilities[BONUS_HEALING_INDEX]) {
      event.setAmount(event.getAmount() * (float) (1 + health.getBonusHealing()));
    }

    // Gain a little XP.
    health.addXp(player, event.getAmount());
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(
      new AbilityDescription("Bonus Health: +" + this.getBonusHealth() + ".", BONUS_HEALTH_INDEX),
      new AbilityDescription("Bonus Healing: +" + (int) (100 * this.getBonusHealing()) + "%.", BONUS_HEALING_INDEX)
    );
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}
