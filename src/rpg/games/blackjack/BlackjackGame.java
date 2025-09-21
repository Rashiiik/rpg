package rpg.games.blackjack;

import rpg.core.Game;
import rpg.player.Player;

public class BlackjackGame {
    public enum GameState {
        WAITING_FOR_BET,
        DEALING,
        PLAYER_TURN,
        DEALER_TURN,
        GAME_OVER
    }

    public enum GameResult {
        PLAYER_WIN,
        DEALER_WIN,
        PUSH,
        PLAYER_BLACKJACK,
    }

    private Deck deck;
    private Hand playerHand;
    private Hand dealerHand;
    private GameState state;
    private int currentBet;
    private Game rpgGame;
    private Player player;

    public BlackjackGame(Game rpgGame) {
        this.rpgGame = rpgGame;
        this.player = rpgGame.getPlayer(); // Get player reference from rpgGame
        this.deck = new Deck();
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.state = GameState.WAITING_FOR_BET;
        this.currentBet = 0;
    }

    public boolean placeBet(int amount) {
        if (state != GameState.WAITING_FOR_BET) {
            return false;
        }

        if (player.getGold() < amount) {
            rpgGame.getGui().displayMessage("You don't have enough gold! You have " + player.getGold() + " gold.");
            return false;
        }

        if (!player.removeGold(amount)) {
            rpgGame.getGui().displayMessage("Failed to place bet.");
            return false;
        }

        this.currentBet = amount;
        rpgGame.getGui().displayMessage("Bet placed: " + amount + " gold. Remaining gold: " + player.getGold());
        dealInitialCards();
        return true;
    }

    private void dealInitialCards() {
        playerHand.clear();
        dealerHand.clear();

        playerHand.addCard(deck.dealCard());
        dealerHand.addCard(deck.dealCard());
        playerHand.addCard(deck.dealCard());
        dealerHand.addCard(deck.dealCard());

        state = GameState.DEALING;

        rpgGame.getGui().displayMessage("=== Cards Dealt ===");
        rpgGame.getGui().displayMessage("Your hand: " + playerHand.toString());
        rpgGame.getGui().displayMessage("Dealer's hand: " + dealerHand.getDisplayString(true));

        if (playerHand.isBlackjack()) {
            if (dealerHand.isBlackjack()) {
                endGame(GameResult.PUSH);
            } else {
                endGame(GameResult.PLAYER_BLACKJACK);
            }
        } else if (dealerHand.isBlackjack()) {
            rpgGame.getGui().displayMessage("Dealer reveals: " + dealerHand.toString());
            rpgGame.getGui().displayMessage("Dealer has blackjack!");
            endGame(GameResult.DEALER_WIN);
        } else {
            state = GameState.PLAYER_TURN;
            rpgGame.getGui().displayMessage("");
            rpgGame.getGui().displayMessage("Your turn! Type 'hit' to draw a card or 'stand' to stay.");
        }
    }

    public boolean hit() {
        if (state != GameState.PLAYER_TURN) {
            return false;
        }

        Card card = deck.dealCard();
        playerHand.addCard(card);

        rpgGame.getGui().displayMessage("You drew: " + card.getDisplayName());
        rpgGame.getGui().displayMessage("Your hand: " + playerHand.toString());

        if (playerHand.isBust()) {
            rpgGame.getGui().displayMessage("Bust! You went over 21.");
            endGame(GameResult.DEALER_WIN);
        } else if (playerHand.getValue() == 21) {
            rpgGame.getGui().displayMessage("21! Automatically standing.");
            stand();
        }

        return true;
    }

    public boolean stand() {
        if (state != GameState.PLAYER_TURN) {
            return false;
        }

        rpgGame.getGui().displayMessage("You stand with " + playerHand.getValue());
        state = GameState.DEALER_TURN;
        playDealerTurn();
        return true;
    }

    private void playDealerTurn() {
        rpgGame.getGui().displayMessage("");
        rpgGame.getGui().displayMessage("Dealer reveals hidden card...");
        rpgGame.getGui().displayMessage("Dealer's hand: " + dealerHand.toString());

        while (dealerHand.getValue() < 17) {
            Card card = deck.dealCard();
            dealerHand.addCard(card);
            rpgGame.getGui().displayMessage("Dealer draws: " + card.getDisplayName());
            rpgGame.getGui().displayMessage("Dealer's hand: " + dealerHand.toString());
        }

        if (dealerHand.isBust()) {
            rpgGame.getGui().displayMessage("Dealer busts!");
            endGame(GameResult.PLAYER_WIN);
        } else {
            int playerValue = playerHand.getValue();
            int dealerValue = dealerHand.getValue();

            if (playerValue > dealerValue) {
                endGame(GameResult.PLAYER_WIN);
            } else if (dealerValue > playerValue) {
                endGame(GameResult.DEALER_WIN);
            } else {
                endGame(GameResult.PUSH);
            }
        }
    }

    private void endGame(GameResult result) {
        state = GameState.GAME_OVER;

        rpgGame.getGui().displayMessage("");
        rpgGame.getGui().displayMessage("=== Game Over ===");

        switch (result) {
            case PLAYER_WIN -> {
                rpgGame.getGui().displayMessage("You win! (" + playerHand.getValue() + " vs " + dealerHand.getValue() + ")");
                int payout = currentBet * 2; // Win back 2x the bet
                rpgGame.getGui().displayMessage("You won " + payout + " gold!");
                player.addGold(payout);
                rpgGame.getGui().displayMessage("Your gold: " + player.getGold());
            }
            case PLAYER_BLACKJACK -> {
                int payout = (int) (currentBet * 2.5); // Blackjack pays 2.5x (bet back + 1.5x bonus)
                rpgGame.getGui().displayMessage("Blackjack! You win!");
                rpgGame.getGui().displayMessage("You won " + payout + " gold!");
                player.addGold(payout);
                rpgGame.getGui().displayMessage("Your gold: " + player.getGold());
            }
            case DEALER_WIN -> {
                rpgGame.getGui().displayMessage("Dealer wins. (" + dealerHand.getValue() + " vs " + playerHand.getValue() + ")");
                rpgGame.getGui().displayMessage("You lost " + currentBet + " gold.");
                rpgGame.getGui().displayMessage("Your gold: " + player.getGold());
            }
            case PUSH -> {
                rpgGame.getGui().displayMessage("Push! It's a tie. (" + playerHand.getValue() + " vs " + dealerHand.getValue() + ")");
                rpgGame.getGui().displayMessage("Your bet is returned.");
                player.addGold(currentBet); // Return the original bet
                rpgGame.getGui().displayMessage("Your gold: " + player.getGold());
            }
        }

        rpgGame.getGui().displayMessage("");
        rpgGame.getGui().displayMessage("Type 'bet [amount]' to play again or 'quit blackjack' to leave the table.");

        state = GameState.WAITING_FOR_BET;
        currentBet = 0;
    }

    public GameState getState() { return state; }
    public Hand getPlayerHand() { return playerHand; }
    public Hand getDealerHand() { return dealerHand; }
    public int getCurrentBet() { return currentBet; }

    public void reset() {
        playerHand.clear();
        dealerHand.clear();
        state = GameState.WAITING_FOR_BET;
        currentBet = 0;
        deck = new Deck(); // Fresh deck
    }
}