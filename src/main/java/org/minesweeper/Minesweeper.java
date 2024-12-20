package org.minesweeper;

import java.io.*;
import java.util.ArrayList;

/**
 * This is the model for the Minesweeper game.
 */
public class Minesweeper {
    private Tile[][] tiles;
    private int gridWidth, gridHeight, mineCount;
    private boolean firstTurnTaken;

    /**
     * Create a new game of Minesweeper.
     * 
     * @param gridWidth  The width of the minefield in tiles.
     * @param gridHeight The height of the minefield in tiles.
     * @param mineCount  The number of mines in the minefield.
     */
    public Minesweeper(int gridWidth, int gridHeight, int mineCount) {
        init(gridWidth, gridHeight, mineCount);
    }

    /**
     * Creates a Minesweeper game using a game save file.
     * 
     * @param filepath The path to the game save file.
     */
    public Minesweeper(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {

            // initialize new minesweeper game
            String[] gridInfo = reader.readLine().split(",");
            if (gridInfo.length != 3) {
                throw new IllegalArgumentException("Invalid grid info stored in file");
            }

            init(
                    Integer.parseInt(gridInfo[0]), Integer.parseInt(gridInfo[1]),
                    Integer.parseInt(gridInfo[2])
            );

            // restore tile info
            String line;
            int i = 0;

            while ((line = reader.readLine()) != null) {
                String[] tiles = line.split(",");
                for (int j = 0; j < tiles.length; j++) {
                    char[] tileInfo = tiles[j].toCharArray();
                    for (char info : tileInfo) {
                        switch (info) {
                            case 'u' -> {
                                this.tiles[i][j].uncover();
                                if (!firstTurnTaken) {
                                    firstTurnTaken = true;
                                }
                            }
                            case 'm' -> this.tiles[i][j].placeMine();
                            case 'f' -> this.tiles[i][j].toggleFlag();
                            case '1', '2', '3', '4', '5', '6', '7', '8' -> this.tiles[i][j]
                                    .setClueNumber(Character.getNumericValue(info));
                            default -> { }
                        }
                    }
                }
                i++;
            }

        } catch (IOException e) {
            System.err.println("An error occurred while reading from the file: " + e.getMessage());
        }
    }

    /**
     * Initializing method of minesweeper.
     * 
     * @param gridWidth  The width of the minefield in tiles.
     * @param gridHeight The height of the minefield in tiles.
     * @param mineCount  The number of mines in the minefield.
     */
    private void init(int gridWidth, int gridHeight, int mineCount) {
        if (gridWidth < 0) {
            throw new IllegalArgumentException("Grid width must be greater than 0");
        } else if (gridHeight < 0) {
            throw new IllegalArgumentException("Grid height must be greater than 0");
        } else if (mineCount <= 0) {
            throw new IllegalArgumentException("Mines count must be non-negative");
        } else if (mineCount > gridWidth * gridHeight - 9) {
            throw new IllegalArgumentException(
                    "Mine count must allow for 3x3 clear area to " +
                            "facilitate initial mine generation."
            );
        }
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.mineCount = mineCount;
        reset();
    }

    /**
     * Saves the current state of the game in the designated file.
     * 
     * @param filepath Path to game save file.
     */
    public void saveGame(String filepath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            // save grid dimensions
            writer.write(gridWidth + "," + gridHeight + "," + mineCount + ",\n");

            // save tile states
            for (Tile[] row : tiles) {
                for (Tile tile : row) {
                    writer.write(tile.toString() + ',');
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    /**
     * Gets the tiles of the minesweeper.
     * 
     * @return 2D array of tiles.
     */
    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * Checks if the given tile is within the minefield
     * 
     * @param i The row of the tile clicked, with the first row starting at i=0.
     * @param j The column coordinate of the tile clicked, with the first column
     *          starting at j=0.
     */
    private void checkOutOfBounds(int i, int j) {
        if (i < 0 || i >= gridHeight || j < 0 || j >= gridWidth) {
            throw new IndexOutOfBoundsException("position clicked is out of bounds");
        }
    }

    /**
     * toggle the presence of flag on tile
     * 
     * @param i The row of the tile clicked, with the first row starting at i=0.
     * @param j The column coordinate of the tile clicked, with the first column
     *          starting at j=0.
     */
    public void toggleTileFlag(int i, int j) {
        checkOutOfBounds(i, j);
        Tile target = tiles[i][j];
        if (target.isCovered()) {
            target.toggleFlag();
        }
    }

    /**
     * Plays a turn in the Minesweeper game model.
     * 
     * @param i The row of the tile clicked, with the first row starting at i=0.
     * @param j The column coordinate of the tile clicked, with the first column
     *          starting at j=0.
     * @return a GameState enum corresponding to whether the player has won, lost,
     *         or neither.
     */
    public GameState playTurn(int i, int j) {
        checkOutOfBounds(i, j);

        // does nothing to flagged tiles or uncovered tiles
        if (tiles[i][j].hasFlag() || !tiles[i][j].isCovered()) {
            return GameState.IN_PROGRESS;
        }

        // generate mines if first turn
        if (!firstTurnTaken) {
            randomMineMap(i, j);
            firstTurnTaken = true;
        }

        // check if tile has a mine
        if (tiles[i][j].hasMine()) {
            return GameState.LOST;
        }

        // uncover region clicked
        uncoverTile(i, j);

        // check if game is finished
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile.isCovered() && !tile.hasMine()) {
                    return GameState.IN_PROGRESS;
                }
            }
        }
        return GameState.WON;
    }

