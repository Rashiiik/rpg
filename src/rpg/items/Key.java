package rpg.items;

import rpg.player.Player;

public class Key extends Item {
    private String keyType;

    public Key(String keyType) {
        super(keyType + " Key", "A key that opens " + keyType + " doors", 0, false);
        this.keyType = keyType;
    }

    public Key() {
        this("Generic");
    }

    @Override
    public void use(Player player) {
        System.out.println("You examine the " + getName() + ". It looks like it might open something important.");
    }

    public String getKeyType() {
        return keyType;
    }
}
