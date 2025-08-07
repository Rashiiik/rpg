package rpg.items;

import rpg.player.Player;
import rpg.core.Game;
import rpg.rooms.Room;
import rpg.rooms.town.Shop;

public class OldCoin extends Item {

    public OldCoin() {
        super("Old Coin",
                "An ancient coin with strange markings.",
                5,
                false,
                new String[]{"coin", "old", "ancient coin"});
    }

    @Override
    public void use(Player player) {
        System.out.println("The old coin glows faintly when you hold it. It seems to be meant for something specific.");
    }

    public void useOn(Player player, String target, Game game) {
        Room currentRoom = game.getCurrentRoom();
        String lowerTarget = target.toLowerCase();

        if (currentRoom instanceof Shop) {
            Shop shop = (Shop) currentRoom;

            if (isCounterTarget(lowerTarget)) {
                handleCoinOnCounter(game, player, shop);
                return;
            }
        }

        game.getGui().displayMessage("You try to use the " + getName() + " on " + target + ".");
        if (!(currentRoom instanceof Shop)) {
            game.getGui().displayMessage("This old coin doesn't seem to belong here. Maybe try it in a shop?");
        } else {
            game.getGui().displayMessage("The coin doesn't fit there. Try using it on the counter.");
        }
    }

    private boolean isCounterTarget(String targetName) {
        if (targetName == null) {
            return false;
        }

        String lower = targetName.toLowerCase().trim();

        return lower.equals("counter") ||
                lower.equals("wooden counter") ||
                lower.equals("indentation") ||
                lower.equals("indent") ||
                lower.equals("circular indentation") ||
                (lower.contains("counter") && lower.contains("indentation"));
    }

    private void handleCoinOnCounter(Game game, Player player, Shop shop) {
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("The display case is already open.");
            return;
        }

        game.getGui().displayMessage("You place the old coin into the circular indentation on the counter.");
        game.getGui().displayMessage("The coin fits perfectly, and you hear a soft *click*.");
        game.getGui().displayMessage("The glass display case opens silently, revealing its contents.");
        game.getGui().displayMessage("A magnificent golden revolver with gleaming silver bullets is now accessible.");

        player.removeItem(this);
        game.getStoryFlags().addFlag("opened_display_case");

        shop.addItem(new GoldenRevolver());
    }
}