package com.avi.skram.perks;

import com.avi.skram.Skram;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Perks {

  public static Map<String, Supplier> PERKS_BY_NAME;

  public static Supplier<AttachmentType<PerkHeartOfTheForge>> HEART_OF_THE_FORGE;

  public static Map<String, Supplier<List<ItemStack>>> PERK_RESEARCH_COST_BY_NAME;

  public static void register() {
    PERKS_BY_NAME = new HashMap<>();

    HEART_OF_THE_FORGE = Skram.ATTACHMENT_TYPES.register(
      PerkHeartOfTheForge.NAME, () -> AttachmentType.serializable(() -> new PerkHeartOfTheForge())
        .sync(new PerkSyncHandler<>(PerkHeartOfTheForge.STREAM_CODEC))
        .copyOnDeath()
        .build()
    );
    PERKS_BY_NAME.put(PerkHeartOfTheForge.NAME, HEART_OF_THE_FORGE);
  }

  public static void setupPerkResearchCosts() {
    PERK_RESEARCH_COST_BY_NAME = new HashMap<>();

    PERK_RESEARCH_COST_BY_NAME.put(PerkHeartOfTheForge.NAME, PerkHeartOfTheForge::getResearchCost);
  }
}
