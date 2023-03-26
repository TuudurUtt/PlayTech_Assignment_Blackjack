import java.util.*;

public class Session {
    public int sessionID;
    private List<Turn> turns;
    private Turn firstFaultyTurn;
    public Session(int sessionID, Turn turn) {
        this.sessionID = sessionID;
        this.turns = new ArrayList<>();
        addTurn(turn);
    }

    public void addTurn(Turn turn) {
        this.turns.add(turn);
    }

    private void sortTurns(){
        Collections.sort(turns);
    }

    public void isolateFirstFaultyTurn() {
        sortTurns(); // We sort turns by their timestamp.
        String[] dealerCards;
        String[] playerCards;
        int playerCardsNumberLastTurn = 0;
        int dealerCardsNumberLastTurn = 0;
        String lastAction = null;
        boolean dealerHasShown = false;
        for (Turn turn : turns) { // check is we already know that a turn is faulty.
            boolean[] knownFaults = turn.getFaulty();
            for (boolean correct : knownFaults) {
                if (!correct) {
                    this.firstFaultyTurn = turn;
                    return;
                }
            }
            dealerCards = turn.getDealerHand().split("-");
            playerCards = turn.getPlayerHand().split("-");
            if (!dealerHasShown) dealerHasShown = Objects.equals(turn.getAction(), "D Show");
            else dealerHasShown = !Objects.equals(turn.getAction(), "D Redeal");
            // If the player joins or the dealer redeals, they must have two cards in hand.
            if ((Objects.equals(turn.getAction(), "P Joined") || Objects.equals(lastAction, "D Redeal")) &&
                    (dealerCards.length != 2 || playerCards.length != 2)) {
                this.firstFaultyTurn = turn;
                return;
            }
            // The amount of player cards should only change if the player has hit or left the game or the dealer has redealt.
            if (!Objects.equals(turn.getAction(), "P Joined") && !Objects.equals(turn.getAction(), "P Left") &&
                    !Objects.equals(turn.getAction(), "D Redeal") && !Objects.equals(lastAction, "P Hit") &&
                    playerCardsNumberLastTurn != playerCards.length) {
                this.firstFaultyTurn = turn;
                return;
            }
            // The amount of dealer cards should only change if the dealer has hit, redealt or player has left the game.
            if (!Objects.equals(turn.getAction(), "P Joined") && !Objects.equals(turn.getAction(), "P Left") &&
                    !Objects.equals(turn.getAction(), "D Redeal") && !Objects.equals(lastAction, "D Hit") &&
                    dealerCardsNumberLastTurn != playerCards.length) {
                this.firstFaultyTurn = turn;
                return;
            }
            // If a player hits, they should get exactly one additional card next turn.
            if (Objects.equals(lastAction, "P Hit") && playerCards.length != playerCardsNumberLastTurn + 1) {
                this.firstFaultyTurn = turn;
                return;
            }
            // If a dealer hits, they should get exactly one additional card next turn.
            if (Objects.equals(lastAction, "D Hit") && playerCards.length != dealerCardsNumberLastTurn + 1) {
                this.firstFaultyTurn = turn;
                return;
            }
            // If a player wins, the dealer must have gone bust, or they must have a greater hand value. Additionally, a comparison can only happen if the dealer has shown.
            if (Objects.equals(turn.getAction(), "P Win") && turn.getDealerHandValue() <= 21 &&
                    (turn.getDealerHandValue() > turn.getPlayerHandValue() || !Objects.equals(lastAction, "D Show"))) {
                this.firstFaultyTurn = turn;
                return;
            }
            // If a player loses, the player must have gone bust, or they must have a lesser hand value. Additionally, a comparison can only happen if the dealer has shown.
            if (Objects.equals(turn.getAction(), "P Lose") && turn.getPlayerHandValue() <= 21 &&
                    (turn.getDealerHandValue() <= turn.getPlayerHandValue() || !Objects.equals(lastAction, "D Show"))) {
                this.firstFaultyTurn = turn;
                return;
            }
            // The player should only be able to hit or stand if the last turn was either "P Joined", "P Hit" or "D Redeal".
            if ((Objects.equals(turn.getAction(), "P Hit")) || Objects.equals(turn.getAction(), "P Stand") &&
                    !Objects.equals(lastAction, "P Joined") && !Objects.equals(lastAction, "P Hit")
                    && !Objects.equals(lastAction, "D Redeal")) {
                this.firstFaultyTurn = turn;
                return;
            }
            // The dealer should only be able to hit if the last turn was either "P SStand" or "D Hit". Additionally, they can only hit with a hand value less than 17.
            if (Objects.equals(turn.getAction(), "D Hit") && (!Objects.equals(lastAction, "P Stand") &&
                    (!Objects.equals(lastAction, "D Hit")) || turn.getDealerHandValue() >= 17)) {
                this.firstFaultyTurn = turn;
                return;
            }
            // The dealer cannot show if they have a hand value of less than 17.
            if (turn.getDealerHandValue() <= 17 && Objects.equals(turn.getAction(), "D Show")) {
                this.firstFaultyTurn = turn;
                return;
            }
            // The player should only be able to leave after winning or losing. Same with the dealer redealing.
            if ((Objects.equals(turn.getAction(), "P Left") || Objects.equals(turn.getAction(), "D Redeal")) &&
                    !Objects.equals(lastAction, "P Win") && !Objects.equals(lastAction, "P Lose")) {
                this.firstFaultyTurn = turn;
                return;
            }
            // check for duplicates in both hands.
            if (checkForDuplicatesInHand(playerCards, dealerCards, turn)) return;
            if (checkForDuplicatesInHand(dealerCards, playerCards, turn)) return;

            if (turn.getPlayerHandValue() > 21 && !Objects.equals(turn.getAction(), "P Lose")) { // Check if the player should have gone bust.
                this.firstFaultyTurn = turn;
                return;
            }
            // If the player leaves, hands should show empty.
            if (Objects.equals(turn.getAction(), "P Left") && (dealerCards.length != 0 || playerCards.length != 0)) {
                this.firstFaultyTurn = turn;
                return;
            }
            // In the dealer has not shown their hand, the second card int their hand should be a "?".
            if (!dealerHasShown && !turn.getDealerHand().endsWith("?")) {
                this.firstFaultyTurn = turn;
                return;
            }
            lastAction = turn.getAction();
            playerCardsNumberLastTurn = playerCards.length;
            dealerCardsNumberLastTurn = dealerCards.length;
        }
    }

    private boolean checkForDuplicatesInHand(String[] cardsInOtherHand, String[] cardsInHand, Turn turn) {
        for (int i = 0; i < cardsInHand.length; i++) {
            if (Arrays.asList(cardsInOtherHand).contains(cardsInHand[i])) {
                this.firstFaultyTurn = turn;
                return true;
            }
            for (int j = 0; j < cardsInHand.length; j++) {
                if (Objects.equals(cardsInHand[j], cardsInHand[i]) && i != j) {
                    this.firstFaultyTurn = turn;
                    return true;
                }
            }
        }
        return false;
    }

    public Turn getFirstFaultyTurn() {
        return firstFaultyTurn;
    }

    public int getSessionID() {
        return sessionID;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionID=" + sessionID +
                ", turns=" + turns +
                '}';
    }
}
