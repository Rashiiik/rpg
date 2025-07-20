package rpg.rooms.tutorial;

import rpg.rooms.Room;
import rpg.core.Game;
import rpg.items.Item;
import rpg.items.Key;
import rpg.player.Player;

public class StartingAlley extends Room {
    private boolean sigilExamined = false;
    private Game game;

    public StartingAlley() {
        super("Forgotten Alley", "You awaken on cold concrete. Your body aches. The fog clings to your skin like damp linen — heavy, unshakable.");
    }

    @Override
    public void enter(Game game) {
        if (!game.getStoryFlags().hasFlag("tutorial_started")) {
            // First time entering - show the full opening narrative
            game.getGui().displayMessage("🕯️ WHERE THE FOG BEGINS 🕯️");
            game.getGui().displayMessage("");
            game.getGui().displayMessage("You awaken on cold concrete. Your body aches.");
            game.getGui().displayMessage("The fog clings to your skin like damp linen — heavy, unshakable.");
            game.getGui().displayMessage("");
            game.getGui().displayMessage("Somewhere above, neon flickers behind the haze.");
            game.getGui().displayMessage("A broken sign pulses in dull amber: \"BEGIN.\"");
            game.getGui().displayMessage("");
            game.getGui().displayMessage("The alley stretches only a dozen feet, hemmed in by high walls and wire.");
            game.getGui().displayMessage("There is no wind. No birds. Only a low hum, like a city dreaming beneath you.");
            game.getGui().displayMessage("");
            game.getGui().displayMessage("You don't remember your name.");
            game.getGui().displayMessage("You don't remember the City.");
            game.getGui().displayMessage("But something remembers you.");
            game.getGui().displayMessage("");
            game.getGui().displayMessage("Type 'look' to examine your surroundings.");

            game.getStoryFlags().addFlag("tutorial_started");
        } else {
            game.getGui().displayMessage("You are back in the " + name);
            game.getGui().displayMessage("The fog still clings to everything, muffling sounds and obscuring vision.");
        }
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the narrow alley.");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("The alley is narrow — too narrow. As if the buildings on either side");
        game.getGui().displayMessage("were dragged together to trap you.");
        game.getGui().displayMessage("");

        if (!game.getStoryFlags().hasFlag("tutorial_door_unlocked")) {
            game.getGui().displayMessage("To your RIGHT, an iron door stands half-swallowed by the wall,");
            game.getGui().displayMessage("rust dripping like dried blood from its hinges.");
        } else {
            game.getGui().displayMessage("To your RIGHT, the iron door stands ajar, revealing a path beyond.");
        }

        game.getGui().displayMessage("");
        game.getGui().displayMessage("Ahead, a mound of trash smolders, twitching with flies.");
        game.getGui().displayMessage("");

        if (!sigilExamined) {
            game.getGui().displayMessage("To your LEFT, on soot-dark brick, something glows faintly —");
            game.getGui().displayMessage("a crooked sigil, etched in yellow and black chalk.");
            game.getGui().displayMessage("It pulses with an unseen rhythm.");
        } else {
            game.getGui().displayMessage("To your LEFT, the brick wall shows faint traces of chalk,");
            game.getGui().displayMessage("as if something important was once written there.");
        }

        game.getGui().displayMessage("");
        game.getGui().displayMessage("Somewhere behind the fog, you hear a metallic clang. Then silence.");

        if (game.getStoryFlags().hasFlag("tutorial_door_unlocked")) {
            game.getGui().displayMessage("");
            game.getGui().displayMessage("You can go FORWARD through the iron door to the town square.");
        }

        displayItems(game);

        displayConnections(game);
    }

    // Handle examination of specific objects
    public boolean handleExamine(Game game, String target) {
        String lowerTarget = target.toLowerCase().trim();

        if (lowerTarget.contains("sigil") || lowerTarget.contains("symbol") || lowerTarget.contains("chalk")) {
            examineSigil(game);
            return true;
        }

        if (lowerTarget.contains("trash") || lowerTarget.contains("pile") || lowerTarget.contains("mound")) {
            examineTrash(game);
            return true;
        }

        if (lowerTarget.contains("door") || lowerTarget.contains("iron")) {
            examineDoor(game);
            return true;
        }

        if (lowerTarget.contains("wall") || lowerTarget.contains("walls") || lowerTarget.contains("brick")) {
            examineWalls(game);
            return true;
        }

        if (lowerTarget.contains("fog") || lowerTarget.contains("mist")) {
            examineFog(game);
            return true;
        }

        if (lowerTarget.contains("sign") || lowerTarget.contains("neon")) {
            examineSign(game);
            return true;
        }

        return false;
    }

