package com.avi.skram.skills;

import com.avi.skram.Skram;
import com.avi.skram.magic.SkramMagic;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class SkillMagic extends Skill {

  public static final String NAME = "magic";
  public static final StreamCodec<ByteBuf, SkillMagic> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillMagic::new);
  public static final String DESCRIPTION = """
    Magic IS real!
    
    Spells can be discovered as loot from most chests, and from a few different magical mobs (Witches, Evokers, Guardian & Elder Guardian).
    Casting a spell takes a lot of concentration, so it's interrupted by many things (not sprinting or jumping though!).
    
    Magic XP is gained by casting a spell with a formula based on the level of spell & total mana used:
      10 + (requiredLevel / 3) + (manaCost / 5)
    """;

  public static final Identifier MANA_CAPACITY_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "magic_mana_capacity_boost");
  public static final Identifier MANA_REGENERATION_BOOST = Identifier.fromNamespaceAndPath(Skram.MODID, "magic_mana_regeneration_boost");

  public static final int MANA_REGEN_INDEX = 0;
  public static final int MANA_CAPACITY_INDEX = 1;
  public static final int SPELL_POWER_INDEX = 2;

  public SkillMagic() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true, true};
  }

  public SkillMagic(ByteBuf buffer) {
    super(buffer);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.MAGIC, player.getData(Skills.MAGIC));
  }

  public double getManaRegen() {
    return 0.02 * this.level;
  }

  public double getManaCapacity() {
    return 50 * this.level;
  }

  public double getSpellPower() {
    return 0.01 * this.level;
  }

  @Override
  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();

    AttributeInstance capacityBoost = attributes.getInstance(SkramMagic.MAX_MANA);
    if (capacityBoost != null) {
      if (this.enabledAbilities[MANA_CAPACITY_INDEX]) {
        capacityBoost.addOrReplacePermanentModifier(new AttributeModifier(
          MANA_CAPACITY_BOOST, this.getManaCapacity(), AttributeModifier.Operation.ADD_VALUE
        ));
      } else {
        capacityBoost.removeModifier(MANA_CAPACITY_BOOST);
      }
    }

    AttributeInstance regenerationBoost = attributes.getInstance(SkramMagic.MANA_REGENERATION);
    if (regenerationBoost != null) {
      if (this.enabledAbilities[MANA_REGEN_INDEX]) {
        regenerationBoost.addOrReplacePermanentModifier(new AttributeModifier(
          MANA_REGENERATION_BOOST, this.getManaRegen(), AttributeModifier.Operation.ADD_VALUE
        ));
      } else {
        regenerationBoost.removeModifier(MANA_REGENERATION_BOOST);
      }
    }
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(
      new AbilityDescription("Mana Regen: +" + String.format("%.2f", this.getManaRegen()), MANA_REGEN_INDEX),
      new AbilityDescription("Mana Capacity: +" + this.getManaCapacity(), MANA_CAPACITY_INDEX),
      new AbilityDescription("Spell Power: +" + (int) (100 * this.getSpellPower()) + "%", SPELL_POWER_INDEX)
    );
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}