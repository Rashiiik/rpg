package rpg.items;

import rpg.player.Player;

public class OldCoin extends Item {

    public OldCoin() {
        super("Old Coin", "An ancient coin with strange markings. It feels warm to the touch.", 5, false);
    }

    @Override
    public void use(Player player) {
        System.out.println("The old coin glows faintly when you hold it. It seems to be meant for something specific.");
    }
}