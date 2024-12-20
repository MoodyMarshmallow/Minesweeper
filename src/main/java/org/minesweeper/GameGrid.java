package org.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * This class is both the view and the controller for the minesweeper game. It
 * is
 * responsible for rendering the game's graphics as well as updating the game's
 * model and graphics according to player input.
 */
public class GameGrid extends JPanel {

    private final Minesweeper minesweeper; // game model
    private GameState gameState; // current game state
    private final JLabel statusText; // text that displays game state

    // Game constants
    public static final int GRID_WIDTH = 10; // width of grid in tiles
    public static final int GRID_HEIGHT = 10; // height of grid in tiles
    public static final int MINE_COUNT = 10;
    public static final int TILE_SIDE_LENGTH = 20; // dimensions of tile in px
    public static final String GAME_SAVE_FILENAME = "files/MinesweeperGameSave.csv";

    /**
     * Initializes the game grid
     */
    public GameGrid(JLabel initStatusText) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on grid area.
        setFocusable(true);
        statusText = initStatusText;

        // check if there is a saved game
        File gameSave = new File(GAME_SAVE_FILENAME);

        if (gameSave.exists()) {
            minesweeper = new Minesweeper(GAME_SAVE_FILENAME);
            gameState = GameState.IN_PROGRESS;
        } else {
            minesweeper = new Minesweeper(GRID_WIDTH, GRID_HEIGHT, MINE_COUNT);
            gameState = GameState.NOT_STARTED;
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (gameState != GameState.WON) {
                    Point p = e.getPoint();

                    // updates game model and state with tile clicked by mouse
                    int iTile = p.y / TILE_SIDE_LENGTH, jTile = p.x / TILE_SIDE_LENGTH;
                    if (iTile >= 0 && iTile < GRID_HEIGHT && jTile >= 0 && jTile < GRID_WIDTH
                            && gameState != GameState.LOST) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            gameState = minesweeper.playTurn(iTile, jTile);

                            // deletes save if game is lost
                            if (gameState == GameState.LOST && gameSave.exists()) {
                                if (!gameSave.delete()) {
                                    throw new RuntimeException(
                                            "Failed to delete file: " + GAME_SAVE_FILENAME
                                    );
                                }
                            }
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            minesweeper.toggleTileFlag(iTile, jTile);
                        }
                    }

                    updateStatusText();
                    repaint();
                }
            }
        });
    }

    /**
     * Saves the minesweeper game if it is in progress.
     */
    public void attemptSaveGame() {
        if (gameState == GameState.IN_PROGRESS) {
            minesweeper.saveGame(GAME_SAVE_FILENAME);
        }
    }

    /**
     * (Re)sets the game to its initial state and deletes any previous game save
     */
    public void reset() {
        minesweeper.reset();
        gameState = GameState.NOT_STARTED;
        updateStatusText();
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * Called to signify that game has finished initializing.
     */
    public void finishSetup() {
        updateStatusText();
        repaint();
        requestFocusInWindow();
    }

    public void updateStatusText() {
        switch (gameState) {
            case NOT_STARTED -> statusText.setText("Click on the minefield to begin!");
            case IN_PROGRESS -> statusText.setText("Find all the mines!");
            case WON -> statusText.setText("You found all the mines!");
            case LOST -> statusText.setText("Kaboom!");
            default -> throw new IllegalStateException("Unexpected gameState: " + gameState);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw contents of the tiles
        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                Tile target = minesweeper.getTile(i, j);
                int xStart = j * TILE_SIDE_LENGTH, yStart = i * TILE_SIDE_LENGTH;

                if (target.isCovered()) {
                    // Color tile background
                    g.setColor(Color.decode("#BDBDBD"));
                    g.fillRect(xStart, yStart, TILE_SIDE_LENGTH, TILE_SIDE_LENGTH);

                    if (target.hasFlag()) {
                        // Draw flag
                        g.setColor(Color.RED);
                        g.drawLine(
                                xStart + 1, yStart + 1, xStart + TILE_SIDE_LENGTH - 1,
                                yStart + TILE_SIDE_LENGTH - 1
                        );
                        g.drawLine(
                                xStart + 1, yStart + TILE_SIDE_LENGTH - 1,
                                xStart + TILE_SIDE_LENGTH - 1, yStart + 1
                        );
                    }

                    if (target.hasMine() && gameState == GameState.LOST) {
                        // Color tile background
                        g.setColor(Color.RED);
                        g.fillRect(xStart, yStart, TILE_SIDE_LENGTH, TILE_SIDE_LENGTH);

                        // Draw mine
                        g.setColor(Color.BLACK);
                        g.fillOval(
                                xStart + 2, yStart + 2, TILE_SIDE_LENGTH - 4, TILE_SIDE_LENGTH - 4
                        );
                    }
                } else {
                    // Color tile background
                    g.setColor(Color.decode("#949494"));
                    g.fillRect(xStart, yStart, TILE_SIDE_LENGTH, TILE_SIDE_LENGTH);

                    int clue = target.getClueNumber();

                    // Draw clue number
                    if (clue != 0) {
                        switch (clue) {
                            case 1 -> g.setColor(Color.BLUE);
                            case 2 -> g.setColor(Color.decode("#417F24"));
                            case 3 -> g.setColor(Color.RED);
                            case 4 -> g.setColor(Color.decode("#12087E"));
                            case 5 -> g.setColor(Color.decode("#72150D"));
                            case 6 -> g.setColor(Color.decode("#458083"));
                            case 7 -> g.setColor(Color.decode("#74197F"));
                            case 8 -> g.setColor(Color.ORANGE);
                            default -> throw new IllegalStateException("Unexpected clue: " + clue);
                        }
                        g.setFont(new Font("TimesRoman", Font.BOLD, 16));
                        g.drawString(
                                String.valueOf(clue), xStart + 7, yStart + TILE_SIDE_LENGTH - 4
                        );
                    }
                }
            }
        }

        // draw the minefield grid
        g.setColor(Color.BLACK);
        for (int i = 0; i <= GRID_HEIGHT; i++) {
            int y = i * TILE_SIDE_LENGTH;
            g.drawLine(0, y, GRID_WIDTH * TILE_SIDE_LENGTH, y);
        }

        for (int i = 0; i <= GRID_WIDTH; i++) {
            int x = i * TILE_SIDE_LENGTH;
            g.drawLine(x, 0, x, GRID_HEIGHT * TILE_SIDE_LENGTH);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GRID_WIDTH * TILE_SIDE_LENGTH, GRID_HEIGHT * TILE_SIDE_LENGTH);
    }
}
