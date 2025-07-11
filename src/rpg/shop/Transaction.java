package rpg.shop;

import rpg.items.Item;
import rpg.player.Player;

public class Transaction {
    private TransactionType type;
    private Item item;
    private int quantity;
    private int totalPrice;
    private Player player;

    // Constructor with explicit total price
    public Transaction(TransactionType type, Item item, int quantity, int totalPrice, Player player) {
        this.type = type;
        this.item = item;
        this.quantity = quantity;
        this.player = player;
        this.totalPrice = totalPrice;
    }

    // Original constructor for backward compatibility
    public Transaction(TransactionType type, Item item, int quantity, Player player) {
        this.type = type;
        this.item = item;
        this.quantity = quantity;
        this.player = player;
        this.totalPrice = calculateTotalPrice();
    }

    private int calculateTotalPrice() {
        switch (type) {
            case BUY:
                return item.getValue() * quantity;
            case SELL:
                return (int) (item.getValue() * 0.5 * quantity); // Sell for half price
            default:
                return 0;
        }
    }

    public boolean canExecute() {
        switch (type) {
            case BUY:
                return player.getGold() >= totalPrice;
            case SELL:
                return player.getInventory().hasItem(item.getName()) &&
                        player.getInventory().getItemCount(item.getName()) >= quantity;
            default:
                return false;
        }
    }

    public boolean execute() {
        if (!canExecute()) {
            return false;
        }

        switch (type) {
            case BUY:
                player.removeGold(totalPrice);
                for (int i = 0; i < quantity; i++) {
                    player.getInventory().addItem(item);
                }
                return true;
            case SELL:
                for (int i = 0; i < quantity; i++) {
                    player.getInventory().removeItem(item.getName());
                }
                player.addGold(totalPrice);
                return true;
            default:
                return false;
        }
    }

    // Getters
    public TransactionType getType() { return type; }
    public Item getItem() { return item; }
    public int getQuantity() { return quantity; }
    public int getTotalPrice() { return totalPrice; }
    public Player getPlayer() { return player; }
}