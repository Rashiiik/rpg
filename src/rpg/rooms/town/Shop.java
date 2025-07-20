package rpg.rooms.town;

import rpg.items.OldCoin;
import rpg.items.Key;
import rpg.rooms.Room;
import rpg.core.Game;
import rpg.items.Item;
import rpg.items.Bullet;
import rpg.shop.ShopItem;
import rpg.utils.StringUtils;
import rpg.player.Player;
import java.util.ArrayList;
import java.util.List;

public class Shop extends Room {
    private List<ShopItem> shopInventory;
    private ShopBasement basement;

    public Shop() {
        super("Item Shop", "Tucked away in a fog-drenched alley of the Backlund docks—where gas lamps flicker and shadows stretch with minds of their own—stands a narrow, leaning storefront with windows dulled by age and secrets.");

        addItem(new OldCoin());
        addItem(new Key("Basement")); // Add the basement key to the shop

        // Create the basement
        basement = new ShopBasement();

        initializeShopInventory();
        setupBasementConnection();
    }

    public boolean handleUseItemOn(Game game, Player player, Item item, String targetName) {
        System.out.println("DEBUG - Shop.handleUseItemOn called");
        System.out.println("DEBUG - Item: " + (item != null ? item.getClass().getSimpleName() + " - " + item.getName() : "null"));
        System.out.println("DEBUG - Target: '" + targetName + "'");

        // Add null checks for safety
        if (game == null || player == null || item == null || targetName == null) {
            System.out.println("DEBUG - Null parameter detected");
            return false;
        }

        // Handle OldCoin on counter interaction
        if (item instanceof OldCoin) {
            System.out.println("DEBUG - Item is OldCoin");
            boolean isCounter = isCounterTarget(targetName);
            System.out.println("DEBUG - Is counter target: " + isCounter);

            if (isCounter) {
                System.out.println("DEBUG - Calling handleCoinOnCounter");
                handleCoinOnCounter(game, player, item);
                return true;
            }
        }

        // Handle Key on basement door interaction
        if (item instanceof Key) {
            System.out.println("DEBUG - Item is Key");
            Key key = (Key) item;
            boolean isBasementDoor = isBasementDoorTarget(targetName);
            System.out.println("DEBUG - Is basement door target: " + isBasementDoor);
            System.out.println("DEBUG - Key type: " + key.getKeyType());

            if (isBasementDoor && ("Basement".equals(key.getKeyType()) || "basement".equalsIgnoreCase(key.getKeyType()))) {
                System.out.println("DEBUG - Calling handleKeyOnBasementDoor");
                handleKeyOnBasementDoor(game, player, key);
                return true;
            }
        }

        System.out.println("DEBUG - No matching interaction found");
        return false; // This room doesn't handle this interaction
    }

    private boolean isBasementDoorTarget(String targetName) {
        String lower = targetName.toLowerCase();
        return lower.contains("door") ||
                lower.contains("basement") ||
                lower.contains("wooden door") ||
                lower.contains("keyhole") ||
                lower.contains("lock");
    }

    // Add method to handle key on basement door
    private void handleKeyOnBasementDoor(Game game, Player player, Key key) {
        if (game.getStoryFlags().hasFlag("basement_unlocked")) {
            game.getGui().displayMessage("The basement door is already unlocked.");
            return;
        }

        game.getGui().displayMessage("You insert the basement key into the large keyhole.");
        game.getGui().displayMessage("The key turns with a satisfying *click*!");

        // Remove the key from player's inventory
        player.removeItem(key);

        // Unlock the basement
        unlockBasement(game);
    }

    private boolean isCounterTarget(String targetName) {
        if (targetName == null) {
            System.out.println("DEBUG - isCounterTarget: targetName is null");
            return false;
        }

        String lower = targetName.toLowerCase().trim();
        System.out.println("DEBUG - isCounterTarget: checking '" + lower + "'");

        boolean result = lower.equals("counter") ||
                lower.equals("wooden counter") ||
                lower.equals("indentation") ||
                lower.equals("indent") ||
                lower.equals("circular indentation") ||
                (lower.contains("counter") && lower.contains("indentation"));

        System.out.println("DEBUG - isCounterTarget result: " + result);
        return result;
    }

    private void handleCoinOnCounter(Game game, Player player, Item coin) {
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("The display case is already open.");
            return;
        }

        // Execute the puzzle solution
        game.getGui().displayMessage("You place the old coin into the circular indentation on the counter.");
        game.getGui().displayMessage("The coin fits perfectly, and you hear a soft *click*.");
        game.getGui().displayMessage("The glass display case opens silently, revealing its contents.");
        game.getGui().displayMessage("A magnificent golden revolver with gleaming silver bullets is now accessible.");

