package io.mzb.Appbot.commands;

import io.mzb.Appbot.Appbot;
import io.mzb.Appbot.plugin.AppbotPlugin;

import java.util.HashMap;

public class CommandManager {

    // Holds all command handlers via their name
    private HashMap<String, CommandHandler> commands;

    /**
     * Command manager init
     */
    public CommandManager() {
        commands = new HashMap<>();
    }

    /**
     * Registers a command
     * If command name already exists the command will be renamed in the format:
     * plugin_name:command
     * @param command The name of the command to register
     * @param handler The command handler that will handle that command
     */
    private void registerCommand(AppbotPlugin plugin, String command, CommandHandler handler) {
        if(plugin == null) {
            System.out.println("[Command] Error: Plugin can not be null!");
        }
        // Does the command exist?
        if (commands.containsKey(command)) {
            // Change the name and try again
            System.out.println("[Commands] Error: Command \"" + command.toLowerCase() + "\" is already assigned!");
            command = Appbot.getPluginManager().getPluginName(plugin) + ":" + command;
            System.out.println("[Commands] Remapping command to: " + command);
            registerCommand(plugin, command, handler);
        } else {
            // Add the command
            commands.put(command, handler);
            System.out.println("[Commands] Command registered: " + command + " (" + handler.getClass().getSimpleName() + ")");
        }
    }

    /**
     * Get the command handler for the command
     * @param command The name of the command
     * @return The command handler that belong to the command (May be null)
     */
    public CommandHandler getCommandHandler(String command) {
        return commands.get(command);
    }

}
