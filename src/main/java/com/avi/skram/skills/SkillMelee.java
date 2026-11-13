package com.avi.skram.skills;

import com.avi.skram.Skram;
import com.avi.skram.client.SkramClientSkillHooks;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.util.Set;

public class SkillMelee extends Skill {

  public static final String NAME = "melee";
  public static final StreamCodec<ByteBuf, SkillMelee> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillMelee::new);
  public static final String DESCRIPTION = """
    Shlice.
    
    XP Gained is 10x damage dealt (with a 10x penalty for friendly mobs).
    """;

  public static final Identifier ATTACK_SPEED_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "melee_attack_speed_boost");

  public static final int DAMAGE_BOOST_INDEX = 0;
  public static final int ATTACK_SPEED_INDEX = 1;

  public static final Set<TagKey<Item>> ALL_WEAPON_TYPES = Set.of(
    ItemTags.SWORDS,
    ItemTags.SPEARS,
    ItemTags.AXES,
    ItemTags.SHOVELS,
    ItemTags.PICKAXES,
    ItemTags.TRIDENT_ENCHANTABLE,
    ItemTags.MACE_ENCHANTABLE
  );

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
    // Reliquary implemented their entities backwards for the handgun :/
    if (event.getSource().getEntity() instanceof Projectile) return;
    if (player.level().isClientSide()) return;

    SkillMelee melee = player.getData(Skills.MELEE);
    if (melee.enabledAbilities[DAMAGE_BOOST_INDEX]) {
      event.setNewDamage(event.getNewDamage() * (float) (1 + melee.getDamageBoost()));
    }
  }

  public static void gainMeleeXP(LivingDamageEvent.Pre event) {
    if (!(event.getSource().getDirectEntity() instanceof Player player)) return;
    if (event.getSource().getEntity() instanceof Projectile) return;
    if (player.level().isClientSide()) return;

    SkillMelee melee = player.getData(Skills.MELEE);
    LivingEntity targetEntity = event.getEntity();
    double xpMult = (!targetEntity.getType().getCategory().isFriendly()) ? 10 : 1;

    melee.addXp(player, event.getNewDamage() * xpMult);
  }

  public static void handleAttackEvent(AttackEntityEvent event) {
    Player player = event.getEntity();
    if (player.isCreative()) return;

    ItemStack usedItem = player.getMainHandItem();
    if (usedItem.isEmpty()) return;

    if (ALL_WEAPON_TYPES.stream().noneMatch(usedItem::is)) return;

    SkillMelee melee = player.getData(Skills.MELEE);
    int requiredLevel = Skills.requiredLevel(usedItem);
    if (melee.level >= requiredLevel) return;

    event.setCanceled(true);
    if (!player.level().isClientSide()) return;
    SkramClientSkillHooks.showRequiredLevelToast(melee, "Using a " + usedItem.getItemName().getString() + " requires a " + melee.name + " level of " + requiredLevel + ".");
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(
      new AbilityDescription("Damage Boost: +" + (int) (100 * this.getDamageBoost()) + "%.", DAMAGE_BOOST_INDEX),
      new AbilityDescription("Attack Speed: +" + (int) (100 * this.getAttackSpeed()) + "%.", ATTACK_SPEED_INDEX)
    );
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}