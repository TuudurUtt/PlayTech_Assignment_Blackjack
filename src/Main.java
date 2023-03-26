import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file name: ");
        String fileName = scanner.nextLine();
        GameDataCollector gameDataCollector = new GameDataCollector();
        String[] unparsedLines = gameDataCollector.readLinesFromFile(fileName);
        GameData gameData = gameDataCollector.createGameData(unparsedLines);
        for (Session session : gameData.getSessions()) session.isolateFirstFaultyTurn();
        File output = new File("analyzer_output.txt");
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(output));
        for (Session session : gameData.getSessions()) {
            int index = session.getFirstFaultyTurn().getIndex();
            fileWriter.write(unparsedLines[index]);
            fileWriter.newLine();
        }
        fileWriter.close();
    }
}