import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Turn implements Comparable<Turn> {
    private final Set<String> tens = new HashSet<>(List.of("J", "j", "Q", "q", "K", "k"));
    private int index;
    private boolean[] faulty;
    private int timestamp;
    private int gameSessionID;
    private int playerID;
    private String action;
    private String dealerHand;
    private String playerHand;
    private int dealerHandValue;
    private int playerHandValue;
    public Turn(int index, boolean[] faulty, int timestamp, int gameSessionID, int playerID, String action, String dealerHand, String playerHand) {
        this.index = index;
        this.faulty = faulty;
        this.timestamp = timestamp;
        this.gameSessionID = gameSessionID;
        this.playerID = playerID;
        this.action = action;
        this.dealerHand = dealerHand;
        this.playerHand = playerHand;
        this.dealerHandValue = evaluateHeldCards(dealerHand, false);
        this.playerHandValue = evaluateHeldCards(playerHand, true);
    }

    private int evaluateHeldCards(String UnparsedCardsInHand, boolean player) { // Returns the value of held cards.
        // If the turn has a faulty tag, it was incorrectly formatted. Additionally, the hand itself must not have a faulty tag.
        if (!faulty[0] || (!faulty[5] && (!player) || (!faulty[6] && player))) return -1;
        String[] cardsInHand = UnparsedCardsInHand.split("-");
        int totalValue = 0;
        String cardValue;
        for (String card : cardsInHand) {
            // cards with length > 1 are either "" or "?" in witch case we skip them.
            if (card.length() > 1) cardValue = card.substring(0, card.length() - 1);
            else continue;
            // Otherwise we evaluate them.
            if (tens.contains(cardValue)) totalValue += 10;
            else if (cardValue.equals("A") || cardValue.equals("a")) totalValue += 11;
            else totalValue += Integer.parseInt(cardValue);
        }
        return totalValue;
    }

    public int getGameSessionID() {
        return gameSessionID;
    }
    public int getTimestamp() {
        return timestamp;
    }

    public boolean[] getFaulty() {
        return faulty;
    }

    public int getDealerHandValue() {
        return dealerHandValue;
    }

    public int getPlayerHandValue() {
        return playerHandValue;
    }

    public String getDealerHand() {
        return dealerHand;
    }

    public String getPlayerHand() {
        return playerHand;
    }

    public String getAction() {
        return action;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Turn{" +
                "index=" + index +
                ", timestamp=" + timestamp +
                ", gameSessionID=" + gameSessionID +
                ", playerID=" + playerID +
                ", action='" + action + '\'' +
                ", dealerHand='" + dealerHand + '\'' +
                ", playerHand='" + playerHand + '\'' +
                ", dealerHandValue=" + dealerHandValue +
                ", playerHandValue=" + playerHandValue +
                '}';
    }

    @Override
    public int compareTo(Turn o) {
        return Integer.compare(timestamp, o.getTimestamp());
    }
}
