package com.avi.skram.commands;

import net.neoforged.neoforge.event.RegisterCommandsEvent;

// Doing this all in a separate class isn't really necessary, but I'm breaking it up into multiple files for clarity.
public class SkramCommands {

  public static void register(RegisterCommandsEvent event) {
    SetSkillCommand.register(event);
  }

}
