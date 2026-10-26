package com.avi.skram.skills;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public abstract class Skill implements ValueIOSerializable {

  public static final int XP_FACTOR = 100;

  public static Map<Integer, Integer> XP_FOR_LEVEL = new HashMap<>();

  public static int getTotalXpForLevel(int level) {
    if (level <= 1) return 0;

    if (XP_FOR_LEVEL.containsKey(level)) return XP_FOR_LEVEL.get(level);

    int xpNeeded = XP_FACTOR * (int) (
      0.5 * Math.pow(level, 3) + 1 * Math.pow(level, 2) + 2 * Math.pow(level, 1)
    );

    XP_FOR_LEVEL.put(level, xpNeeded);

    return xpNeeded;
  }

  public String name;
  public int level = 1;
  public double xp = 0;
  public boolean enabled = true;

  public Skill(String name, int level, double xp) {
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
    buffer.writeDouble(this.xp);
    buffer.writeBoolean(this.enabled);
  }

  @Override
  public String toString() {
    return "[" + this.name + "] Lvl: " + this.level + ". XP: " + (int) this.xp + " / " + Skill.getTotalXpForLevel(this.level + 1) + ". Enabled: " + this.enabled;
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

  // Overload that assumes server side processing. If you want to do client side processing, make sure that you specify
  // syncToServer = true.
  public void addXp(Player player, double xp) {
    addXp(player, xp, false);
  }

  public void addXp(Player player, double xp, boolean syncToServer) {
    if (xp <= 0) return;

    this.xp += (1 + 0.01 * player.experienceLevel) * xp;
    while (this.xp > Skill.getTotalXpForLevel(this.level + 1)) {
      this.level++;
      this.onLevelChange(player);
    }

    // Make sure to call setData() so changes persist.
    // Calling setData() will automatically sync data from server -> client.
    player.setData(Skills.SKILLS_BY_NAME.get(this.name), this);

    // If we want to sync from client -> server we need to explicitly send a packet.
    if (syncToServer) {
      ClientPacketDistributor.sendToServer(new SkillPacket(this.name, this.level, this.xp, this.enabled));
    }
  }

  public void onLevelChange(Player player) {
    return;
  }

  public void toggleEnabled() {
    this.enabled = !this.enabled;
  }

}
