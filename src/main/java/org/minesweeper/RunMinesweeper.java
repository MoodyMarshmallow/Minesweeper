package org.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This class handles the top-level widgets and GUI for the game.
 */
public class RunMinesweeper implements Runnable {
    public void run() {

        // Top-level frame
        final JFrame frame = new JFrame("Minesweeper");
        frame.setLayout(new BorderLayout());
        frame.setLocation(300, 300);

        // Add status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.NORTH);
        final JLabel status = new JLabel("Setting up...");
        status_panel.add(status);

        // Add game board
        final GameGrid gameGrid = new GameGrid(status);
        frame.add(gameGrid, BorderLayout.CENTER);

        /* ==== Render buttons ==== */

        // Make JPanel that holds the control buttons
        final JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        frame.add(controls, BorderLayout.EAST);

        // Add button controls
        final JButton newGame = new JButton("New Game");
        newGame.addActionListener(e -> gameGrid.reset());
        controls.add(newGame);
        final JButton instructions = new JButton("How To Play");
        instructions.addActionListener(e -> MenuUtilities.displayInstructions());
        controls.add(instructions);

        /* ======================== */

        // handle window closing
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                gameGrid.attemptSaveGame();
                System.exit(0);
            }
        });

        // Display game
        frame.pack();
        frame.setVisible(true);

        // Start game
        gameGrid.finishSetup();
    }
}
