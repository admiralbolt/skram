package com.avi.skram.skills;

import com.avi.skram.client.SkramClientSkillHooks;
import com.avi.skram.perks.Perks;
import io.netty.buffer.ByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public abstract class Skill implements ValueIOSerializable {

  public static final int XP_FACTOR = 100;
  public static Identifier YELLOW_BAR = Identifier.withDefaultNamespace("textures/gui/sprites/boss_bar/yellow_progress.png");
  public static Identifier YELLOW_BAR_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/sprites/boss_bar/yellow_background.png");
  public static Identifier WHITE_BAR = Identifier.withDefaultNamespace("textures/gui/sprites/boss_bar/white_progress.png");

  public static double PERK_CHANCE = 100000;
  public static double XP_MULT_PER_LEVEL = 0.0005;

  public static Map<Integer, Integer> XP_FOR_LEVEL = new HashMap<>();

  // Probably unnecessary optimization, but cache this value so we don't concat this string all the time.
  public String requiredLevelToastName = "";

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
  public double xpMultiplier = 1;
  // For portability, we track if the ith ability of a skill is enabled by checking enabledAbilities[i]. This means
  // each instantiated skill is responsible for keeping an appropriate mapping and order of abilities.
  public boolean[] enabledAbilities;

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
    this.xpMultiplier = buffer.readDouble();
    this.enabledAbilities = new boolean[buffer.readInt()];
    for (int i = 0; i < this.enabledAbilities.length; ++i) {
      this.enabledAbilities[i] = buffer.readBoolean();
    }
  }

  public void encode(ByteBuf buffer) {
    buffer.writeInt(this.name.length());
    buffer.writeCharSequence(this.name, Charset.defaultCharset());
    buffer.writeInt(this.level);
    buffer.writeDouble(this.xp);
    buffer.writeDouble(this.xpMultiplier);
    buffer.writeInt(this.enabledAbilities.length);
    for (boolean enabledAbility : this.enabledAbilities) {
      buffer.writeBoolean(enabledAbility);
    }
  }

  @Override
  public String toString() {
    return "[" + this.name + "] Lvl: " + this.level + ". XP: " + (int) this.xp + " / " + Skill.getTotalXpForLevel(this.level + 1) + ". Enabled: " + this.enabledAbilities;
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putString("name", this.name);
    output.putInt("level", this.level);
    output.putDouble("xp", this.xp);
    output.putDouble("xpMultiplier", this.xpMultiplier);
    // Doesn't support a bool array by default, just convert to int.
    output.putIntArray("enabledAbilities", IntStream.range(0, this.enabledAbilities.length).map(i -> this.enabledAbilities[i] ? 1 : 0).toArray());
  }

  @Override
  public void deserialize(ValueInput input) {
    this.name = input.getStringOr("name", "die");
    this.level = input.getIntOr("level", -1);
    this.xp = input.getDoubleOr("xp", -2.2);
    this.xpMultiplier = input.getDoubleOr("xpMultiplier", 1);
    // Convert back from an int array to a boolean one.
    int[] enabled = input.getIntArray("enabledAbilities").get();
    boolean[] boolArray = new boolean[enabled.length];

    for (int i = 0; i < enabled.length; ++i) {
      boolArray[i] = (enabled[i] == 1);
    }

    this.enabledAbilities = boolArray;
  }

  public String getRequiredLevelToastName() {
    if (this.requiredLevelToastName.isEmpty()) {
      this.requiredLevelToastName = this.name + ":required_level";
    }
    return this.requiredLevelToastName;
  }

  // I don't know how expensive looking up data attachments are, but will optimize if this is too slow.
  public double getTotalXpMultiplier(Player player) {
    int totalLevels = 0;
    for (Supplier supplier : Skills.SKILLS_BY_NAME.values()) {
      Skill skill = (Skill) player.getData(supplier);
      totalLevels += skill.level;
    }

    return this.xpMultiplier + totalLevels * XP_MULT_PER_LEVEL;
  }

  // Overload that assumes server side processing. If you want to do client side processing, make sure that you specify
  // syncToServer = true.
  public void addXp(Player player, double xp) {
    addXp(player, xp, false);
  }

  public void addXp(Player player, double xp, boolean syncToServer) {
    if (xp <= 0) return;

    double gainedXp = xp * this.getTotalXpMultiplier(player);
    this.xp += gainedXp;

    // Whenever we gain xp, we want to give a chance of discovering a relevant perk.
    if (Math.random() < (gainedXp / PERK_CHANCE)) {
      Perks.discoverRandomPerk(player, this, syncToServer);
    }

    boolean leveledUp = false;
    while (this.xp > Skill.getTotalXpForLevel(this.level + 1)) {
      leveledUp = true;
      this.level++;
    }

    if (leveledUp) {
      this.onLevelChange(player);
    }

    // Make sure to call setData() so changes persist.
    // Calling setData() will automatically sync data from server -> client.
    player.setData(Skills.SKILLS_BY_NAME.get(this.name), this);

    // If we want to sync from client -> server we need to explicitly send a packet.
    if (syncToServer) {
      SkramClientSkillHooks.notifyXpGain(this.name, xp);
      SkramClientSkillHooks.sendSkillToServer(this);
    }
  }

  public void onLevelChange(Player player) {
    // If we are on the client side, display the level up toast!
    if (player.level().isClientSide()) {
      SkramClientSkillHooks.showLevelUpToast(this);
    }

    this.applyModifiers(player);
  }

  public void applyModifiers(Player player) {
    return;
  }

  public void toggleEnabled(int i) {
    this.enabledAbilities[i] = !this.enabledAbilities[i];
    SkramClientSkillHooks.sendSkillToServer(this);
  }

  public <T extends PlayerInteractEvent & ICancellableEvent> void requiredLevelCheck(T event) {
    ItemStack usedItem = event.getItemStack();
    if (usedItem == ItemStack.EMPTY) return;
    if (event.getEntity().isCreative()) return;

    int requiredLevel = Skills.requiredLevel(usedItem);
    if (this.level >= requiredLevel) return;

    event.setCanceled(true);
    if (!event.getLevel().isClientSide()) return;
    SkramClientSkillHooks.showRequiredLevelToast(this, "Using a " + usedItem.getItemName().getString() + " requires a " + this.name + " level of " + requiredLevel + ".");
  }

  public record AbilityDescription(String text, int enabledIndex) {}

  public List<AbilityDescription> getAbilityDescriptions() {
    return List.of();
  }

  public String getDescription() {
    return "";
  }

  public Map<Item, Integer> getItemXpMap() {
    return Map.of();
  }

  public Map<Block, Integer> getBlockXpMap() {
    return Map.of();
  }

}
