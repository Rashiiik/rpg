package rpg.items;

import rpg.player.Player;

public class GoldenRevolver extends Item {

    public GoldenRevolver() {
        super("Golden Revolver", "A magnificent golden revolver with intricate engravings along the barrel. The cylinder is loaded with gleaming silver bullets that seem to shimmer with an otherworldly light.", 250, false);
    }

    @Override
    public void use(Player player) {
        System.out.println("You raise the golden revolver, feeling its weight and power in your hands.");
        System.out.println("The silver bullets catch the light as the cylinder spins with a satisfying click.");
        System.out.println("You sense this weapon would be particularly effective against supernatural enemies.");
    }
}