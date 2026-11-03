package com.avi.skram.perks;

import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.nio.charset.Charset;
import java.util.Map;

public abstract class Perk implements ValueIOSerializable {

  // As far as serialization goes, the only values that can change are really the discovered/researched values.
  public String name;
  public String description;
  // Perks are unlocked via a two-step process, first discovery, then research.
  public boolean discovered;
  public boolean researched;
  public boolean enabled;

  // Each perk has governing skills. These skills have required minimum levels before the perk can be researched.
  public Map<String, Integer> requiredSkills;
  // Each perk also has a required research cost. These rely on ItemStacks being available, so they are unfortunately
  // tracked separately. See cost info in Perks.java.

  public Perk(String name, String description, Map<String, Integer> requiredSkills) {
    this.name = name;
    this.description = description;
    this.discovered = false;
    this.researched = false;
    this.enabled = true;
    this.requiredSkills = requiredSkills;
  }

  public Perk(ByteBuf buffer) {
    int nameLength = buffer.readInt();
    this.name = buffer.readString(nameLength, Charset.defaultCharset());
    this.discovered = buffer.readBoolean();
    this.researched = buffer.readBoolean();
    this.enabled = buffer.readBoolean();
  }

  public void encode(ByteBuf buffer) {
    buffer.writeInt(this.name.length());
    buffer.writeCharSequence(this.name, Charset.defaultCharset());
    buffer.writeBoolean(this.discovered);
    buffer.writeBoolean(this.researched);
    buffer.writeBoolean(this.enabled);
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putString("name", this.name);
    output.putBoolean("discovered", this.discovered);
    output.putBoolean("researched", this.researched);
    output.putBoolean("enabled", this.enabled);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.name = input.getStringOr("name", "DIE");
    this.discovered = input.getBooleanOr("discovered", false);
    this.researched = input.getBooleanOr("researched", false);
    this.enabled = input.getBooleanOr("enabled", false);
  }

  // Override this if the perk applies any static ability modifiers to the player.
  public void applyModifiers(Player player) {
    return;
  }

  public boolean canResearch(Player player) {
    for (Map.Entry<String, Integer> entry : requiredSkills.entrySet()) {
      Skill skill = (Skill) player.getData(Skills.SKILLS_BY_NAME.get(entry.getKey()));
      if (skill.level < entry.getValue()) return false;
    }

    return true;
  }

}
