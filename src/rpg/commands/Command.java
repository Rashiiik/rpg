package rpg.commands;

import rpg.core.Game;

public interface Command {

    void execute(Game game, String[] args);

    String getHelpText(); //Loki

    default String[] getAliases() {
        return new String[0];
    }
}