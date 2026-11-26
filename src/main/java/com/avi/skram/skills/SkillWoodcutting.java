package com.avi.skram.skills;

import com.avi.skram.client.SkramClientSkillHooks;
import com.avi.skram.utils.MathUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillWoodcutting extends Skill {

  public static final String NAME = "woodcutting";
  public static final StreamCodec<ByteBuf, SkillWoodcutting> STREAM_CODEC = StreamCodec.ofMember(Skill::encode, SkillWoodcutting::new);
  public static final String DESCRIPTION = """
    Chop wood.
    
    XP is also gained for a wide variety of axe right-clicking things. Also any leaf block OR block mineable with an axe is worth 10xp. Otherwise, XP:
    """;
  public static Map<Block, Integer> XP_MAP;

  public static final int WOODCUTTING_SPEED_INDEX = 0;
  public static final int WOODCUTTING_FORTUNE_INDEX = 1;

  public SkillWoodcutting() {
    super(NAME, 1, 0);
    this.enabledAbilities = new boolean[]{true, true};
  }

  public SkillWoodcutting(ByteBuf buffer) {
    super(buffer);
    this.ensureFortuneAbility();
  }

  private void ensureFortuneAbility() {
    if (this.enabledAbilities.length <= WOODCUTTING_FORTUNE_INDEX) {
      this.enabledAbilities = java.util.Arrays.copyOf(this.enabledAbilities, WOODCUTTING_FORTUNE_INDEX + 1);
      this.enabledAbilities[WOODCUTTING_FORTUNE_INDEX] = true;
    }
  }

  @Override
  public void deserialize(net.minecraft.world.level.storage.ValueInput input) {
    super.deserialize(input);
    this.ensureFortuneAbility();
  }

  public static void setupXPMap() {
    XP_MAP = new HashMap<>();

    XP_MAP.put(Blocks.ACACIA_LOG, 300);
    XP_MAP.put(Blocks.BIRCH_LOG, 300);
    XP_MAP.put(Blocks.CHERRY_LOG, 320);
    XP_MAP.put(Blocks.CRIMSON_STEM, 440);
    XP_MAP.put(Blocks.DARK_OAK_LOG, 300);
    XP_MAP.put(Blocks.JUNGLE_LOG, 300);
    XP_MAP.put(Blocks.MANGROVE_LOG, 300);
    XP_MAP.put(Blocks.OAK_LOG, 200);
    XP_MAP.put(Blocks.PALE_OAK_LOG, 360);
    XP_MAP.put(Blocks.POPLAR_LOG, 300);
    XP_MAP.put(Blocks.SPRUCE_LOG, 300);
    XP_MAP.put(Blocks.WARPED_STEM, 440);
  }

  @Override
  public void addXp(Player player, double xp) {
    super.addXp(player, xp);
    player.setData(Skills.WOODCUTTING, player.getData(Skills.WOODCUTTING));
  }

  public float getSpeedBonus() {
    return 0.03f * this.level;
  }

  public double getFortuneChance() {
    return 0.02 * this.level;
  }

  public static void applySpeed(PlayerEvent.BreakSpeed event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_AXE)) return;

    SkillWoodcutting woodcutting = event.getEntity().getData(Skills.WOODCUTTING);
    if (!woodcutting.enabledAbilities[WOODCUTTING_SPEED_INDEX]) return;

    event.setNewSpeed(event.getOriginalSpeed() * (1 + woodcutting.getSpeedBonus()));
  }

  public static void gainXp(BreakBlockEvent event) {
    if (!event.getState().is(BlockTags.MINEABLE_WITH_AXE) && !event.getState().is(BlockTags.LEAVES)) return;

    SkillWoodcutting woodcutting = event.getPlayer().getData(Skills.WOODCUTTING);
    woodcutting.addXp(event.getPlayer(), XP_MAP.getOrDefault(event.getState().getBlock(), 20));
  }

  public static void applyFortune(BlockDropsEvent event) {
    if (!XP_MAP.containsKey(event.getState().getBlock())) return;
    if (!(event.getBreaker() instanceof Player player)) return;

    SkillWoodcutting woodcutting = player.getData(Skills.WOODCUTTING);
    if (!woodcutting.enabledAbilities[WOODCUTTING_FORTUNE_INDEX]) return;

    int fortuneAmount = MathUtils.getScalingRandom(woodcutting.getFortuneChance());
    if (fortuneAmount <= 1) return;

    List<ItemEntity> drops = event.getDrops();
    int dropSize = drops.size();
    for (int i = 0; i < dropSize; ++i) {
      ItemEntity drop = drops.get(i);
      for (int j = 0; j < fortuneAmount - 1; ++j) {
        drops.add(new ItemEntity(drop.level(), drop.getX(), drop.getY(), drop.getZ(), drop.getItem().copy()));
      }
    }
  }

  public static void handleLeftClick(PlayerInteractEvent.LeftClickBlock event) {
    ItemStack usedItem = event.getItemStack();
    if (usedItem == ItemStack.EMPTY) return;
    if (!usedItem.is(ItemTags.AXES)) return;
    if (event.getEntity().isCreative()) return;

    SkillWoodcutting woodcutting = event.getEntity().getData(Skills.WOODCUTTING);
    int requiredLevel = Skills.requiredLevel(usedItem);
    if (woodcutting.level < requiredLevel) {
      event.setCanceled(true);
      if (event.getLevel().isClientSide()) {
        SkramClientSkillHooks.showRequiredLevelToast(woodcutting, "Using a " + usedItem.getItemName().getString() + " requires a " + SkillWoodcutting.NAME + " level of " + requiredLevel + ".");
      }
    }
  }

  public static void handleRightClick(PlayerInteractEvent.RightClickBlock event) {
    ItemStack usedItem = event.getItemStack();
    if (!usedItem.is(ItemTags.AXES)) return;

    SkillWoodcutting woodcutting = event.getEntity().getData(Skills.WOODCUTTING);
    woodcutting.requiredLevelCheck(event);

    // Otherwise, if we successfully scraped, waxed, or stripped, gain some xp.
    if (!event.isCanceled() && event.getLevel().isClientSide()) return;

    BlockPos pos = event.getHitVec().getBlockPos();
    BlockState state = event.getLevel().getBlockState(pos);

    // Removed weathering...
    if (state.getBlock() instanceof WeatheringCopper weatheringCopper) {
      WeatheringCopper.WeatherState weatherState = weatheringCopper.getAge();
      if (weatherState != WeatheringCopper.WeatherState.UNAFFECTED) {
        woodcutting.addXp(event.getEntity(), 20);
        return;
      }
    }

    // Stripped some logs...
    if (state.is(BlockTags.LOGS)) {
      woodcutting.addXp(event.getEntity(), 20);
      return;
    }

    // Wax OFF.
    if (state.getBlock().getName().toString().contains("waxed")) {
      woodcutting.addXp(event.getEntity(), 20);
      return;
    }
  }

  @Override
  public java.util.List<AbilityDescription> getAbilityDescriptions() {
    return java.util.List.of(
      new AbilityDescription("Woodcutting Speed: +" + (int) (100 * this.getSpeedBonus()) + "%.", WOODCUTTING_SPEED_INDEX),
      new AbilityDescription(MathUtils.formatFortune(this.getFortuneChance()), WOODCUTTING_FORTUNE_INDEX)
    );
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }

  @Override
  public Map<Block, Integer> getBlockXpMap() {
    return XP_MAP;
  }

}