package com.avi.skram.commands;

import com.avi.skram.Skram;
import com.avi.skram.perks.Perk;
import com.avi.skram.perks.Perks;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.function.Supplier;

public class SetPerkCommand {

  private static final SimpleCommandExceptionType PERK_DOES_NOT_EXIST =
    new SimpleCommandExceptionType(Component.literal("Specified perk does not exist."));

  public static void register(RegisterCommandsEvent event) {
    event.getDispatcher().register(
      Commands.literal(Skram.MODID)
        .then(Commands.literal("setperk")
          .then(Commands.argument("entity", EntityArgument.player())
            .then(Commands.argument("perk", StringArgumentType.word()).suggests(perkSuggestions())
              .then(Commands.literal("discovered")
                .then(Commands.argument("discoveredValue", BoolArgumentType.bool())
                  .executes(context -> {
                    Player player = EntityArgument.getPlayer(context, "entity");
                    String perkName = StringArgumentType.getString(context, "perk");
                    boolean discovered = BoolArgumentType.getBool(context, "discoveredValue");
                    if (!Perks.PERKS_BY_NAME.containsKey(perkName)) throw PERK_DOES_NOT_EXIST.create();

                    Supplier supplier = Perks.PERKS_BY_NAME.get(perkName);
                    Perk perk = (Perk) player.getData(supplier);
                    perk.discovered = discovered;
                    player.setData(supplier, perk);

                    return Command.SINGLE_SUCCESS;
                  })
                )
              )
              .then(Commands.literal("researched")
                .then(Commands.argument("researchedValue", BoolArgumentType.bool())
                  .executes(context -> {
                    Player player = EntityArgument.getPlayer(context, "entity");
                    String perkName = StringArgumentType.getString(context, "perk");
                    boolean researched = BoolArgumentType.getBool(context, "researchedValue");
                    if (!Perks.PERKS_BY_NAME.containsKey(perkName)) throw PERK_DOES_NOT_EXIST.create();

                    Supplier supplier = Perks.PERKS_BY_NAME.get(perkName);
                    Perk perk = (Perk) player.getData(supplier);
                    perk.researched = researched;
                    player.setData(supplier, perk);

                    return Command.SINGLE_SUCCESS;
                  })
                )
              )
            )
          )
        ).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
    );
  }

  private static SuggestionProvider<CommandSourceStack> perkSuggestions() {
    return (context, builder) -> SharedSuggestionProvider.suggest(Perks.PERKS_BY_NAME.keySet().stream().sorted().toList(), builder);
  }
}
