package rpg.items;

import rpg.player.Player;
import rpg.core.Game;
import rpg.rooms.town.Shop;

public class Key extends Item {
    private String keyType;

    public Key(String keyType) {
        super(keyType + " Key", "A key that opens " + keyType + " doors", 0, false,
                new String[]{"key", keyType.toLowerCase()});
        this.keyType = keyType;
    }

    public Key() {
        this("Generic");
    }

    @Override
    public void examine(Game game) {
        if (keyType.equals("Basement")) {
            game.getGui().displayMessage("This is an old iron key with intricate engravings along its shaft.");
            game.getGui().displayMessage("The head of the key bears mystical symbols that seem to shift when you're not looking directly at them.");
            game.getGui().displayMessage("It feels heavy for its size, as if it holds more than just the power to unlock doors.");
            if (!game.getStoryFlags().hasFlag("basement_unlocked")) {
                game.getGui().displayMessage("You sense this key is meant for something specific in the shop...");
            }
        } else {
            game.getGui().displayMessage("An ordinary key, though it bears the craftsmanship of a skilled locksmith.");
            game.getGui().displayMessage("The metal is slightly tarnished but still strong.");
        }
    }

    @Override
    public void use(Player player) {
        // Get the game instance (you might need to pass this differently based on your architecture)
        Game game = player.getGame(); // Assuming Player has access to Game

        game.getGui().displayMessage("You examine the " + getName() + ". It looks like it might open something important.");
        game.getGui().displayMessage("Try using 'use key on door' or 'use key on basement' to unlock something specific.");
    }

    // Method for using key on specific targets
    public void useOn(Player player, String target, Game game) {
        if (keyType.equals("Basement")) {
            // Check if player is in the shop
            if (!(game.getCurrentRoom() instanceof Shop)) {
                game.getGui().displayMessage("You need to be in the shop to use this key.");
                return;
            }

            Shop shop = (Shop) game.getCurrentRoom();
            String lowerTarget = target.toLowerCase();

            // Check if target is basement door related
            if (lowerTarget.contains("door") || lowerTarget.contains("basement") ||
                    lowerTarget.contains("downstairs") || lowerTarget.equals("down")) {

                if (shop.canAccessBasement(game)) {
                    game.getGui().displayMessage("The basement door is already unlocked.");
                } else {
                    // Use the key to unlock the basement
                    shop.unlockBasement(game);

                    // Remove the key from player's inventory since it's been used
                    player.removeItem(this);
                    game.getGui().displayMessage("The " + getName() + " has been used and is no longer in your inventory.");
                }
            } else {
                game.getGui().displayMessage("You can't use the " + getName() + " on that.");
                game.getGui().displayMessage("Try using it on the 'door' or 'basement'.");
            }
        } else {
            game.getGui().displayMessage("You examine the " + getName() + ". It looks like it might open something important.");
        }
    }

    public String getKeyType() {
        return keyType;
    }
}