package rpg.items;

import rpg.player.Player;
import rpg.core.Game;
import rpg.rooms.Room;
import rpg.rooms.town.Shop;
import java.lang.reflect.Method;

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
        if (keyType.equals("Tutorial")) {
            game.getGui().displayMessage("This is a brass key, tarnished but ornate.");
            game.getGui().displayMessage("The handle forms a tiny eight-pointed star, and the number 0 is etched into its side.");
            game.getGui().displayMessage("It feels cold to the touch, as if it holds memories of forgotten places.");
            game.getGui().displayMessage("You sense this key is meant for the iron door in the alley.");
        } else if (keyType.equals("Basement")) {
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
        Game game = player.getGame();

        if (keyType.equals("Tutorial")) {
            game.getGui().displayMessage("You examine the " + getName() + ". The brass gleams dully in the flickering light.");
            game.getGui().displayMessage("Try using 'use key on door' to unlock the iron door in the alley.");
        } else if (keyType.equals("Basement")) {
            game.getGui().displayMessage("You examine the " + getName() + ". It looks like it might open something important.");
            game.getGui().displayMessage("Try using 'use key on door' or 'use key on basement' to unlock something specific.");
        } else {
            game.getGui().displayMessage("You examine the " + getName() + ". It looks like it might open something important.");
            game.getGui().displayMessage("Try using 'use key on door' or similar to unlock something specific.");
        }
    }

    public void useOn(Player player, String target, Game game) {
        Room currentRoom = game.getCurrentRoom();
        boolean handled = false;

        if (currentRoom != null) {
            try {
                Method method = findHandleUseItemOnMethod(currentRoom.getClass());
                if (method != null) {
                    Object result = method.invoke(currentRoom, game, player, this, target);
                    if (result instanceof Boolean && (Boolean) result) {
                        handled = true;
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!handled) {
            handleKeySpecificLogic(player, target, game, currentRoom);
        }
    }

    private Method findHandleUseItemOnMethod(Class<?> roomClass) {
        Class<?> currentClass = roomClass;

        while (currentClass != null) {
            try {
                Method method = currentClass.getDeclaredMethod("handleUseItemOn",
                        Game.class, Player.class, Item.class, String.class);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                currentClass = currentClass.getSuperclass();
            }
        }

        return null;
    }

    private void handleKeySpecificLogic(Player player, String target, Game game, Room currentRoom) {
        String lowerTarget = target.toLowerCase();

        if (keyType.equals("Basement")) {
            handleBasementKey(player, target, lowerTarget, game, currentRoom);
        } else if (keyType.equals("Tutorial")) {
            handleTutorialKey(player, target, lowerTarget, game, currentRoom);
        } else {
            game.getGui().displayMessage("You try to use the " + getName() + " on " + target + ".");
            game.getGui().displayMessage("The key doesn't seem to work here. Try moving to the right location.");
        }
    }

    private void handleBasementKey(Player player, String target, String lowerTarget, Game game, Room currentRoom) {
        if (currentRoom instanceof Shop) {
            Shop shop = (Shop) currentRoom;

            if (lowerTarget.contains("door") || lowerTarget.contains("basement") ||
                    lowerTarget.contains("keyhole") || lowerTarget.contains("lock")) {

                if (game.getStoryFlags().hasFlag("basement_unlocked")) {
                    game.getGui().displayMessage("The basement door is already unlocked.");
                } else {
                    game.getGui().displayMessage("You insert the basement key into the large keyhole.");
                    game.getGui().displayMessage("The key turns with a satisfying *click*!");

                    player.removeItem(this);

                    shop.unlockBasement(game);
                }
                return;
            }
        }

        game.getGui().displayMessage("You try to use the " + getName() + " on " + target + ".");
        if (!(currentRoom instanceof Shop)) {
            game.getGui().displayMessage("This basement key doesn't seem to belong here. Maybe try it in a shop?");
        } else {
            game.getGui().displayMessage("The key doesn't fit. Try using it on the basement door.");
        }
    }

    private void handleTutorialKey(Player player, String target, String lowerTarget, Game game, Room currentRoom) {
        game.getGui().displayMessage("You try to use the " + getName() + " on " + target + ".");
        game.getGui().displayMessage("The tutorial key doesn't seem to work here yet. Implementation needed for this location.");
    }

    private boolean hasHandleUseItemOnMethod(Room room) {
        return findHandleUseItemOnMethod(room.getClass()) != null;
    }

    public String getKeyType() {
        return keyType;
    }
}