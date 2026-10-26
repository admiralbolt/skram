package com.avi.skram.skills;

import com.avi.skram.Skram;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Skills {

  // Lord forgive me, for I have sinned.
  // Most of the pain here was just the fact that I can use a generic type like
  //   Supplier<AttachmentType<? extends Skill>>
  // As a result of using the non-typed Supplier, there WILL be unchecked casting.
  public static Map<String, Supplier> SKILLS_BY_NAME;

  public static Supplier<AttachmentType<SkillAcrobatics>> ACROBATICS;
  public static Supplier<AttachmentType<SkillAthletics>> ATHLETICS;
  public static Supplier<AttachmentType<SkillDigging>> DIGGING;
  public static Supplier<AttachmentType<SkillMining>> MINING;

  public static void register() {
    SKILLS_BY_NAME = new HashMap<>();

    ACROBATICS = Skram.ATTACHMENT_TYPES.register(
      SkillAcrobatics.NAME, () -> AttachmentType.serializable(() -> new SkillAcrobatics())
        .sync((holder, to) -> holder == to, SkillAcrobatics.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillAcrobatics.NAME, ACROBATICS);

    ATHLETICS = Skram.ATTACHMENT_TYPES.register(
      SkillAthletics.NAME, () -> AttachmentType.serializable(() -> new SkillAthletics())
        .sync((holder, to) -> holder == to, SkillAthletics.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillAthletics.NAME, ATHLETICS);

    DIGGING = Skram.ATTACHMENT_TYPES.register(
      SkillDigging.NAME, () -> AttachmentType.serializable(() -> new SkillDigging())
        .sync((holder, to) -> holder == to, SkillDigging.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillDigging.NAME, DIGGING);

    MINING = Skram.ATTACHMENT_TYPES.register(
      SkillMining.NAME, () -> AttachmentType.serializable(() -> new SkillMining())
        .sync((holder, to) -> holder == to, SkillMining.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillMining.NAME, MINING);
  }

  public static int requiredLevel(ItemStack stack) {
    // This is a pretty unfortunate way of checking, but the materials themselves aren't actually stored on the items
    // in any way, they are simply used to set attribute values on construction.
    // We instead check the names for the appropriate material.

    String itemID = stack.getItem().toString();

    if (itemID.contains("netherite")) return 40;
    if (itemID.contains("diamond")) return 30;
    if (itemID.contains("gold")) return 25;
    if (itemID.contains("iron")) return 20;
    if (itemID.contains("copper")) return 10;
    if (itemID.contains("stone")) return 5;

    return 1;
  }

}
