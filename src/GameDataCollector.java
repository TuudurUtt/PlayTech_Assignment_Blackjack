import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameDataCollector {
    private final Set<String> legalActions = new HashSet<>(List.of("P Joined", "P Hit", "P Stand", "P Win", "P Lose",
                                                     "P Left", "D Show", "D Hit", "D Redeal"));
    private final Set<String> legalCards = new HashSet<>(List.of("?", "2H", "2D", "2C", "2S", "3H", "3D", "3C", "3S",
                                            "4H", "4D", "4C", "4S", "5H", "5D", "5C", "5S", "6H", "6D", "6C", "6S",
                                            "7H", "7D", "7C", "7S", "8H", "8D", "8C", "8S", "9H", "9D", "9C", "9S",
                                            "10H", "10D", "10C", "10S", "JH", "JD", "JC", "JS", "jH", "jD", "jC", "jS",
                                            "QH", "QD", "QC", "QS", "qH", "qD", "qC", "qS", "KH", "KD", "KC", "KS",
                                            "kH", "kD", "kC", "kS", "AH", "AD", "AC", "AS", "aH", "aD", "aC", "aS"));

    // This is the main method of this class and is used to create a GameData object witch stores all relevant information, organized into sessions.
    public GameData createGameData(String[] unparsedLines) throws IOException {
        String[] turnData;
        GameData gameData = new GameData();

        int timestamp;
        int gameSessionID;
        int playerID;
        String action;
        String dealerHand;
        String playerHand;

        for (int i = 0; i < unparsedLines.length; i++) {
            // The following array stores if each element of the turn was correct or not (true = correct)
            boolean[] turnDataCorrectness = faultyInputChecker(unparsedLines[i]);
            // If the turn did not have the correct amount of elements, we create an easily identifiable "dummy" turn.
            if (turnDataCorrectness[0]) turnData = unparsedLines[i].split(",");
            else {
                gameData.organizeTurn(new Turn(i, turnDataCorrectness, -1, -1, -1, "-1", "-1", "-1"));
                continue;
            }
            if (turnDataCorrectness[1]) timestamp = Integer.parseInt(turnData[0]);
            else timestamp = -1;
            if (turnDataCorrectness[2]) gameSessionID = Integer.parseInt(turnData[1]);
            else gameSessionID = -1;
            if (turnDataCorrectness[3]) playerID = Integer.parseInt(turnData[2]);
            else playerID = -1;
            if (turnDataCorrectness[4]) action = turnData[3];
            else action = "-1";
            if (turnDataCorrectness[5]) dealerHand = turnData[4];
            else dealerHand = "-1";
            if (turnDataCorrectness[6]) playerHand = turnData[5];
            else playerHand = "-1";

            gameData.organizeTurn(new Turn(i, turnDataCorrectness, timestamp, gameSessionID, playerID, action, dealerHand, playerHand));
        }
        return gameData;
    }
    private int getNumberOfLinesFromFile(String fileName) throws IOException {
        int numberOfLines = 0;
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        while (reader.readLine() != null) numberOfLines++;
        reader.close();
        return  numberOfLines;
    }

    public String[] readLinesFromFile(String fileName) throws IOException {
        int numberOfLines = getNumberOfLinesFromFile(fileName);
        String[] gameData = new String[numberOfLines];
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        for (int i = 0; i < numberOfLines; i++) {
            gameData[i] = reader.readLine();
        }
        reader.close();
        return gameData;
    }

    // Returns a boolean array, with each element representing if a field is correctly formatted or not.
    private boolean[] faultyInputChecker(String unparsedTurnData) {
        boolean[] output = new boolean[7];

        // We check that all fields are present. If not, we return the output right away due to following fields potentially not existing.
        if (unparsedTurnData.split(",").length == 6) output[0] = true;
        else return output;
        String[] turnData = unparsedTurnData.split(",");

        // We check if the Timestamp, Game Session ID and PlayerID are numeric.
        if (isNumeric(turnData[0])) output[1] = true;
        if (isNumeric(turnData[1])) output[2] = true;
        if (isNumeric(turnData[2])) output[3] = true;

        if (legalActions.contains(turnData[3])) output[4] = true; // We check if the action is correctly formatted.

        // We check if the cards both hands are correctly formatted.
        if (checkValidityOfHeldCards(turnData[4], false )) output[5] = true;
        if (checkValidityOfHeldCards(turnData[5], true)) output[6] = true;

        return output;
    }

    private boolean checkValidityOfHeldCards(String unparsedCardsInHand, boolean player) { // returns true if a hand contains only legal cards.
        if (unparsedCardsInHand.equals("-")) return true; // split does not add empty element to the created array.
        String[] cardsInHand = unparsedCardsInHand.split("-");
        if (cardsInHand.length == 1) return false; // there can never be 1 card in any hand.
        boolean output = false;
        for (String card : cardsInHand) {
            output = legalCards.contains(card);
             if (player && card.equals("?")) { // a player can never have an unknown card in hand.
                output = false;
                break;
            }
        }
        return output;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException exception) {return false;}
    }
}
