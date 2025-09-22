package rpg.rooms.town;

import rpg.core.Game;
import rpg.rooms.Room;

public class ForgottenTunnels extends Room {

    public ForgottenTunnels() {
        super("Forgotten Tunnels", "Ancient stone tunnels that predate the town above.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("These tunnels stretch beneath the entire town, carved from " +
                "living rock. The walls bear the same strange runes you've seen elsewhere, and " +
                "you can feel a faint pulse emanating from deeper underground.");

        game.getGui().displayMessage("Thank you for playing :)");
    }
}
