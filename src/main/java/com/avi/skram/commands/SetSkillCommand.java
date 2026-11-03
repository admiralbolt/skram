package com.avi.skram.commands;

import com.avi.skram.Skram;
import com.avi.skram.skills.Skill;
import com.avi.skram.skills.Skills;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class SetSkillCommand {

  private static final SimpleCommandExceptionType SKILL_DOES_NOT_EXIST =
    new SimpleCommandExceptionType(Component.literal("Specified skill does not exist."));

  private static final SimpleCommandExceptionType LEVEL_MUST_BE_POSITIVE =
    new SimpleCommandExceptionType(Component.literal("Level must be positive."));

  public static void register(RegisterCommandsEvent event) {
    event.getDispatcher().register(
      Commands.literal(Skram.MODID)
        .then(Commands.literal("setskill")
          .then(Commands.argument("entity", EntityArgument.player())
            .then(Commands.argument("skill", StringArgumentType.word()).suggests(skillSuggestions())
              .then(Commands.literal("xpmult")
                .then(Commands.argument("xpmult", DoubleArgumentType.doubleArg())
                  .executes(context -> {
                    Player player = EntityArgument.getPlayer(context, "entity");
                    String skillName = StringArgumentType.getString(context, "skill");
                    double xpMult = DoubleArgumentType.getDouble(context, "xpmult");
                    if (!Skills.SKILLS_BY_NAME.containsKey(skillName)) throw SKILL_DOES_NOT_EXIST.create();

                    Supplier supplier = Skills.SKILLS_BY_NAME.get(skillName);
                    Skill skill = (Skill) player.getData(supplier);
                    skill.xpMultiplier = skill.xpMultiplier + xpMult;
                    player.setData(supplier, skill);

                    return Command.SINGLE_SUCCESS;
                  })
                )
              )
              .then(Commands.literal("level")
                .then(Commands.argument("level", IntegerArgumentType.integer(1))
                  .executes(context -> {
                    Player player = EntityArgument.getPlayer(context, "entity");
                    String skillName = StringArgumentType.getString(context, "skill");
                    int level = IntegerArgumentType.getInteger(context, "level");

                    // You type name bad. Do better.
                    if (!Skills.SKILLS_BY_NAME.containsKey(skillName)) throw SKILL_DOES_NOT_EXIST.create();
                    // Don't try to set levels below 1.
                    if (level <= 0) throw LEVEL_MUST_BE_POSITIVE.create();

                    Supplier supplier = Skills.SKILLS_BY_NAME.get(skillName);
                    Skill skill = (Skill) player.getData(supplier);
                    skill.level = level;
                    player.setData(supplier, skill);

                    return Command.SINGLE_SUCCESS;
                  })
                )
              )
            )
          )
        ).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
    );
  }

  private static SuggestionProvider<CommandSourceStack> skillSuggestions() {
    return (context, builder) -> SharedSuggestionProvider.suggest(Skills.SKILLS_BY_NAME.keySet().stream().sorted().toList(), builder);
  }
}
