package rpg.commands.general;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;

public class StatsCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        Player player = game.getPlayer();

        game.getGui().displayMessage("=== Character Stats ===");
        game.getGui().displayMessage("Name: " + player.getName());
        game.getGui().displayMessage("HP: " + player.getHp() + "/" + player.getMaxHp());
        game.getGui().displayMessage("Gold: " + player.getGold());
    }

    @Override
    public String getHelpText() {
        return "Display your character's statistics";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"stat", "status", "info"};
    }
}