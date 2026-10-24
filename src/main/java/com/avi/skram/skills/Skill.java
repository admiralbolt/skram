package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.nio.charset.Charset;

public class Skill implements ValueIOSerializable {

  public static final int XP_FACTOR = 100;

  public String name;
  public int level = 1;
  public double xp = 0;
  public boolean enabled = true;

  // Default constructor for level 1 / 0 xp.
  public Skill(String name) {
    this(name, 1, 0);
  }

  public Skill(String name, int level, int xp) {
    this.name = name;
    this.level = level;
    this.xp = xp;
  }

  public Skill(ByteBuf buffer) {
    int nameLength = buffer.readInt();
    this.name = buffer.readString(nameLength, Charset.defaultCharset());
    this.level = buffer.readInt();
    this.xp = buffer.readDouble();
    this.enabled = buffer.readBoolean();
  }

  public void encode(ByteBuf buffer) {
    buffer.writeInt(this.name.length());
    buffer.writeCharSequence(this.name, Charset.defaultCharset());
    buffer.writeInt(this.level);
    buffer.writeDouble(this.level);
    buffer.writeBoolean(this.enabled);
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putString("name", this.name);
    output.putInt("level", this.level);
    output.putDouble("xp", this.xp);
    output.putBoolean("enabled", this.enabled);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.name = input.getStringOr("name", "die");
    this.level = input.getIntOr("level", -1);
    this.xp = input.getDoubleOr("xp", -2.2);
    this.enabled = input.getBooleanOr("enabled", true);
  }

  public void addXp(Player player, double xp) {
    if (xp <= 0) return;

    this.xp += (1 + 0.01 * player.experienceLevel) * xp;
  }

  public void toggleEnabled() {
    this.enabled = !this.enabled;
  }

}
