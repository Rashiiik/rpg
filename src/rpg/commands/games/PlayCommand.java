package rpg.commands.games;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.games.blackjack.BlackjackGame;
import rpg.utils.StringUtils;

public class PlayCommand implements Command, CommandParser.EnhancedCommand {
    private BlackjackGame blackjackGame;

    @Override
    public void execute(Game game, String[] args) {
        execute(game, args, args);
    }

    @Override
    public void execute(Game game, String[] originalArgs, String[] filteredArgs) {
        if (!game.getCurrentRoom().getName().contains("Inn")) {
            game.getGui().displayMessage("You need to be at the inn to play games.");
            return;
        }

        if (filteredArgs.length == 0) {
            showAvailableGames(game);
            return;
        }

        String gameType = filteredArgs[0].toLowerCase();

        switch (gameType) {
            case "blackjack", "bj", "21" -> handleBlackjack(game, filteredArgs);
            default -> {
                game.getGui().displayMessage("Unknown game: " + gameType);
                showAvailableGames(game);
            }
        }
    }

    private void showAvailableGames(Game game) {
        game.getGui().displayMessage("=== Available Games at the Inn ===");
        game.getGui().displayMessage("• Blackjack - Type 'play blackjack' or 'play bj'");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("The innkeeper looks at you expectantly. 'What'll it be, traveler?'");
    }

    private void handleBlackjack(Game game, String[] filteredArgs) {
        if (blackjackGame == null) {
            blackjackGame = new BlackjackGame(game);
        }

        String[] blackjackArgs = new String[Math.max(0, filteredArgs.length - 1)];
        if (filteredArgs.length > 1) {
            System.arraycopy(filteredArgs, 1, blackjackArgs, 0, blackjackArgs.length);
        }

        if (blackjackArgs.length == 0) {
            startOrShowBlackjackGame(game);
            return;
        }

        String action = blackjackArgs[0].toLowerCase();

        switch (action) {
            case "bet" -> handleBlackjackBet(game, blackjackArgs);
            case "hit" -> handleBlackjackHit(game);
            case "stand" -> handleBlackjackStand(game);
            case "quit", "leave", "exit" -> handleBlackjackQuit(game);
            case "rules" -> showBlackjackRules(game);
            default -> {
                if (StringUtils.isNumber(action)) {
                    handleBlackjackBet(game, blackjackArgs);
                } else {
                    game.getGui().displayMessage("Unknown blackjack command. Type 'play blackjack rules' for help.");
                }
            }
        }
    }

    private void startOrShowBlackjackGame(Game game) {
        switch (blackjackGame.getState()) {
            case WAITING_FOR_BET -> {
                game.getGui().displayMessage("Welcome to the blackjack table!");
                game.getGui().displayMessage("The innkeeper shuffles a fresh deck of cards.");
                game.getGui().displayMessage("'Place your bet, traveler! Minimum 1 coin.'");
                game.getGui().displayMessage("");
                game.getGui().displayMessage("Type 'play blackjack bet [amount]' to place a bet, or 'play blackjack rules' for game rules.");
            }
            case PLAYER_TURN -> {
                game.getGui().displayMessage("=== Current Blackjack Game ===");
                game.getGui().displayMessage("Your hand: " + blackjackGame.getPlayerHand().toString());
                game.getGui().displayMessage("Dealer's hand: " + blackjackGame.getDealerHand().getDisplayString(true));
                game.getGui().displayMessage("Current bet: " + blackjackGame.getCurrentBet() + " coins");
                game.getGui().displayMessage("");
                game.getGui().displayMessage("Your turn! Type 'hit' or 'stand'.");
            }
            case DEALER_TURN, DEALING -> {
                game.getGui().displayMessage("Dealer is playing...");
            }
            case GAME_OVER -> {
                game.getGui().displayMessage("Game finished. Type 'play blackjack bet [amount]' to play again.");
            }
        }
    }

    private void handleBlackjackBet(Game game, String[] args) {
        if (blackjackGame.getState() != BlackjackGame.GameState.WAITING_FOR_BET) {
            game.getGui().displayMessage("You can't place a bet right now. Finish the current hand first.");
            return;
        }

        int betAmount = 0;

        if (args.length >= 2 && StringUtils.isNumber(args[1])) {
            betAmount = Integer.parseInt(args[1]);
        } else if (args.length >= 1 && StringUtils.isNumber(args[0])) {
            betAmount = Integer.parseInt(args[0]);
        } else {
            game.getGui().displayMessage("Please specify a bet amount. Example: 'play blackjack bet 10'");
            return;
        }

        if (betAmount < 1) {
            game.getGui().displayMessage("Minimum bet is 1 coin.");
            return;
        }

        game.getGui().displayMessage("You place a bet of " + betAmount + " coins on the table.");
        game.getGui().displayMessage("The innkeeper nods and begins dealing...");
        game.getGui().displayMessage("");

        blackjackGame.placeBet(betAmount);
    }

    private void handleBlackjackHit(Game game) {
        if (!blackjackGame.hit()) {
            game.getGui().displayMessage("You can't hit right now.");
        }
    }

    private void handleBlackjackStand(Game game) {
        if (!blackjackGame.stand()) {
            game.getGui().displayMessage("You can't stand right now.");
        }
    }

    private void handleBlackjackQuit(Game game) {
        if (blackjackGame.getState() == BlackjackGame.GameState.PLAYER_TURN ||
                blackjackGame.getState() == BlackjackGame.GameState.DEALER_TURN) {
            game.getGui().displayMessage("You can't quit in the middle of a hand!");
            return;
        }

        game.getGui().displayMessage("You step away from the blackjack table.");
        game.getGui().displayMessage("The innkeeper waves farewell. 'Come back anytime!'");
        blackjackGame = null;
    }

    private void showBlackjackRules(Game game) {
        game.getGui().displayMessage("=== Blackjack Rules ===");
        game.getGui().displayMessage("• Get as close to 21 as possible without going over");
        game.getGui().displayMessage("• Face cards (J, Q, K) are worth 10 points");
        game.getGui().displayMessage("• Aces are worth 11 or 1 (whichever is better)");
        game.getGui().displayMessage("• Dealer hits on 16, stands on 17");
        game.getGui().displayMessage("• Blackjack (21 with 2 cards) pays 1.5x your bet");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("Commands:");
        game.getGui().displayMessage("• 'play blackjack bet [amount]' - Place a bet");
        game.getGui().displayMessage("• 'hit' - Draw another card");
        game.getGui().displayMessage("• 'stand' - Keep your current hand");
        game.getGui().displayMessage("• 'play blackjack quit' - Leave the table");
    }

    @Override
    public String[] getAliases() {
        return new String[]{"game", "games"};
    }

    @Override
    public String getHelpText() {
        return "Play games at the inn (blackjack, poker, roulette, etc.)";
    }
}