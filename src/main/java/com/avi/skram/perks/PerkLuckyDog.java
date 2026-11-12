package com.avi.skram.perks;

import com.avi.skram.Skram;
import com.avi.skram.skills.*;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class PerkLuckyDog extends Perk {

  public static final String NAME = "lucky_dog";
  public static final String DESCRIPTION = "Gain +1 luck.";
  public static final Map<String, Integer> REQUIRED_SKILLS = ImmutableMap.<String, Integer>builder()
    .put(SkillBotany.NAME, 20)
    .put(SkillFishing.NAME, 20)
    .put(SkillFarming.NAME, 20)
    .put(SkillHusbandry.NAME, 20)
    .put(SkillMining.NAME, 20)
    .build();
  public static StreamCodec<ByteBuf, PerkLuckyDog> STREAM_CODEC = StreamCodec.ofMember(Perk::encode, PerkLuckyDog::new);

  public static final Identifier PERK_LUCKY_DOG_LUCK = Identifier.fromNamespaceAndPath(Skram.MODID, "perk_lucky_dog_luck");

  public static List<ItemStack> getResearchCost() {
    return List.of(
      new ItemStack(Items.RABBIT_FOOT, 16)
    );
  }

  @Override
  public void applyModifiers(Player player) {
    AttributeMap attributes = player.getAttributes();

    AttributeInstance luck = attributes.getInstance(Attributes.LUCK);
    if (luck == null) return;
    if (this.isFullyEnabled()) {
      luck.addOrReplacePermanentModifier(new AttributeModifier(
        PERK_LUCKY_DOG_LUCK, 1, AttributeModifier.Operation.ADD_VALUE
      ));
    } else {
      luck.removeModifier(PERK_LUCKY_DOG_LUCK);
    }
  }

  public PerkLuckyDog() {
    super(NAME, DESCRIPTION, REQUIRED_SKILLS);
  }

  public PerkLuckyDog(ByteBuf buffer) {
    super(buffer, DESCRIPTION, REQUIRED_SKILLS);
  }

}
