package nl.tue.s2id90.group19;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org.junit.Test;
import static org.junit.Assert.*;
import org10x10.dam.game.Move;

/**
 *
 * @author daan
 */
public class MyDraughtsPlayerTest {
    
    /** Scores */
    int winScore = 4;
    int drawWinScore = 0;
    
    /** Time limit per move */
    int timelimit = 2;
    
    /** Search depth for all players */
    int searchDepth = 4;
    
    int generation = 0;
    int generationSize = 20;
    
    boolean generateNew = true;
    String readFileName = "machineLearning.txt";
    String outputFileName = "generation__";
    
    ArrayList<MyDraughtsPlayer> Players = new ArrayList<>();
    
    
    
    
    
    
    
    public MyDraughtsPlayerTest() {
    }
    
    
    private int simulateGame(DraughtsPlayer white, DraughtsPlayer black) {
            DraughtsState state = new DraughtsState();
            int captureTimer = 0;
            while(!state.isEndState()) {
                DraughtsPlayer player = state.isWhiteToMove()? white : black;
                Move bestMove = player.getMove(state);
                if (bestMove.isCapture() || bestMove.isPieceMove()) {
                    captureTimer = 0;
                } else {
                    captureTimer++;
                }
                if (captureTimer>=20) {
                    //only kings moved around, no captures for 20 moves: draw
                    break;
                }
                state.doMove(bestMove);    
                
            }
            
            return evaluate(state);
    }
    
    //Plays game where player1 plays vs player2
    private void playGame(MyDraughtsPlayer Player1, MyDraughtsPlayer Player2) {
        int result1 = simulateGame(Player1, Player2);
        switch (result1) {
        case Integer.MAX_VALUE:
            //White wins:
            Player1.fitness += winScore;
            Player2.fitness -= winScore;
            break;
        case Integer.MIN_VALUE:
            //Black wins:
            Player1.fitness -= winScore;
            Player2.fitness += winScore;
            break;
        default:
            //draw
            if (result1 > 0) {
                Player1.fitness += drawWinScore;
                Player2.fitness -= drawWinScore;
            } else if (result1 < 0) {
                Player1.fitness -= drawWinScore;
                Player2.fitness += drawWinScore;
            }
            break;          
        }
    }
    

    //@Test
    public void testBeatsExtra() {
        DraughtsPlayer white = new MyDraughtsPlayer(searchDepth, 20, 2000, 1, 0, 0);
        DraughtsPlayer black = new MyDraughtsPlayer(searchDepth, 20, 2000, 1, 0, 0);
        int resultGame = simulateGame(white, black);
        System.out.println(resultGame);
        assertTrue(resultGame > 0);
    }
    
