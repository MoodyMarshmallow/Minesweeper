package org.minesweeper;

import javax.swing.*;

/**
 * This class handles any functionality that is not directly related to the game
 * but needed for the GUI.
 */
public class MenuUtilities {
    public static void displayInstructions() {
        final String instructionsText = """
                Welcome to Minesweeper!

                On the game window, you'll see a grid of tiles.
                Your goal is to uncover all the tiles that
                don't have mines beneath them. Left click to
                uncover a tile!

                Don't worry! This is not all guesswork. Your
                first uncovered tile is guaranteed to not be a
                mine. From then on, you'll see a series of
                clues in the form of numbers on the tiles next
                to a mine.

                Each number indicates the number of mines in
                the 8 tiles surrounding the numbered tile!

                Use these numbers to deduce the positions of
                the mines!

                For your convenience, you can also right click
                a tile to mark it as a potential mine! Tiles
                with marks on them cannot be uncovered until
                you right click on them again to remove the
                mark.

                Good luck minesweeping!
                """;
        final JFrame frame = new JFrame("Minesweeper Instructions.");
        frame.setLocation(600, 600);
        final JTextArea instructions = new JTextArea(instructionsText);
        instructions.setEditable(false);
        frame.add(instructions);
        frame.pack();
        frame.setVisible(true);
    }
}
