package rpg.games.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand {
    private final List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void clear() {
        cards.clear();
    }

    public int getValue() {
        int value = 0;
        int aceCount = 0;
        for (Card card : cards) {
            value += card.getValue();
            if (card.getRank() == Card.Rank.ACE) {
                aceCount++;
            }
        }
        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }
        return value;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    public boolean isBust() {
        return getValue() > 21;
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card.getDisplayName()).append(" ");
        }
        sb.append("(").append(getValue()).append(")");
        return sb.toString();
    }

    public String getDisplayString(boolean hideFirstCard) {
        if (hideFirstCard && cards.size() > 1) {
            return "[Hidden Card] " + cards.get(1).getDisplayName();
        }
        return toString();
    }
}