    private void examineSigil(Game game) {
        if (game.getStoryFlags().hasFlag("sigil_examined")) {
            game.getGui().displayMessage("The wall shows only faint chalk traces now.");
            game.getGui().displayMessage("But you remember the sigil's power, and the words it whispered.");
            return;
        }

        game.getGui().displayMessage("You step closer. The symbol is no mere graffiti — it's deliberate, ritualistic.");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("A jagged circle surrounds a twisting crown-like shape.");
        game.getGui().displayMessage("Yellow lines spiral out, swallowing black ink.");
        game.getGui().displayMessage("Beneath it, one word is scratched over and over in smaller, frantic letters:");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("FOOL");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("As you stare, your breath catches. The fog thickens.");
        game.getGui().displayMessage("Words surface in your mind — not spoken, not read, but known:");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("'The Fool that doesn't belong to this era'");
        game.getGui().displayMessage("'The mysterious ruler above the gray fog'");
        game.getGui().displayMessage("'The King of Yellow and Black that wields good luck'");
        game.getGui().displayMessage("'Praise the Fool.'");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("And just like that, the sigil dims. You feel… watched. Or perhaps remembered.");

        game.getStoryFlags().addFlag("sigil_examined");
    }

    private void examineTrash(Game game) {
        if (game.getStoryFlags().hasFlag("tutorial_key_found")) {
            game.getGui().displayMessage("The trash pile has been disturbed. Flies buzz angrily around the remaining refuse.");
            game.getGui().displayMessage("Whatever was hidden here has been claimed.");
            return;
        }

        game.getGui().displayMessage("The trash pile stinks of rain-soaked ash and something sweet, rotting.");
        game.getGui().displayMessage("As you prod it aside, your fingers brush something cold — metal.");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("You pull free a brass key, tarnished but ornate.");
        game.getGui().displayMessage("The handle forms a tiny eight-pointed star. The number 0 is etched into its side.");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("A whisper curls behind your ear: 'Luck favors the lost.'");

        addItem(new Key("Tutorial"));
        game.getStoryFlags().addFlag("tutorial_key_found");
    }

    private void examineDoor(Game game) {
        if (game.getStoryFlags().hasFlag("tutorial_door_unlocked")) {
            game.getGui().displayMessage("The iron door stands ajar, its tortured metal groaning softly.");
            game.getGui().displayMessage("Beyond it, you can see a path leading to what looks like a town square.");
            game.getGui().displayMessage("Warm light filters through the opening, a welcome contrast to the fog.");
        } else {
            game.getGui().displayMessage("The rusted iron door is sealed tight against the wall.");
            game.getGui().displayMessage("It has no handle — only a keyhole shaped like a half-moon.");
            game.getGui().displayMessage("Rust drips like dried blood from its hinges.");

            if (game.getStoryFlags().hasFlag("tutorial_key_found")) {
                game.getGui().displayMessage("The brass key in your possession might fit this lock...");
            }
        }
    }

    private void examineWalls(Game game) {
        game.getGui().displayMessage("The walls loom impossibly high, disappearing into the fog above.");
        game.getGui().displayMessage("They seem to lean inward, as if the alley is slowly crushing itself.");
        game.getGui().displayMessage("Soot and strange stains mark the brick, telling stories you don't want to read.");
    }

    private void examineFog(Game game) {
        game.getGui().displayMessage("The fog is unnatural — too thick, too still.");
        game.getGui().displayMessage("It doesn't move with any wind, but seems to pulse with its own rhythm.");
        game.getGui().displayMessage("Looking into it too long makes your eyes water and your head spin.");
        game.getGui().displayMessage("Something moves within it, just beyond sight.");
    }

    private void examineSign(Game game) {
        game.getGui().displayMessage("High above, barely visible through the fog, neon letters flicker:");
        game.getGui().displayMessage("'BEGIN' — though some letters are dark, others spark and hiss.");
        game.getGui().displayMessage("It might once have said something else, something longer.");
        game.getGui().displayMessage("But now it only offers this single, ominous command.");
    }

    // Handle using items on things in this room
    @Override
    public boolean handleUseItemOn(Game game, Player player, Item item, String targetName) {
        // DEBUG: Add debug output to track what's happening
        System.out.println("DEBUG - StartingAlley.handleUseItemOn called");
        System.out.println("DEBUG - Item: " + (item != null ? item.getClass().getSimpleName() + " - " + item.getName() : "null"));
        System.out.println("DEBUG - Target: '" + targetName + "'");

        // Add null checks for safety
        if (game == null || player == null || item == null || targetName == null) {
            System.out.println("DEBUG - Null parameter detected in StartingAlley");
            return false;
        }

        if (item instanceof Key) {
            Key key = (Key) item;
            System.out.println("DEBUG - Key type: " + key.getKeyType());

            String lowerTarget = targetName.toLowerCase().trim();
            System.out.println("DEBUG - Lower target: '" + lowerTarget + "'");

            if ("Tutorial".equals(key.getKeyType())) {
                boolean isDoorTarget = lowerTarget.contains("door") ||
                        lowerTarget.contains("iron") ||
                        lowerTarget.contains("keyhole") ||
                        lowerTarget.contains("lock") ||
                        lowerTarget.equals("iron door") ||
                        lowerTarget.contains("rusted") ||
                        lowerTarget.contains("metal");

                System.out.println("DEBUG - Is door target: " + isDoorTarget);

                if (isDoorTarget) {
                    if (game.getStoryFlags().hasFlag("tutorial_door_unlocked")) {
                        game.getGui().displayMessage("The door is already unlocked.");
                        return true;
                    }

                    System.out.println("DEBUG - Calling unlockDoor");
                    unlockDoor(game, player, item);
                    return true;
                } else {
                    // Key doesn't match this target in this room
                    game.getGui().displayMessage("The " + key.getName() + " doesn't seem to work on " + targetName + ".");
                    game.getGui().displayMessage("Try using it on the 'iron door' or just 'door'.");
                    return true; // We handled the attempt, even if it failed
                }
            }
        }

        System.out.println("DEBUG - No matching interaction found in StartingAlley");
        return false; // This room doesn't handle this interaction
    }

