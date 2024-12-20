package org.minesweeper;

/**
 * This is the class for a tile in minesweeper.
 */
public class Tile {
    private int clueNumber;
    private boolean hasMine, isCovered, hasFlag;

    /**
     * Creates a new, empty, covered tile.
     */
    public Tile() {
        this.hasMine = false;
        this.isCovered = true;
        this.hasFlag = false;
        this.clueNumber = 0;
    }

    public int getClueNumber() {
        return clueNumber;
    }

    public boolean hasFlag() {
        return hasFlag;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public boolean hasMine() {
        return hasMine;
    }

    /**
     * Sets clue number of tile.
     * 
     * @param clueNumber number to be displayed on tile. Must be between 1 and 8.
     */
    public void setClueNumber(int clueNumber) {
        if (clueNumber < 1 || clueNumber > 8) {
            throw new IllegalArgumentException("clueNumber must be between 1 and 8");
        } else if (hasMine) {
            throw new IllegalCallerException("target tile has mine");
        }
        this.clueNumber = clueNumber;
    }

    public void placeMine() {
        if (hasMine) {
            throw new IllegalCallerException("target tile already has mine");
        }
        hasMine = true;
    }

    public void toggleFlag() {
        if (!isCovered) {
            throw new IllegalCallerException("uncovered tile cannot be flagged");
        }
        hasFlag = !hasFlag;
    }

    public void uncover() {
        if (!isCovered) {
            throw new IllegalCallerException("target tile is already uncovered");
        } else if (hasFlag) {
            throw new IllegalCallerException("target tile has flag");
        }
        isCovered = false;
    }

    // override toString method to properly save tile state
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!isCovered) {
            sb.append("u");
        }
        if (hasMine) {
            sb.append("m");
        }
        if (hasFlag) {
            sb.append("f");
        }
        if (getClueNumber() != 0) {
            sb.append(clueNumber);
        }
        return sb.toString();
    }
}