package rpg.rooms.outskirts;

import rpg.core.Game;
import rpg.items.Key;
import rpg.rooms.Room;

public class RuinedGlade extends Room  {

    public RuinedGlade() {
        super("Ruined Glade", "A desolate glade filled with broken stone monuments and an oppressive atmosphere.");
        addItem(new Key("archives"));
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

    public boolean handleExamine(Game game, String taget) {
        String lowerTarget = taget.toLowerCase();

        if (lowerTarget.contains("lina")) {
            game.getGui().displayMessage("Linna looks at you with recognition, but her eyes are wrong.");
            game.getGui().displayMessage( "\"You... you're real, aren't you? Not like the others. " +
                    "Help me get back to town, but beware - nothing is as it seems.\"");
            return true;
        }

        return false;
    }


}