    /**
     * Generates random map of mines and clue numbers based on first tile clicked.
     * 
     * @param i The row of the tile clicked, with the first row starting at i=0.
     * @param j The column coordinate of the tile clicked, with the first column
     *          starting at j=0.
     */
    private void randomMineMap(int i, int j) {
        int firstTile = i * gridWidth + j; // tile number, with first mine at position 0
        int totalTiles = gridWidth * gridHeight;
        ArrayList<Integer> tileNumbers = new ArrayList<>();
        ArrayList<Integer> ignoredTiles = new ArrayList<>();
        int[][] minePositions = new int[mineCount][2];

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                ignoredTiles.add(firstTile + x + y * gridWidth);
            }
        }

        /* === fisher-yates shuffle implementation === */

        // make array to be shuffled, discarding 3x3 area centered around first turn
        for (int k = 0; k < totalTiles; k++) {
            if (!ignoredTiles.contains(k)) {
                tileNumbers.add(k);
            }
        }

        // shuffle
        for (int k = 0; k < mineCount; k++) {
            int mineNumber = (int) (Math.random() * (totalTiles - 9 - k) + k);
            int temp = tileNumbers.get(k);
            tileNumbers.set(k, tileNumbers.get(mineNumber));
            tileNumbers.set(mineNumber, temp);
        }

        /* ========================================== */

        // Place mines
        for (int k = 0; k < mineCount; k++) {
            int mineNumber = tileNumbers.get(k);
            int iMine = (int) Math.ceil((double) (mineNumber + 1) / gridWidth) - 1,
                    jMine = mineNumber % gridWidth;
            tiles[iMine][jMine].placeMine();

            // record mine positions for generating clue numbers
            minePositions[k][0] = iMine;
            minePositions[k][1] = jMine;
        }

        // generate clue numbers
        generateClueNumbers(minePositions);
    }

    /**
     * Generates clue numbers for the minesweeper game given positions of mines as a
     * 2D array.
     * 
     * @param minePositions A 2D array of the positions of the mines given as {ith
     *                      row, jth column}.
     */
    public void generateClueNumbers(int[][] minePositions) {
        for (int[] mine : minePositions) {
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    int xTarget = mine[0] + x, yTarget = mine[1] + y;

                    if (xTarget < gridHeight && xTarget >= 0 && yTarget < gridWidth
                            && yTarget >= 0) {
                        Tile target = tiles[xTarget][yTarget];

                        if (!target.hasMine()) {
                            target.setClueNumber(target.getClueNumber() + 1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Uncovers the region that is clicked
     * 
     * @param i The row of the tile clicked, with the first row starting at i=0.
     * @param j The column coordinate of the tile clicked, with the first column
     *          starting at j=0.
     */
    public void uncoverTile(int i, int j) {
        Tile currentTile = tiles[i][j];
        currentTile.uncover();

        if (currentTile.getClueNumber() != 0) {
            return;
        }

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                int xTarget = i + x, yTarget = j + y;
                if (xTarget < gridHeight && xTarget >= 0 && yTarget < gridWidth && yTarget >= 0) {
                    Tile target = tiles[xTarget][yTarget];
                    if (!target.hasMine() && target.isCovered()) {
                        uncoverTile(xTarget, yTarget);
                    }
                }
            }
        }
    }

    /**
     * (Re)sets the game.
     */
    public void reset() {
        firstTurnTaken = false;
        tiles = new Tile[gridHeight][gridWidth];
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                tiles[i][j] = new Tile();
            }
        }
    }

    /**
     * Gets a tile given the position of the tile on the minefield.
     * 
     * @param i The row of the tile clicked, with the first row starting at i=0.
     * @param j The column coordinate of the tile clicked, with the first column
     *          starting at j=0.
     * @return The corresponding Tile object
     */
    public Tile getTile(int i, int j) {
        checkOutOfBounds(i, j);
        return tiles[i][j];
    }

    /**
     * Prints ascii representation of the game screen. Used for testing.
     * 
     * @param transparent True reveals mines and clue numbers, false hides them.
     */
    public String asciiGameState(boolean transparent) {
        StringBuilder builder = new StringBuilder();
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile.hasMine() && transparent) {
                    builder.append('*');
                } else if (tile.getClueNumber() != 0 && (transparent || !tile.isCovered())) {
                    builder.append(tile.getClueNumber());
                } else if (tile.isCovered()) {
                    builder.append('%');
                } else {
                    builder.append('-');
                }
            }
            builder.append('\n');
        }
        return builder.toString();
    }

}