    private void unlockDoor(Game game, Player player, Item key) {
        game.getGui().displayMessage("You approach the rusted iron door.");
        game.getGui().displayMessage("The brass key slips into the half-moon keyhole like it's been waiting.");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("With a dull click and a scream of tortured metal,");
        game.getGui().displayMessage("the door shudders open a few inches.");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("Warm light spills through the opening, cutting through the fog.");
        game.getGui().displayMessage("Beyond, you can see a town square with cobblestone streets and welcoming buildings.");

        // Remove the key from player's inventory
        player.removeItem(key);

        // Mark door as unlocked
        game.getStoryFlags().addFlag("tutorial_door_unlocked");

        // Complete tutorial when door is unlocked
        completeTutorial(game);
    }

    private void completeTutorial(Game game) {
        game.getGui().displayMessage("");
        game.getGui().displayMessage("As the door opens fully, the fog behind you begins to dissipate.");
        game.getGui().displayMessage("The oppressive weight of the alley lifts from your shoulders.");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("You take a deep breath of clean, warm air.");
        game.getGui().displayMessage("Whatever brought you to this place, your journey forward begins now.");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("🎮 TUTORIAL COMPLETE 🎮");
        game.getGui().displayMessage("You can now explore the town and beyond!");

        game.getStoryFlags().addFlag("tutorial_complete");
    }

    // Override movement methods to handle going to town
    @Override
    public boolean hasConnection(String direction) {
        String lowerDirection = direction.toLowerCase();

        if (lowerDirection.equals("forward") || lowerDirection.equals("through") ||
                lowerDirection.equals("door") || lowerDirection.equals("town") ||
                lowerDirection.equals("out") || lowerDirection.equals("exit")) {
            return game.getStoryFlags().hasFlag("tutorial_door_unlocked");
        }

        return super.hasConnection(direction);
    }

    @Override
    public boolean attemptMove(String direction, Game game) {
        String lowerDirection = direction.toLowerCase();

        if (lowerDirection.equals("forward") || lowerDirection.equals("through") ||
                lowerDirection.equals("door") || lowerDirection.equals("town") ||
                lowerDirection.equals("out") || lowerDirection.equals("exit")) {
            if (!game.getStoryFlags().hasFlag("tutorial_door_unlocked")) {
                game.getGui().displayMessage("The iron door is locked tight. You need to find a way to unlock it first.");
                if (!game.getStoryFlags().hasFlag("tutorial_key_found")) {
                    game.getGui().displayMessage("Maybe there's something useful in the trash pile...");
                } else {
                    game.getGui().displayMessage("Try using your key on the door.");
                }
                return false;
            }
        }

        return super.attemptMove(direction, game);
    }

    @Override
    public Room tryMove(String direction, Game game) {
        String lowerDirection = direction.toLowerCase();

        if (lowerDirection.equals("forward") || lowerDirection.equals("through") ||
                lowerDirection.equals("door") || lowerDirection.equals("town") ||
                lowerDirection.equals("out") || lowerDirection.equals("exit")) {
            if (!attemptMove(direction, game)) {
                return null;
            }

            // Special transition message when leaving the alley
            game.getGui().displayMessage("You step through the iron doorway, leaving the fog-shrouded alley behind.");
            game.getGui().displayMessage("As you cross the threshold, you hear the door clang shut.");
            game.getGui().displayMessage("");
            game.getGui().displayMessage("When you turn to look back, there is only a brick wall.");
            game.getGui().displayMessage("The alley, the fog, the sigil — all gone, as if they never existed.");
            game.getGui().displayMessage("");
            game.getGui().displayMessage("Only the faint whisper remains: 'Praise the Fool.'");
            game.getGui().displayMessage("");
            game.getGui().displayMessage("Welcome to your new world.");

            // Get the town room from the room manager
            Room townSquare = game.getRoomManager().getRoom("town");
            return townSquare;
        }

        return super.tryMove(direction, game);
    }

    @Override
    public void displayConnections(Game game) {
        if (game.getStoryFlags().hasFlag("tutorial_door_unlocked")) {
            game.getGui().displayMessage("Available exits:");
            game.getGui().displayMessage("- forward (through the iron door to the town square)");
        } else {
            game.getGui().displayMessage("The iron door blocks the only visible exit.");
            game.getGui().displayMessage("You need to find a way to unlock it.");
        }
    }
}