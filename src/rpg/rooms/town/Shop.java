package rpg.rooms.town;

import rpg.rooms.Room;
import rpg.core.Game;
import rpg.items.OldCoin;
import rpg.items.Item;
import rpg.items.Bullet;
import rpg.shop.ShopItem;
import java.util.ArrayList;
import java.util.List;

public class Shop extends Room {
    private List<ShopItem> shopInventory;

    public Shop() {
        super("Item Shop", "Tucked away in a fog-drenched alley of the Backlund docks—where gas lamps flicker and shadows stretch with minds of their own—stands a narrow, leaning storefront with windows dulled by age and secrets.");

        addItem(new OldCoin());
        initializeShopInventory();
    }

    private void initializeShopInventory() {
        shopInventory = new ArrayList<>();

        // Add some basic shop items
        try {
            // Create instances of items to sell
            shopInventory.add(new ShopItem(new Bullet(), 10, 8, 4)); // 10 bullets, buy for 8, sell for 4
            shopInventory.add(new ShopItem(new OldCoin(), 5, 15, 7)); // 5 coins, buy for 15, sell for 7

            // Add more items as needed - you can expand this list
        } catch (Exception e) {
            System.err.println("Error initializing shop inventory: " + e.getMessage());
        }
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
        game.getGui().displayMessage("A friendly shopkeeper greets you with a warm smile.");
        game.getGui().displayMessage("'Welcome to my shop! Feel free to browse my wares.'");

        if (!game.getStoryFlags().hasFlag("shop_visited")) {
            game.getGui().displayMessage("You notice a peculiar display case behind the counter, gleaming despite the shop's dusty atmosphere.");
            game.getStoryFlags().addFlag("shop_visited");
        }

        game.getGui().displayMessage("Type 'buy' to see what's for sale, or 'sell' to trade your items.");
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the shop.");
        game.getGui().displayMessage("The shelves are lined with:");
        game.getGui().displayMessage("- Food");
        game.getGui().displayMessage("- Books and Magazines");
        game.getGui().displayMessage("- Clothes");
        game.getGui().displayMessage("- Various tools and supplies");

        // Add puzzle elements description
        game.getGui().displayMessage("");
        game.getGui().displayMessage("Behind the counter, you notice:");
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("- An open display case with velvet lining");
            game.getGui().displayMessage("- A wooden counter with a coin-shaped indentation");
        } else {
            game.getGui().displayMessage("- A locked glass display case that pulses with a quiet hum");
            game.getGui().displayMessage("- A worn wooden counter");
        }

        game.getGui().displayMessage("");
        game.getGui().displayMessage("The shopkeeper stands behind the counter, ready to help.");
        game.getGui().displayMessage("Type 'buy' to see available items for purchase.");
        game.getGui().displayMessage("");
        displayConnections(game);

        // Show items in the room
        game.getGui().displayMessage("");
        displayItems(game);
    }

    public void displayShopInventory(Game game) {
        game.getGui().displayMessage("=== SHOP INVENTORY ===");

        if (shopInventory.isEmpty()) {
            game.getGui().displayMessage("The shop is currently out of stock.");
            return;
        }

        boolean hasItems = false;
        for (ShopItem shopItem : shopInventory) {
            if (shopItem.canBuy() && shopItem.hasStock()) {
                hasItems = true;
                String stockText = shopItem.getStock() > 1 ?
                        " (Stock: " + shopItem.getStock() + ")" : "";
                game.getGui().displayMessage("- " + shopItem.getItem().getName() +
                        " - " + shopItem.getBuyPrice() + " gold" + stockText);
                game.getGui().displayMessage("  " + shopItem.getItem().getDescription());
            }
        }

        if (!hasItems) {
            game.getGui().displayMessage("No items currently available for purchase.");
        }

        game.getGui().displayMessage("");
        game.getGui().displayMessage("Usage: buy <item name> [quantity]");
        game.getGui().displayMessage("       sell <item name> [quantity]");
    }

    public ShopItem findShopItem(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return null;
        }

        String searchTerm = itemName.toLowerCase().trim();

        for (ShopItem shopItem : shopInventory) {
            // Check if the item name matches
            if (shopItem.getItem().getName().toLowerCase().contains(searchTerm)) {
                return shopItem;
            }

            // Check if the item matches search keywords
            if (shopItem.getItem().matchesSearch(searchTerm)) {
                return shopItem;
            }
        }

        return null;
    }

    public List<ShopItem> getShopInventory() {
        return new ArrayList<>(shopInventory);
    }

    public void addShopItem(ShopItem shopItem) {
        if (shopItem != null) {
            shopInventory.add(shopItem);
        }
    }

    public void removeShopItem(ShopItem shopItem) {
        shopInventory.remove(shopItem);
    }

    public void restockShopItem(String itemName, int quantity) {
        ShopItem shopItem = findShopItem(itemName);
        if (shopItem != null) {
            shopItem.addStock(quantity);
        }
    }

    // Method to add items to shop inventory when player sells them
    public void addToShopInventory(Item item, int quantity) {
        ShopItem existingShopItem = findShopItem(item.getName());
        if (existingShopItem != null && existingShopItem.canSell()) {
            existingShopItem.addStock(quantity);
        } else {
            // Create a new shop item for this sold item
            ShopItem newShopItem = new ShopItem(item, quantity);
            shopInventory.add(newShopItem);
        }
    }
}