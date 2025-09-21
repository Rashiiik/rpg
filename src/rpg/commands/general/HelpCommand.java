package rpg.commands.general;

import rpg.commands.Command;
import rpg.core.Game;
import java.util.Map;

public class HelpCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        if (args.length > 0) {
            String commandName = args[0].toLowerCase();
            Command command = game.getCommandParser().getCommands().get(commandName);
            if (command != null) {
                game.getGui().displayMessage("Help for '" + commandName + "':");
                game.getGui().displayMessage(command.getHelpText());
                if (command.getAliases().length > 0) {
                    game.getGui().displayMessage("Aliases: " + String.join(", ", command.getAliases()));
                }
            } else {
                game.getGui().displayMessage("Unknown command: " + commandName);
            }
        } else {
            game.getGui().displayMessage("=== Available Commands ===");

            Map<String, Command> commands = game.getCommandParser().getCommands();

            commands.entrySet().stream()
                    .filter(entry -> !isAlias(entry.getKey(), entry.getValue()))
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        game.getGui().displayMessage("- " + entry.getKey() + ": " + entry.getValue().getHelpText());
                    });

            game.getGui().displayMessage("");
            game.getGui().displayMessage("Type 'help <command>' for detailed help on a specific command.");
        }
    }

    private boolean isAlias(String commandName, Command command) {
        for (String alias : command.getAliases()) {
            if (alias.equals(commandName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getHelpText() {
        return "Show available commands or get help for a specific command";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"h", "?"};
    }
}