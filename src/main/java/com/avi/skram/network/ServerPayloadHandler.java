package com.avi.skram.network;

import com.avi.skram.magic.SkramMagic;
import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.Spellbook;
import com.avi.skram.magic.spells.Spell;
import com.avi.skram.magic.spells.Spells;
import com.avi.skram.perks.Perk;
import com.avi.skram.perks.PerkFlyingDutchman;
import com.avi.skram.perks.Perks;
import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.ReflectionUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ServerPayloadHandler {

  public static void handleSkillPacket(final SkillPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      Player player = context.player();
      Skill skill = (Skill) player.getData(Skills.SKILLS_BY_NAME.get(packet.name()));
      // Modify the skill values. We don't actually need to call "setData" here. It's assumed this method is invoked
      // because we need to sync client -> server, in which case the client already has the correct value.
      if (packet.level() != skill.level) {
        skill.level = packet.level();
        skill.onLevelChange(player);
      }
      skill.xp = packet.xp();
      skill.xpMultiplier = packet.xpMultiplier();
      if (!Arrays.equals(skill.enabledAbilities, packet.enabledAbilities())) {
        skill.enabledAbilities = packet.enabledAbilities();
        skill.applyModifiers(player);
      }
    });
  }

  public static void handlePerkPacket(final PerkPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      Player player = context.player();
      Perk perk = (Perk) player.getData(Perks.PERKS_BY_NAME.get(packet.name()));
      perk.discovered = packet.discovered();
      if (perk.researched && packet.enabled() != perk.enabled) {
        perk.enabled = packet.enabled();
        perk.applyModifiers(player);
      }
    });
  }

  public static void handlePerkResearchPacket(final PerkResearchPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      var perkAttachment = Perks.PERKS_BY_NAME.get(packet.name());
      if (perkAttachment == null) return;

      Player player = context.player();
      Perk perk = (Perk) player.getData(perkAttachment);
      if (perk.research(player)) {
        context.reply(packet);
      }
    });
  }

  public static void handleBoatJumpPacket(final BoatJumpPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      Player player = context.player();
      Entity entity = player.getControlledVehicle();
      PerkFlyingDutchman perk = player.getData(Perks.FLYING_DUTCHMAN);

      if (!perk.isFullyEnabled()) return;
      if (!(entity instanceof AbstractBoat boat)) return;

      AbstractBoat.Status status = null;
      // Reflection strikes again.
      try {
        status = (AbstractBoat.Status) ReflectionUtils.invokeMethod(boat, "getStatus");
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
        System.out.println("no status for you.");
      }

      if (status == null || status == AbstractBoat.Status.IN_AIR) return;

      Vec3 baseDelta = boat.getKnownMovement();
      boat.setDeltaMovement(baseDelta.x, player.getAttributeValue(Attributes.JUMP_STRENGTH) * 1.5, baseDelta.z);
    });
  }

  public static void handleSpellCastPacket(final SpellCastPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      Player player = context.player();
      Spell spell = Spells.SPELLS_BY_NAME.get(packet.name());

      if (spell == null) return;

      spell.doEffect(player, packet.hitResult().isPresent() ? packet.hitResult().get() : null);
    });
  }

  public static void handleSpellEquipPacket(final SpellEquipPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      Player player = context.player();
      if (!Spells.SPELLS_BY_NAME.containsKey(packet.name())) return;

      Spellbook spellbook = player.getData(SkramMagic.SPELLBOOK);
      if (!spellbook.knownSpells.contains(packet.name())) return;

      SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
      castingInfo.equippedSpellName = packet.name();
      player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
    });
  }

  // We receive this from the client, and should update the values of the server side data attachment accordingly.
  public static void handleCastingSyncPacket(final CastingSyncServerPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      Player player = context.player();
      // If we use player.setData() it will trigger a sync back to the client, which we don't want.
      SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
      castingInfo.currentMana = packet.castingInfo().currentMana;
      castingInfo.isCasting = packet.castingInfo().isCasting;
      castingInfo.castingTimer = packet.castingInfo().castingTimer;
      castingInfo.spellName = packet.castingInfo().spellName;
      castingInfo.equippedSpellName = packet.castingInfo().equippedSpellName;
      castingInfo.failedCasting = packet.castingInfo().failedCasting;
      castingInfo.startCasting = packet.castingInfo().startCasting;
    });
  }

}
