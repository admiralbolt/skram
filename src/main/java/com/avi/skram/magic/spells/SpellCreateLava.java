package com.avi.skram.magic.spells;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.network.SpellCastPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class SpellCreateLava extends Spell {

  public static String NAME = "create_lava";
  public static int REQUIRED_MAGIC_LEVEL = 22;
  public static int COST = 1800;
  public static int CASTING_TICKS = 120;

  public SpellCreateLava() {
    super(NAME, REQUIRED_MAGIC_LEVEL, COST, CASTING_TICKS, SkramSounds.SPELL_HEAL);
  }

  @Override
  public void sendCastPacket(Player player) {
    BlockHitResult hitResult = Item.getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.NONE);

    Vec3 targetBlock = Vec3.ZERO;
    Direction direction = Direction.UP;
    if (hitResult.getType() != HitResult.Type.MISS) {
      targetBlock = hitResult.getLocation();
      direction = hitResult.getDirection();
    }

    ClientPacketDistributor.sendToServer(new SpellCastPacket(this.name, targetBlock, direction));
  }

  @Override
  public void doEffect(Player player, Vec3 targetBlock, Direction direction) {
    super.doEffect(player, targetBlock, direction);

    if (targetBlock.equals(Vec3.ZERO)) return;

    // Create a fake bucket item!
    BucketItem LavaBucket = (BucketItem) Items.LAVA_BUCKET;
    ItemStack itemStack = new ItemStack(LavaBucket, 1);
    BlockPos pos = new BlockPos((int) targetBlock.x, (int) targetBlock.y, (int) targetBlock.z);
    BlockPos directionOffsetPos = pos.relative(direction);

    if (!player.level().mayInteract(player, pos) || !player.mayUseItemAt(directionOffsetPos, direction, itemStack))
      return;

    BlockState clicked = player.level().getBlockState(pos);
    if (clicked.getBlock() instanceof AbstractCauldronBlock) {
      player.level().setBlockAndUpdate(pos, Blocks.LAVA_CAULDRON.defaultBlockState());
      return;
    }

    LavaBucket.emptyContents(player, player.level(), pos, null, itemStack);
  }

  @Override
  public String getDescription(Player player) {
    return "Create a source Lava block at the target location. Block must be highlighted for spell to take effect.";
  }
}
