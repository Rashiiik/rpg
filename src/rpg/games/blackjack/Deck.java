package rpg.games.blackjack;

import java.util.*;

public class Deck {
    private List<Card> cards;
    private Random random;

    public Deck() {
        this.random = new Random();
        initializeDeck();
        shuffle();
    }

    private void initializeDeck() {
        cards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            initializeDeck();
            shuffle();
        }
        return cards.remove(cards.size() - 1);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}