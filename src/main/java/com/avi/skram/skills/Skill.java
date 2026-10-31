package com.avi.skram.skills;

import com.avi.skram.ui.GenericMessageToast;
import com.avi.skram.ui.ToastUtils;
import com.avi.skram.ui.XPGainGui;
import com.avi.skram.utils.MathUtils;
import com.avi.skram.utils.StringUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.client.gui.widget.ResizableTextureImageWidget;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public abstract class Skill implements ValueIOSerializable {

  public static final int XP_FACTOR = 100;
  public static Identifier YELLOW_BAR = Identifier.withDefaultNamespace("textures/gui/sprites/boss_bar/yellow_progress.png");
  public static Identifier YELLOW_BAR_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/sprites/boss_bar/yellow_background.png");

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
    // Doesn't support a bool array by default, just convert to int.
    output.putIntArray("enabledAbilities", IntStream.range(0, this.enabledAbilities.length).map(i -> this.enabledAbilities[i] ? 1 : 0).toArray());
  }

  @Override
  public void deserialize(ValueInput input) {
    this.name = input.getStringOr("name", "die");
    this.level = input.getIntOr("level", -1);
    this.xp = input.getDoubleOr("xp", -2.2);
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

  // Overload that assumes server side processing. If you want to do client side processing, make sure that you specify
  // syncToServer = true.
  public void addXp(Player player, double xp) {
    addXp(player, xp, false);
  }

  public void addXp(Player player, double xp, boolean syncToServer) {
    if (xp <= 0) return;

    this.xp += (1 + 0.01 * player.experienceLevel) * xp;
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
      XPGainGui.notify(this.name, xp);
      ClientPacketDistributor.sendToServer(new SkillPacket(this.name, this.level, this.xp, this.enabledAbilities));
    }
  }

  public void onLevelChange(Player player) {
    // If we are on the client side, display the level up toast!
    if (player.level().isClientSide()) {
      Minecraft.getInstance().gui.toastManager().addToast(new GenericMessageToast(
        this.name + ":level_up",
        Skills.SKILL_ICONS.getOrDefault(this.name, Skills.DEFAULT_ICON),
        StringUtils.capitalize(this.name) + " leveled up to Lvl " + this.level + "."
      ));
      // If we are on the server side, we need to send a packet to the client to display the toast.
    }
  }

  public void applyModifiers(Player player) {
    return;
  }

  public void toggleEnabled(int i) {
    this.enabledAbilities[i] = !this.enabledAbilities[i];
    // If we call this on the client side (which we expect to), update the server side as well.
    if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.isClientSide()) {
      ClientPacketDistributor.sendToServer(new SkillPacket(this.name, this.level, this.xp, this.enabledAbilities));
    }
  }

  public <T extends PlayerInteractEvent & ICancellableEvent> void requiredLevelCheck(T event) {
    ItemStack usedItem = event.getItemStack();
    if (usedItem == ItemStack.EMPTY) return;
    if (event.getEntity().isCreative()) return;

    int requiredLevel = Skills.requiredLevel(usedItem);
    if (this.level >= requiredLevel) return;

    event.setCanceled(true);
    if (!event.getLevel().isClientSide()) return;
    ToastManager toastManager = Minecraft.getInstance().gui.toastManager();

    if (!ToastUtils.hasGenericMessageToast(toastManager, this.getRequiredLevelToastName())) {
      toastManager.addToast(new GenericMessageToast(
        this.getRequiredLevelToastName(), Skills.SKILL_ICONS.getOrDefault(this.name, Skills.DEFAULT_ICON), "Using a " + usedItem.getItemName().getString() + " requires a " + this.name + " level of " + requiredLevel + "."
      ));
    }
  }

  public Layout renderToLayout() {
    LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
    layout.spacing(2).defaultCellSetting().padding(2);

    layout.addChild(new ResizableTextureImageWidget(0, 0, 32, 32, Skills.SKILL_ICONS.get(this.name), 16, 16));

    LinearLayout vertical = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
    vertical.spacing(3).defaultCellSetting().paddingBottom(2);

    vertical.addChild(new MultiLineTextWidget(Component.literal(StringUtils.capitalize(this.name) + " Lvl " + this.level), Minecraft.getInstance().font));

    GridLayout xpBarLayout = new GridLayout();
    int xpBarWidth = (int) MathUtils.linearRescale(this.xp, Skill.getTotalXpForLevel(this.level), Skill.getTotalXpForLevel(this.level + 1), 0, 100);
    xpBarLayout.addChild(new ResizableTextureImageWidget(0, 0, 100, 3, YELLOW_BAR_BACKGROUND, 182, 5), 0, 0);
    xpBarLayout.addChild(new ResizableTextureImageWidget(0, 0, xpBarWidth, 3, YELLOW_BAR, 182, 5), 0, 0);
    vertical.addChild(xpBarLayout);

    vertical.addChild(new MultiLineTextWidget(Component.literal("EXP:  " + String.format("%,d", (int) this.xp)), Minecraft.getInstance().font));
    vertical.addChild(new MultiLineTextWidget(Component.literal("Next: " + String.format("%,d", Skill.getTotalXpForLevel(this.level + 1))), Minecraft.getInstance().font));

    layout.addChild(vertical);
    layout.addChild(this.renderSkillAbilities());

    return layout;
  }

  public abstract Layout renderSkillAbilities();

}
