package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.network.SpellCastPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Optional;

public class SpellFertileSoil extends Spell {

  public static String NAME = "fertile_soil";
  public static int REQUIRED_MAGIC_LEVEL = 11;
  public static int COST = 400;
  public static int CASTING_TICKS = 25;

  public SpellFertileSoil() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_CAST_GENERIC4);
  }

  @Override
  public void sendCastPacket(Player player) {
    BlockHitResult hitResult = Item.getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.NONE);

    ClientPacketDistributor.sendToServer(new SpellCastPacket(this.name, Optional.of(hitResult)));
  }

  @Override
  public void doEffect(Player player, BlockHitResult hitResult) {
    super.doEffect(player, hitResult);

    if (hitResult == null) return;

    BoneMealItem item = (BoneMealItem) Items.BONE_MEAL;
    ItemStack stack = new ItemStack(item, 1);

    UseOnContext context = new UseOnContext(player.level(), player, player.getUsedItemHand(), stack, hitResult);
    item.useOn(context);
  }

  @Override
  public String getDescription(Player player) {
    return "Applies bonemeal to the target location.";
  }
}