        // Remove the coin and set story flag
        player.removeItem(coin);
        game.getStoryFlags().addFlag("opened_display_case");

        // FIXED: Add the item to THIS shop room, not just any current room
        this.addItem(new rpg.items.GoldenRevolver());
    }

    private void setupBasementConnection() {
        // The basement connection back to shop
        basement.addConnection("up", this);
        basement.addConnection("upstairs", this);
        basement.addConnection("shop", this);
        basement.addConnection("back", this);
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
            game.getGui().displayMessage("In the corner, you spot a heavy wooden door with iron bands - it appears to lead downstairs.");
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

        // Basement door description
        game.getGui().displayMessage("");
        game.getGui().displayMessage("In the corner, you see:");
        if (game.getStoryFlags().hasFlag("basement_unlocked")) {
            game.getGui().displayMessage("- An open wooden door leading to a basement (you can go 'down' or 'basement')");
        } else {
            game.getGui().displayMessage("- A heavy wooden door with iron bands and a large keyhole");
            game.getGui().displayMessage("- The door appears to be locked and leads downstairs");
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

    // Add examination method for shop-specific items
    public boolean handleExamine(Game game, String target) {
        String lowerTarget = target.toLowerCase();

        if (lowerTarget.contains("counter") || lowerTarget.contains("indentation") || lowerTarget.contains("indent")) {
            examineCounter(game);
            return true;
        }

        if (lowerTarget.contains("display") || lowerTarget.contains("case") || lowerTarget.contains("glass")) {
            examineDisplayCase(game);
            return true;
        }

        if (lowerTarget.contains("shopkeeper") || lowerTarget.contains("keeper") || lowerTarget.contains("merchant")) {
            examineShopkeeper(game);
            return true;
        }

        if (lowerTarget.contains("shelf") || lowerTarget.contains("shelves")) {
            examineShelves(game);
            return true;
        }

        if (lowerTarget.contains("window") || lowerTarget.contains("windows")) {
            examineWindows(game);
            return true;
        }

        if (lowerTarget.contains("door") && (lowerTarget.contains("basement") || lowerTarget.contains("wooden") || lowerTarget.contains("iron"))) {
            examineBasementDoor(game);
            return true;
        }

        return false; // Not handled by shop
    }

    private void examineCounter(Game game) {
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("You run your fingers along the counter's surface.");
            game.getGui().displayMessage("The circular indentation is perfectly smooth, as if the coin melted into it.");
            game.getGui().displayMessage("You notice faint magical residue around the edges.");
        } else {
            game.getGui().displayMessage("You examine the counter closely.");
            game.getGui().displayMessage("The circular indentation is precisely carved - definitely not natural wear.");
            game.getGui().displayMessage("Running your finger around its edge, you feel a faint magical tingle.");
            game.getGui().displayMessage("This was made to hold something specific...");
        }
    }

    private void examineDisplayCase(Game game) {
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("The display case's glass is crystal clear, without a single fingerprint.");
            game.getGui().displayMessage("The opening mechanism is completely hidden - no hinges or locks visible.");
            game.getGui().displayMessage("Whatever magic opened this, it's beyond your understanding.");
        } else {
            game.getGui().displayMessage("You press your face close to the glass.");
            game.getGui().displayMessage("Inside, you can make out the shape of a magical compass.");
            game.getGui().displayMessage("The glass itself seems to shimmer with protective enchantments.");
            game.getGui().displayMessage("No amount of force would break this - it needs to be opened properly.");
        }
    }

    private void examineShopkeeper(Game game) {
        game.getGui().displayMessage("You study the shopkeeper carefully.");
        game.getGui().displayMessage("Their eyes hold ancient wisdom, far older than their appearance suggests.");
        game.getGui().displayMessage("Small scars on their hands tell of a life beyond shopkeeping.");
        game.getGui().displayMessage("When they think you're not looking, they glance meaningfully at the display case.");
    }

    private void examineShelves(Game game) {
        game.getGui().displayMessage("You examine the shelves more closely.");
        game.getGui().displayMessage("Everything is organized by more than just type - there's a pattern here.");
        game.getGui().displayMessage("Magical items are subtly separated from mundane ones.");
        game.getGui().displayMessage("Some items seem to be placed deliberately to hide others behind them.");
    }

    private void examineWindows(Game game) {
        game.getGui().displayMessage("You peer through the grimy windows.");
        game.getGui().displayMessage("The dirt seems intentionally placed to obscure the view.");
        game.getGui().displayMessage("You can just make out shadowy figures moving in the street outside.");
        game.getGui().displayMessage("This shop exists in a place between worlds...");
    }

    private void examineBasementDoor(Game game) {
        if (game.getStoryFlags().hasFlag("basement_unlocked")) {
            game.getGui().displayMessage("The heavy wooden door stands open, revealing stone steps descending into darkness.");
            game.getGui().displayMessage("The iron bands are purely decorative - the real security was the lock.");
            game.getGui().displayMessage("A cool breeze carries the scent of old stone and mysterious herbs from below.");
        } else {
            game.getGui().displayMessage("You examine the basement door closely.");
            game.getGui().displayMessage("The wood is thick and sturdy, reinforced with iron bands.");
            game.getGui().displayMessage("The large keyhole is ornately decorated with mystical symbols.");
            game.getGui().displayMessage("You can hear faint sounds from below - this basement is not empty.");
        }
    }

    // Override hasConnection to include basement
    @Override
    public boolean hasConnection(String direction) {
        String lowerDirection = direction.toLowerCase();

        // Check if trying to go to basement
        if (lowerDirection.equals("down") || lowerDirection.equals("basement") || lowerDirection.equals("downstairs")) {
            return true;
        }

        return super.hasConnection(direction);
    }

    // Override displayConnections to show basement option
    @Override
    public void displayConnections(Game game) {
        super.displayConnections(game); // Show normal connections first

        // Add basement connection info
        if (game.getStoryFlags().hasFlag("basement_unlocked")) {
            game.getGui().displayMessage("- down (to Shop Basement)");
        } else {
            game.getGui().displayMessage("- down (to basement - LOCKED, requires key)");
        }
    }

    // Method to check if player can access basement
    public boolean canAccessBasement(Game game) {
        if (game != null) {
            return game.getStoryFlags().hasFlag("basement_unlocked");
        }
        return false;
    }

    // Method to unlock basement (called when player uses key)
    public void unlockBasement(Game game) {
        if (!game.getStoryFlags().hasFlag("basement_unlocked")) {
            game.getStoryFlags().addFlag("basement_unlocked");
            game.getGui().displayMessage("You unlock the basement door with the key!");
            game.getGui().displayMessage("The heavy wooden door creaks open, revealing stone stairs descending into darkness.");
            game.getGui().displayMessage("You can now go 'down' to explore the basement.");
        }
    }

    // Get the basement room (for room manager or other systems)
    public ShopBasement getBasement() {
        return basement;
    }

    // FIXED: Updated attemptMove to properly handle basement access
    @Override
    public boolean attemptMove(String direction, Game game) {
        String lowerDirection = direction.toLowerCase();

        // Check if trying to go to basement
        if (lowerDirection.equals("down") || lowerDirection.equals("basement") || lowerDirection.equals("downstairs")) {
            if (!canAccessBasement(game)) {
                game.getGui().displayMessage("The basement door is locked. You need to find a way to unlock it first.");
                game.getGui().displayMessage("The heavy wooden door has a large keyhole - maybe you need a key?");
                return false; // Movement blocked
            }
        }

        return super.attemptMove(direction, game); // Call parent method for other directions
    }

    // FIXED: Override getConnectedRoom to handle basement properly
    @Override
    public Room getConnectedRoom(String direction) {
        String lowerDirection = direction.toLowerCase();

        // Check if trying to go to basement
        if (lowerDirection.equals("down") || lowerDirection.equals("basement") || lowerDirection.equals("downstairs")) {
            // Return basement room regardless of unlock status
            // The unlock check is handled in attemptMove/tryMove
            return basement;
        }

        // For all other directions, use the parent implementation
        return super.getConnectedRoom(direction);
    }

    // FIXED: Override tryMove to properly integrate with Room's movement system
    @Override
    public Room tryMove(String direction, Game game) {
        String lowerDirection = direction.toLowerCase();

        // Check if trying to go to basement
        if (lowerDirection.equals("down") || lowerDirection.equals("basement") || lowerDirection.equals("downstairs")) {
            // First check if movement is allowed
            if (!attemptMove(direction, game)) {
                return null; // Movement blocked
            }
            // If allowed, return the basement room
            return basement;
        }

        // For all other directions, use the parent implementation
        return super.tryMove(direction, game);
    }

    // All the existing shop methods remain the same
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
        if (StringUtils.isNullOrEmpty(itemName)) {
            return null;
        }

        String searchTerm = StringUtils.safeTrim(itemName).toLowerCase();

        for (ShopItem shopItem : shopInventory) {
            // Check if the item name matches using partial matching
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