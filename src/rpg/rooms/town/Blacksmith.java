package rpg.rooms.town;

import rpg.rooms.Room;
import rpg.core.Game;

public class Blacksmith extends Room {

    public Blacksmith() {
        super("The Iron Forge", "A hot, smoky blacksmith shop with the sound of hammering metal.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
        game.getGui().displayMessage("The blacksmith pauses his work and wipes sweat from his brow.");
        game.getGui().displayMessage("'Ho there! Looking for some fine metalwork?'");
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the blacksmith shop.");
        game.getGui().displayMessage("The forge is filled with:");
        game.getGui().displayMessage("- A blazing furnace");
        game.getGui().displayMessage("- Various hammers and tools");
        game.getGui().displayMessage("- Weapons and armor on display");
        game.getGui().displayMessage("- Raw metal ingots");
        game.getGui().displayMessage("- An anvil with sparks flying");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("The blacksmith stands ready to craft or repair items.");
        game.getGui().displayMessage("");
        displayConnections(game);
    }
}