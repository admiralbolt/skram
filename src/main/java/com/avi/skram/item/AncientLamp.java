package com.avi.skram.item;

import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.StringUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AncientLamp extends Item {

  public static final String NAME = "ancient_lamp";

  public AncientLamp(Properties properties) {
    super(properties);
  }

  @Override
  public @NonNull Component getName(@NonNull ItemStack itemStack) {
    SkillNameDataComponent skillName = itemStack.get(SkramItems.SKILL_NAME_DATA_COMPONENT.get());
    if (skillName == null) return Component.literal("Empty Ancient Lamp");

    return Component.literal("Ancient Lamp of " + StringUtils.snakeToTitle(skillName.skillName()));
  }

  @Override
  public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
    SkillNameDataComponent skillNameData = itemStack.get(SkramItems.SKILL_NAME_DATA_COMPONENT.get());
    String skillName = skillNameData.skillName();
    builder.accept(Component.literal(String.format("Using this lamp gives 100 * level %s xp.", skillName)));
    super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
  }

  @Override
  public boolean isFoil(@NonNull ItemStack itemStack) {
    return true;
  }

  @Override
  public @NonNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (level.isClientSide()) return InteractionResult.PASS;

    SkillNameDataComponent skillNameData = stack.get(SkramItems.SKILL_NAME_DATA_COMPONENT.get());
    String skillName = skillNameData.skillName();
    if (skillNameData == null) return InteractionResult.PASS;
    if (!Skills.SKILLS_BY_NAME.containsKey(skillName)) return InteractionResult.PASS;

    Supplier skillSupplier = Skills.SKILLS_BY_NAME.get(skillName);
    Skill skill = (Skill) player.getData(skillSupplier);
    skill.addXp(player, 100 * skill.level);

    stack.hurtAndBreak(1, player, hand);
    return InteractionResult.SUCCESS_SERVER;
  }
}
