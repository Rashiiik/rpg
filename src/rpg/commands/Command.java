package rpg.commands;

import rpg.core.Game;

public interface Command {
    /**
     * Execute the command
     * @param game The game instance
     * @param args The command arguments (excluding the command name itself)
     */
    void execute(Game game, String[] args);

    /**
     * Get the help text for this command
     * @return Help text describing what this command does
     */
    String getHelpText();

    /**
     * Get alternative names/aliases for this command
     * @return Array of alternative command names
     */
    default String[] getAliases() {
        return new String[0];
    }
}