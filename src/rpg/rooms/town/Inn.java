package rpg.rooms.town;

import rpg.rooms.Room;
import rpg.core.Game;

public class Inn extends Room {
    private boolean blackjackIntroduced = false;

    public Inn() {
        super("The Weary Traveler Inn", "A cozy inn with a warm fireplace and comfortable beds.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
        game.getGui().displayMessage("The innkeeper looks up from cleaning a mug and nods at you.");
        game.getGui().displayMessage("'Welcome, traveler! Rest your weary bones here.'");

        if (!blackjackIntroduced) {
            game.getGui().displayMessage("");
            game.getGui().displayMessage("You notice a small card table in the corner with a deck of cards.");
            game.getGui().displayMessage("The innkeeper notices your interest: 'Fancy a game of blackjack?'");
            blackjackIntroduced = true;
        }
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the inn.");
        game.getGui().displayMessage("The common room is filled with:");
        game.getGui().displayMessage("- Wooden tables and chairs");
        game.getGui().displayMessage("- A crackling fireplace");
        game.getGui().displayMessage("- A few other travelers enjoying meals");
        game.getGui().displayMessage("- A staircase leading to the rooms upstairs");
        game.getGui().displayMessage("- A blackjack table in the corner with a fresh deck of cards");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("The innkeeper stands behind the bar, ready to serve.");
        game.getGui().displayMessage("'Try your luck at cards if you're feeling brave!' he calls out.");
        game.getGui().displayMessage("");
        displayConnections(game);
    }
}