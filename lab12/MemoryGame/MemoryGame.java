package MemoryGame;

import byowTools.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!", "You are so slay!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        String randomString = "";
        for (int i = 0; i < n; i++) {
            randomString = randomString + Character.toString(CHARACTERS[rand.nextInt(CHARACTERS.length)]);
        }
        return randomString;
    }

    public void drawFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
        * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.width / 2, this.height / 2, s);


        //TODO: If the game is not over, display encouragement, and let the user know if they
        // should be typing their answer or watching for the next round.

            if (playerTurn = true) {
                StdDraw.text((this.width / 2), this.height - 1.5, "Your turn! :3");
                StdDraw.textRight(this.width - 0.75, this.height - 1.5, ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)]);
            } else {
                StdDraw.text(this.width / 2, this.height - 1.5, "Watch this pls! <3");
                StdDraw.textRight(this.width - 0.75, this.height - 1.5, ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)]);
            }

        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
            char[] splitLetters = letters.toCharArray();
            for (int i = 0; i < splitLetters.length; i++) {
                drawFrame(String.valueOf(splitLetters[i]));
                StdDraw.pause(500);
            }

    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String playerInput = "";
        while (playerInput.length() < n) {
            while ((StdDraw.hasNextKeyTyped()) == true) {
                playerInput = playerInput + StdDraw.nextKeyTyped();
            }
        }
        return playerInput;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        this.gameOver = false;
        this.round = 1;
        this.playerTurn = false;

        //TODO: Establish Engine loop
        while (!gameOver) {
            drawFrame("You are on round: " + round);
            StdDraw.pause(1000);
            drawFrame("Watch this (:<");
            StdDraw.pause(1000);
            String randomString = generateRandomString(round);
            flashSequence(randomString);
            playerTurn = true;
            if (this.playerTurn = true) {
                drawFrame("your turn");
                StdDraw.pause(500);
                drawFrame(solicitNCharsInput(round));
            }
        }
        this.drawFrame("Game Over! You made it to round: " + this.round);
    }

}
