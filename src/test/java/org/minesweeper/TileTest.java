package org.minesweeper;

import org.junit.jupiter.api.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

public class TileTest {
    @Test
    public void testEmptyTile() {
        Tile tile = new Tile();
        assertTrue(tile.isCovered());
        assertEquals(tile.getClueNumber(), 0);
        assertFalse(tile.hasMine());
        assertFalse(tile.hasFlag());
    }

    @Test
    public void testUncoverTile() {
        Tile tile = new Tile();
        tile.uncover();
        assertFalse(tile.isCovered());
    }

    @Test
    public void testUncoveringOneTile() {
        Tile tile = new Tile();
        Tile tile2 = new Tile();
        tile2.uncover();
        assertTrue(tile.isCovered());
        assertFalse(tile2.isCovered());
    }

    @Test
    public void testPlaceMineTile() {
        Tile tile = new Tile();
        tile.placeMine();
        assertTrue(tile.hasMine());
    }

    @Test
    public void testTileToString() {
        Tile tile = new Tile();
        assertEquals(tile.toString(), "");
        tile.placeMine();
        assertEquals(tile.toString(), "m");
        tile.uncover();
        assertEquals(tile.toString(), "um");

        Tile tile2 = new Tile();
        tile2.toggleFlag();
        assertEquals(tile2.toString(), "f");
    }
}
