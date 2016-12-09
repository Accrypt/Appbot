package io.mzb.Appbot.commands;

import io.mzb.Appbot.plugin.AppbotPlugin;

import java.util.HashMap;

public class CommandManager {

    private HashMap<String, CommandHandler> commands;

    public CommandManager() {
        commands = new HashMap<>();
    }

    public void registerCommand(String command, CommandHandler handler) {
        if (commands.containsKey(command)) {
            System.out.println("[Commands] Error: Command \"" + command.toLowerCase() + "\" is already assigned!");
            command = command + "_";
            System.out.println("[Commands] Remapping command to: " + command);
            registerCommand(command, handler);
        } else {
            commands.put(command, handler);
            System.out.println("[Commands] Command registered: " + command + " (" + handler.getClass().getSimpleName() + ")");
        }
    }

    public CommandHandler getCommandHandler(String command) {
        return commands.get(command);
    }

}
