package com.avi.skram.skills;

import com.avi.skram.Skram;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
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

  // Map skills to the item used for their icon.
  public static Map<String, ItemStackTemplate> SKILL_ICONS;

  public static void register() {
    SKILLS_BY_NAME = new HashMap<>();

    ACROBATICS = Skram.ATTACHMENT_TYPES.register(
      SkillAcrobatics.NAME, () -> AttachmentType.serializable(() -> new SkillAcrobatics())
        .sync(new SkillSyncHandler<>(SkillAcrobatics.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillAcrobatics.NAME, ACROBATICS);

    ATHLETICS = Skram.ATTACHMENT_TYPES.register(
      SkillAthletics.NAME, () -> AttachmentType.serializable(() -> new SkillAthletics())
        .sync(new SkillSyncHandler<>(SkillAthletics.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillAthletics.NAME, ATHLETICS);

    DIGGING = Skram.ATTACHMENT_TYPES.register(
      SkillDigging.NAME, () -> AttachmentType.serializable(() -> new SkillDigging())
        .sync(new SkillSyncHandler<>(SkillDigging.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillDigging.NAME, DIGGING);

    MINING = Skram.ATTACHMENT_TYPES.register(
      SkillMining.NAME, () -> AttachmentType.serializable(() -> new SkillMining())
        .sync(new SkillSyncHandler<>(SkillMining.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillMining.NAME, MINING);
  }

  // This needs to be done in a separate handler to ensure that items have been registered.
  public static void setupSkillIcons() {
    SKILL_ICONS = new HashMap<>();

    SKILL_ICONS.put(SkillDigging.NAME, new ItemStackTemplate(Items.DIAMOND_SHOVEL));
    SKILL_ICONS.put(SkillMining.NAME, new ItemStackTemplate(Items.DIAMOND_PICKAXE));
  }

  public static ItemStack getItemIcon(String skillName) {
    ItemStackTemplate template = SKILL_ICONS.get(skillName);

    if (template == null) return new ItemStack(Items.COW_SPAWN_EGG);

    return template.create();
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
