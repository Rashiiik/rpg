package rpg.rooms.town;

import rpg.rooms.Room;
import rpg.core.Game;

public class TownCenter extends Room {

    public TownCenter() {
        super("Town Hall", "A stately brick building that serves as the administrative center of the town, where civic matters are handled and records are kept.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
        game.getGui().displayMessage("The town clerk looks up from filing documents and adjusts her glasses.");
        game.getGui().displayMessage("'Hello there! Welcome to town hall. We handle all civic matters here.'");
        game.getGui().displayMessage("'There have been some interesting developments lately - missing persons reports, strange property disputes...'");
        game.getGui().displayMessage("'Perhaps someone with your investigative skills could help us sort through these cases.'");
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the town hall.");
        game.getGui().displayMessage("The office is filled with:");
        game.getGui().displayMessage("- Filing cabinets lined against the walls");
        game.getGui().displayMessage("- A large desk covered in paperwork and reports");
        game.getGui().displayMessage("- Local maps and town planning documents");
        game.getGui().displayMessage("- A bulletin board with community notices");
        game.getGui().displayMessage("- Records of property deeds and permits");
        game.getGui().displayMessage("- A telephone and typewriter on the desk");
        game.getGui().displayMessage("- Several chairs for visitors");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("The town clerk is organized and efficient, ready to discuss local issues.");
        game.getGui().displayMessage("Citizens occasionally come in to file complaints or request information.");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("A sign on the wall reads: 'Serving the community through transparency and cooperation.'");
        game.getGui().displayMessage("");
        displayConnections(game);
    }
}