    @Test
    public void machineLearning() {
        //Create random object
        Random random = new Random();
        //Generate our first generation
        if (generateNew) {
            try {
                //Write initial generation to file aswell
                File file = new File(outputFileName + String.valueOf(generation) + ".txt");
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                MyDraughtsPlayer firstPlayer = new MyDraughtsPlayer(searchDepth, 20, 2000, 1, 0, 0);
                Players.add(firstPlayer);
                for (int i = 1; i < generationSize; i++) {
                    //Generate random values for everything
                    MyDraughtsPlayer newPlayer = new MyDraughtsPlayer(searchDepth, random.nextInt(50), 500 + random.nextInt(50000), random.nextInt(6), 0, 0);
                    newPlayer.generation = 0;
                    Players.add(newPlayer);
                    writePlayerToFile(newPlayer, bufferedWriter);
                }

                bufferedWriter.flush();
                bufferedWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        } else {
            try {
                File file = new File(readFileName);
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String firstLine;
                while ((firstLine = bufferedReader.readLine()) != null) {
                    Players.add(readPlayerFromFile(bufferedReader, firstLine));
                }
                assert !Players.isEmpty();
                generation = Players.get(0).generation;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        while (generation < 100) {
            


            //got list of players: now make them play eachother
            for (int i = 0; i < Players.size(); i++) {
                for (int j = 0; j < Players.size(); j++) {
                    if (i != j) {
                        MyDraughtsPlayer Player1 = Players.get(i);
                        MyDraughtsPlayer Player2 = Players.get(j);
                        playGame(Player1, Player2);
                        System.out.println("GAME "+i+" VS "+j+" DONE");
                    }
                }
            }

            //done playing eachother: cut off bottom half
            //sort them by fitness first
            Collections.sort(Players, new Comparator<MyDraughtsPlayer>() {
                @Override
                public int compare(MyDraughtsPlayer p1, MyDraughtsPlayer p2) {
                    return p2.fitness - p1.fitness; // Descending
                }
            });
            int amountToRemove = Players.size() / 2;
            for (int i = 0; i < amountToRemove; i++) {
                System.out.println(Players.get(Players.size() - 1).fitness); //test, should be ascending
                Players.remove(Players.size() - 1); //remove last element repeatedly until done
            }

            //Now add new candidates to play against: Make them random deviations of already exisiting ones 
            int amountOfNewPlayers = generationSize - Players.size();
            for (int i = 0; i < amountOfNewPlayers; i++) {
                MyDraughtsPlayer oldPlayer = Players.get(i);
                MyDraughtsPlayer newPlayer = new MyDraughtsPlayer(searchDepth, oldPlayer.scoreValue + (20 - random.nextInt(41)), oldPlayer.winValue + (500 - random.nextInt(1001)), oldPlayer.tempiValue + (1 - random.nextInt(3)), 0, 0);
                Players.add(newPlayer);
            }

            generation++;
            
            //write everything to file at the end of a generation; the new generation:
            File file = new File(outputFileName+String.valueOf(generation)+".txt");
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(file);
                try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                    //Increase all generations, reset fitness after writing the current fitness to file
                    for (MyDraughtsPlayer p : Players) {
                        p.generation = generation;
                        writePlayerToFile(p, bufferedWriter);
                        p.fitness = 0;
                    }
                    bufferedWriter.flush();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        
        
    }
    
    
    
    //@Test
    public void testWriteRead() {
        MyDraughtsPlayer player1 = new MyDraughtsPlayer(searchDepth, 20, 2000, 1, 4, -2);

        try {
            File file = new File("generationtest.txt");
            FileWriter fileWriter = new FileWriter(file);
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                writePlayerToFile(player1, bufferedWriter);
                
                bufferedWriter.flush();
            }
            
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String firstLine = bufferedReader.readLine();
            MyDraughtsPlayer player2 = readPlayerFromFile(bufferedReader, firstLine);
            bufferedReader.close();
            
            
            player2.fitness = 5;
            
            File file2 = new File("generationtest2.txt");
            FileWriter fileWriter2 = new FileWriter(file2);
            BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
            writePlayerToFile(player1, bufferedWriter2);
            writePlayerToFile(player2, bufferedWriter2);
            bufferedWriter2.flush();
            bufferedWriter2.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    
    }
    
    
    
    public void writePlayerToFile(MyDraughtsPlayer player, BufferedWriter writer) {
        //Write all relevant data about a player to a file
        try {
            writer.write("PlayerStart");
            writer.newLine();
            writer.write(String.valueOf(player.generation));
            writer.newLine();
            writer.write(String.valueOf(player.fitness));
            writer.newLine();
            writer.write(String.valueOf(player.scoreValue));
            writer.newLine();
            writer.write(String.valueOf(player.winValue));
            writer.newLine();
            writer.write(String.valueOf(player.tempiValue));
            writer.newLine();
            writer.write(String.valueOf(player.columnValue));
            writer.newLine();
            writer.write(String.valueOf(player.splitValue));
            writer.newLine();
            writer.write("PlayerEnd");
            writer.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public MyDraughtsPlayer readPlayerFromFile(BufferedReader reader, String firstLine) throws Exception { 
        firstLine = reader.readLine();
        if (!firstLine.equals("PlayerStart")) {
            throw new Exception("Reader is not in correct position in file");
        }
        int generation = Integer.parseInt(reader.readLine());
        int fitness = Integer.parseInt(reader.readLine());
        int scoreValue = Integer.parseInt(reader.readLine());
        int winValue = Integer.parseInt(reader.readLine());
        int tempiValue = Integer.parseInt(reader.readLine());
        int columnValue = Integer.parseInt(reader.readLine());
        int splitValue = Integer.parseInt(reader.readLine());
        String endLine = reader.readLine();
        if (!endLine.equals("PlayerEnd")) {
            throw new Exception("End of player not in proper place in file");
        }
        MyDraughtsPlayer scannedPlayer = new MyDraughtsPlayer(searchDepth, scoreValue, winValue, tempiValue, columnValue, splitValue);
        scannedPlayer.fitness = fitness;
        scannedPlayer.generation = generation;
        return scannedPlayer;
    }
    
    
    /** Simple evaluate function that counts pieces and counts king as 3 */
    public static int evaluate(DraughtsState state) {
        int whites = 0;
        int blacks = 0;
        int whiteKings = 0;
        int blackKings = 0;
        int[] pieces = state.getPieces();
 
        for(int i = 0; i < pieces.length; i++) {
            int piece = pieces[i];
            switch(piece) {
                case DraughtsState.WHITEPIECE:
                    whites++;
                    break;
                case DraughtsState.BLACKPIECE:
                    blacks++;
                    break;
                case DraughtsState.WHITEKING:
                    whiteKings++;
                    break;
                case DraughtsState.BLACKKING:
                    blackKings++;
                    break;
            }
        }
        int score;
        score = whites - blacks + 3*(whiteKings - blackKings);
        
        if (blacks+blackKings == 0) {
            score = Integer.MAX_VALUE;
        }
        if (whites+whiteKings == 0) {
            score = Integer.MIN_VALUE;
        }
        return score;
    }
}
