package nl.tue.s2id90.group19;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org.junit.Test;
import static org.junit.Assert.*;
import org10x10.dam.game.BoardState;
import org10x10.dam.game.Move;

/**
 *
 * @author daan
 */
public class MyDraughtsPlayerTest {
    
    /** Time limit per move */
    int timelimit = 2;
    
    /** Search depth for all players */
    int searchDepth = 5;
    
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
    

    //@Test
    public void testBeatsExtra() {
        DraughtsPlayer white = new MyDraughtsPlayer(5);
        DraughtsPlayer black = new MyDraughtsPlayer(5);
        int resultGame = simulateGame(white, black);
        assertTrue(resultGame > 0);
    }
    
    //@Test
    public void machineLearning() {
        //Generate our first generation
        
        //Or read from file if specified
        
        
        //Make them play against eachother to get better
        
        //After a round is done, write everything to file (to make sure the tests
        //can be stopped safely
        
        //After all matches player: start over with the next generation
        
    }
    
    //for generating patterns more easily
    /* 
     * Simple method to convert a state into a string: useful for more 
     * efficiently copying a state and searching for patterns
     * Format: String with length 50. Every 5 characters is 1 row.
     * Meaning of characters:
     * 0 = empty
     * 1 = white piece
     * 2 = black piece
     * 3 = white king
     * 4 = black king
     * 9 = DO NOT CARE
     */
    
    @Test
    public void testWriteRead() {
        MyDraughtsPlayer player1 = new MyDraughtsPlayer(searchDepth, 20, 2000, 1, new int[][]{{3,0,0,0,3,0,0,0,0,0,0,3,4,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{3,0,0,0,3,0,0,0,0,0,0,3,4,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2}}, new int[]{100, 50});
            
        try {
            File file = new File("generationtest.txt");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            writePlayerToFile(player1, bufferedWriter);
            
            bufferedWriter.flush();
            bufferedWriter.close();
            
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            MyDraughtsPlayer player2 = readPlayerFromFile(bufferedReader);
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
            writer.write("Patterns");
            writer.newLine();
            writer.write(String.valueOf(player.patterns.length));
            writer.newLine();
            for (int i = 0; i < player.patterns.length; i++) {
                for (int j = 0; j < player.patterns[i].length; j++) {
                    writer.write(String.valueOf(player.patterns[i][j]));
                }
                writer.write(".");
                writer.write(String.valueOf(player.patternValues[i]));
                writer.newLine();
            }
            writer.write("PlayerEnd");
            writer.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public MyDraughtsPlayer readPlayerFromFile(BufferedReader reader) throws Exception { 
        String firstLine = reader.readLine();
        if (!firstLine.equals("PlayerStart")) {
            throw new Exception("Reader is not in correct position in file");
        }
        int generation = Integer.parseInt(reader.readLine());
        int fitness = Integer.parseInt(reader.readLine());
        int scoreValue = Integer.parseInt(reader.readLine());
        int winValue = Integer.parseInt(reader.readLine());
        int tempiValue = Integer.parseInt(reader.readLine());
        String patternLine = reader.readLine();
        if (!patternLine.equals("Patterns")) {
            throw new Exception("Player (patterns) not formatted properly in file");
        }
        int amountOfPatterns = Integer.parseInt(reader.readLine());
        int[] patternValues = new int[amountOfPatterns];
        int[][] patterns = new int[amountOfPatterns][50];
        for (int i = 0; i < amountOfPatterns; i++) {
            String currentLine = reader.readLine();
            //patterns always have length 50
            for (int j = 0; j < 50; j++) {
                patterns[i][j] = Character.getNumericValue(currentLine.charAt(j));
            }
            if (currentLine.charAt(50) != '.') { 
                throw new Exception("Incorrect syntax of pattern");
            }
            //patternvalue is what's left of the string
            patternValues[i] = Integer.parseInt(currentLine.substring(51, currentLine.length()));
        }
        String endLine = reader.readLine();
        if (!endLine.equals("PlayerEnd")) {
            throw new Exception("End of player not in proper place in file");
        }
        MyDraughtsPlayer scannedPlayer = new MyDraughtsPlayer(searchDepth, scoreValue, winValue, tempiValue, patterns, patternValues);
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
        if (whites+whiteKings == 0) {
            return MIN_VALUE;
        }
        else if (blacks + blackKings == 0) {
            return MAX_VALUE;
        }
        score = whites - blacks + 3*(whiteKings - blackKings);
        return score;
    }
}
