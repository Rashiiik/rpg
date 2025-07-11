package rpg.items;

import rpg.player.Player;

public class SilverCompass extends Item {

    public SilverCompass() {
        super("Silver Compass", "A mystical compass that ticks despite having no hands. It seems to point toward hidden secrets.", 150, false);
    }

    @Override
    public void use(Player player) {
        System.out.println("The compass ticks rhythmically, its needle spinning to point at something beyond ordinary direction.");
        System.out.println("You feel it might guide you to hidden treasures or secret passages.");
    }
}