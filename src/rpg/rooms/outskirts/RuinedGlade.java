package rpg.rooms.outskirts;

import rpg.core.Game;
import rpg.rooms.Room;

public class RuinedGlade extends Room  {

    public RuinedGlade() {
        super("Ruined Glade", "A desolate glade filled with broken stone monuments and an oppressive atmosphere.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You emerge into a circular glade surrounded by crumbling stone monuments.");
        game.getGui().displayMessage("The air here feels thick and wrong. In the center, you see a figure sitting motionless.");
        game.getGui().displayMessage("Among the ruins - it's Linna, but something about her seems... changed.");
        displayConnections(game);
    }


}
