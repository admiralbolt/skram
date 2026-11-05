package com.avi.skram.network;

import com.avi.skram.audio.SkramSounds;
import com.avi.skram.magic.SkramMagic;
import com.avi.skram.magic.SpellCastingInfo;
import com.avi.skram.magic.spells.Spell;
import com.avi.skram.magic.spells.Spells;
import com.avi.skram.perks.Perk;
import com.avi.skram.perks.PerkFlyingDutchman;
import com.avi.skram.perks.Perks;
import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import com.avi.skram.utils.ReflectionUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
  }

  public static void handlePerkPacket(final PerkPacket packet, final IPayloadContext context) {
    Player player = context.player();
    Perk perk = (Perk) player.getData(Perks.PERKS_BY_NAME.get(packet.name()));
    perk.discovered = packet.discovered();
    if (perk.researched && packet.enabled() != perk.enabled) {
      perk.enabled = packet.enabled();
      perk.applyModifiers(player);
    }
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
  }

  public static void handleSpellCastPacket(final SpellCastPacket packet, final IPayloadContext context) {
    Player player = context.player();
    Spell spell = Spells.SPELLS_BY_NAME.get(packet.name());
    if (spell == null) return;

    // First, we check to see if we are currently casting a spell. If so, we simply cancel the cast.
    SpellCastingInfo castingInfo = player.getData(SkramMagic.SPELL_CASTING_INFO);
    if (castingInfo.isCasting) {
      castingInfo.cancel = true;
    } else {
      if (spell.canCast(player)) {
        // If we can cast it, do so!
        castingInfo.currentMana -= spell.manaCost;
        castingInfo.isCasting = true;
        castingInfo.castingTimer = spell.castingTimeTicks;
        castingInfo.spellName = castingInfo.equippedSpellName;
        castingInfo.startCasting = true;
      } else {
        // Otherwise, mark that we failed to cast a spell.
        castingInfo.failedCasting = true;
        // Rather than using a separate var to track failed casting, we reuse the same timer.
        castingInfo.castingTimer = 20;
      }
    }

    player.setData(SkramMagic.SPELL_CASTING_INFO, castingInfo);
  }

}
