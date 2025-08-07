package rpg.games.blackjack;

public class Card {
    public enum Suit {
        HEARTS("♥"), DIAMONDS("♦"), CLUBS("♣"), SPADES("♠");

        private final String symbol;

        Suit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public enum Rank {
        TWO(2, "2"), THREE(3, "3"), FOUR(4, "4"), FIVE(5, "5"), SIX(6, "6"),
        SEVEN(7, "7"), EIGHT(8, "8"), NINE(9, "9"), TEN(10, "10"),
        JACK(10, "J"), QUEEN(10, "Q"), KING(10, "K"), ACE(11, "A");

        private final int value;
        private final String display;

        Rank(int value, String display) {
            this.value = value;
            this.display = display;
        }

        public int getValue() {
            return value;
        }

        public String getDisplay() {
            return display;
        }

        public boolean isAce() {
            return this == ACE;
        }
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        return rank.getDisplay() + suit.getSymbol();
    }

    public String getDisplayName() {
        String rankName = switch (rank) {
            case ACE -> "Ace";
            case JACK -> "Jack";
            case QUEEN -> "Queen";
            case KING -> "King";
            default -> rank.getDisplay();
        };

        String suitName = switch (suit) {
            case HEARTS -> "Hearts";
            case DIAMONDS -> "Diamonds";
            case CLUBS -> "Clubs";
            case SPADES -> "Spades";
        };

        return rankName + " of " + suitName;
    }
}
