package rpg.rooms.town;

import rpg.items.Item;
import rpg.items.Key;
import rpg.player.Player;
import rpg.rooms.Room;
import rpg.core.Game;

public class Inn extends Room {
    private boolean blackjackIntroduced = false;
    private InnArchives innArchives;

    public Inn() {
        super("The Weary Traveler Inn", "A cozy inn with a warm fireplace and comfortable beds.");
        this.innArchives = new InnArchives();
        addItem(new Key("archives"));
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
        game.getGui().displayMessage("- A few other patrons enjoying meals");
        game.getGui().displayMessage("- A staircase leading to the rooms upstairs");
        game.getGui().displayMessage("- A blackjack table in the corner with a fresh deck of cards");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("The innkeeper stands behind the bar, ready to serve.");
        game.getGui().displayMessage("'Try your luck at cards if you're feeling brave!' he calls out.");
        game.getGui().displayMessage("");
        displayConnections(game);
        displayItems(game);
    }

    public boolean handleExamine(Game game, String target) {
        String lowerTarget = target.toLowerCase();

        if(lowerTarget.contains("stairs") || lowerTarget.contains("staircase")) {
            examineStairs(game);
            return true;
        }

        if (lowerTarget.contains("patrons") || lowerTarget.contains("travellers")) {
            examinePatrons(game);
            return true;
        }

        if (lowerTarget.contains("floor") || lowerTarget.contains("ground")) {
            examineFloor(game);
            return true;
        }

        return false;
    }

    public void examineStairs(Game game) {
        game.getGui().displayMessage("Wooden stairs leading to guest rooms that seem to shift when unobserved");
    }

    public void examinePatrons(Game game) {
        game.getGui().displayMessage("The guests avoid eye contact, but you feel them watching when you look away.");
    }

    public void examineFloor(Game game) {
        game.getGui().displayMessage("You notice a hidden trapdoor behind the bar.");
    }

    public boolean handleUseItemOn(Game game, Player player, Item item, String targetName) {

        if (item instanceof Key) {
            Key key = (Key) item;

            if ("archives".equals(key.getKeyType())) {
                boolean isTrapDoor = isTrapDoorTarget(targetName);

                if (isTrapDoor) {
                    if (game.getStoryFlags().hasFlag("trapdoor_unlocked")) {
                        game.getGui().displayMessage("The trapdoor is already unlocked");
                        return true;
                    }

                    game.getGui().displayMessage("You insert the key into the trapdoor");
                    game.getGui().displayMessage("It opens with a click");

                    player.removeItem(key);

                    setupInnArchivesConnection();
                }
            }
        }
        return false;
    }

    private boolean isTrapDoorTarget(String targetName) {
        String lower = targetName.toLowerCase();
        return lower.contains("door") ||
                lower.contains("basement") ||
                lower.contains("trapdoor") ||
                lower.contains("keyhole") ||
                lower.contains("lock");
    }

    private void setupInnArchivesConnection() {
        innArchives.addConnection("up", this);
        innArchives.addConnection("upstairs", this);
        innArchives.addConnection("inn", this);
        innArchives.addConnection("back", this);

        this.addConnection("down", innArchives);
        this.addConnection("trapdoor", innArchives);
        this.addConnection("basement", innArchives);
    }

}