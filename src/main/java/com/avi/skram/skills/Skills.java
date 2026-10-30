package com.avi.skram.skills;

import com.avi.skram.Skram;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Skills {

  // Lord forgive me, for I have sinned.
  // Most of the pain here was just the fact that I can't use a generic type like:
  //   Supplier<AttachmentType<? extends Skill>>
  // As a result of using the non-typed Supplier, there WILL be unchecked casting.
  public static Map<String, Supplier> SKILLS_BY_NAME;

  public static Supplier<AttachmentType<SkillAcrobatics>> ACROBATICS;
  public static Supplier<AttachmentType<SkillAthletics>> ATHLETICS;
  public static Supplier<AttachmentType<SkillBotany>> BOTANY;
  public static Supplier<AttachmentType<SkillBrewing>> BREWING;
  public static Supplier<AttachmentType<SkillCooking>> COOKING;
  public static Supplier<AttachmentType<SkillDigging>> DIGGING;
  public static Supplier<AttachmentType<SkillFarming>> FARMING;
  public static Supplier<AttachmentType<SkillFishing>> FISHING;
  public static Supplier<AttachmentType<SkillMelee>> MELEE;
  public static Supplier<AttachmentType<SkillMining>> MINING;
  public static Supplier<AttachmentType<SkillRanged>> RANGED;
  public static Supplier<AttachmentType<SkillSmithing>> SMITHING;
  public static Supplier<AttachmentType<SkillWoodcutting>> WOODCUTTING;

  // Map skills to the item used for their icon.
  public static Map<String, Identifier> SKILL_ICONS;
  public static Identifier DEFAULT_ICON = Identifier.withDefaultNamespace("textures/item/cow_spawn_egg.png");

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

    BOTANY = Skram.ATTACHMENT_TYPES.register(
      SkillBotany.NAME, () -> AttachmentType.serializable(() -> new SkillBotany())
        .sync(new SkillSyncHandler<>(SkillBotany.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillBotany.NAME, BOTANY);

    BREWING = Skram.ATTACHMENT_TYPES.register(
      SkillBrewing.NAME, () -> AttachmentType.serializable(() -> new SkillBrewing())
        .sync(new SkillSyncHandler<>(SkillBrewing.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillBrewing.NAME, BREWING);

    COOKING = Skram.ATTACHMENT_TYPES.register(
      SkillCooking.NAME, () -> AttachmentType.serializable(() -> new SkillCooking())
        .sync(new SkillSyncHandler<>(SkillCooking.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillCooking.NAME, COOKING);

    DIGGING = Skram.ATTACHMENT_TYPES.register(
      SkillDigging.NAME, () -> AttachmentType.serializable(() -> new SkillDigging())
        .sync(new SkillSyncHandler<>(SkillDigging.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillDigging.NAME, DIGGING);

    FARMING = Skram.ATTACHMENT_TYPES.register(
      SkillFarming.NAME, () -> AttachmentType.serializable(() -> new SkillFarming())
        .sync(new SkillSyncHandler<>(SkillFarming.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillFarming.NAME, FARMING);

    FISHING = Skram.ATTACHMENT_TYPES.register(
      SkillFishing.NAME, () -> AttachmentType.serializable(() -> new SkillFishing())
        .sync(new SkillSyncHandler<>(SkillFishing.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillFishing.NAME, FISHING);

    MELEE = Skram.ATTACHMENT_TYPES.register(
      SkillMelee.NAME, () -> AttachmentType.serializable(() -> new SkillMelee())
        .sync(new SkillSyncHandler<>(SkillMelee.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillMelee.NAME, MELEE);

    MINING = Skram.ATTACHMENT_TYPES.register(
      SkillMining.NAME, () -> AttachmentType.serializable(() -> new SkillMining())
        .sync(new SkillSyncHandler<>(SkillMining.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillMining.NAME, MINING);

    RANGED = Skram.ATTACHMENT_TYPES.register(
      SkillRanged.NAME, () -> AttachmentType.serializable(() -> new SkillRanged())
        .sync(new SkillSyncHandler<>(SkillRanged.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillRanged.NAME, RANGED);

    SMITHING = Skram.ATTACHMENT_TYPES.register(
      SkillSmithing.NAME, () -> AttachmentType.serializable(() -> new SkillSmithing())
        .sync(new SkillSyncHandler<>(SkillSmithing.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillSmithing.NAME, SMITHING);

    WOODCUTTING = Skram.ATTACHMENT_TYPES.register(
      SkillWoodcutting.NAME, () -> AttachmentType.serializable(() -> new SkillWoodcutting())
        .sync(new SkillSyncHandler<>(SkillWoodcutting.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillWoodcutting.NAME, WOODCUTTING);
  }

  // This needs to be done in a separate handler to ensure that items have been registered.
  public static void setupSkillIcons() {
    SKILL_ICONS = new HashMap<>();

    SKILL_ICONS.put(SkillAcrobatics.NAME, Identifier.withDefaultNamespace("textures/item/netherite_boots.png"));
    SKILL_ICONS.put(SkillAthletics.NAME, Identifier.withDefaultNamespace("textures/item/rabbit_foot.png"));
    SKILL_ICONS.put(SkillBotany.NAME, Identifier.withDefaultNamespace("textures/item/wildflowers.png"));
    SKILL_ICONS.put(SkillBrewing.NAME, Identifier.withDefaultNamespace("textures/item/brewing_stand.png"));
    SKILL_ICONS.put(SkillCooking.NAME, Identifier.withDefaultNamespace("textures/item/cooked_beef.png"));
    SKILL_ICONS.put(SkillDigging.NAME, Identifier.withDefaultNamespace("textures/item/stone_shovel.png"));
    SKILL_ICONS.put(SkillFarming.NAME, Identifier.withDefaultNamespace("textures/item/iron_hoe.png"));
    SKILL_ICONS.put(SkillFishing.NAME, Identifier.withDefaultNamespace("textures/item/fishing_rod.png"));
    SKILL_ICONS.put(SkillMelee.NAME, Identifier.withDefaultNamespace("textures/item/iron_sword.png"));
    SKILL_ICONS.put(SkillMining.NAME, Identifier.withDefaultNamespace("textures/item/stone_pickaxe.png"));
    SKILL_ICONS.put(SkillRanged.NAME, Identifier.withDefaultNamespace("textures/item/bow.png"));
    SKILL_ICONS.put(SkillSmithing.NAME, Identifier.withDefaultNamespace("textures/item/campfire.png"));
    SKILL_ICONS.put(SkillWoodcutting.NAME, Identifier.withDefaultNamespace("textures/item/stone_axe.png"));
  }

  // Same here, done in a separate handler to ensure that items have been registered.
  public static void setupSkillXPMaps() {
    SkillBrewing.registerXPMap();
    SkillCooking.setupXPMap();
    SkillDigging.setupXPMap();
    SkillFarming.setupXPMap();
    SkillMining.setupXPMap();
    SkillSmithing.setupXPMap();
    SkillWoodcutting.setupXPMap();
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
