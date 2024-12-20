package org.minesweeper;

import org.junit.jupiter.api.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MinesweeperTest {
    @Test
    public void testCreateMinesweeperFromFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("files/testFile.csv"))) {
            writer.write("""
                    4,4,4,
                    2,m,2,,
                    3,m,3,,
                    3,m,3,,
                    2,m,2,,
                    """);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
        Minesweeper test = new Minesweeper("files/testFile.csv");
        String expected = """
                2*2%
                3*3%
                3*3%
                2*2%
                """;
        assertEquals(test.asciiGameState(true), expected);
        if (!(new File("files/testFile.csv")).delete()) {
            throw new RuntimeException("Failed to delete file: files/testFile.csv");
        }
    }

    @Test
    public void testGenerateClueNumbers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("files/testFile.csv"))) {
            writer.write("""
                    5,5,8,
                    ,,,,,
                    ,m,m,m,,
                    ,m,,m,,
                    ,m,m,m,,
                    ,,,,,
                    """);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
        Minesweeper test = new Minesweeper("files/testFile.csv");
        int[][] minePos = { { 1, 1 }, { 1, 2 }, { 1, 3 }, { 2, 1 }, { 2, 3 }, { 3, 1 }, { 3, 2 },
            { 3, 3 } };
        test.generateClueNumbers(minePos);
        String expected = """
                12321
                2***2
                3*8*3
                2***2
                12321
                """;
        assertEquals(test.asciiGameState(true), expected);
    }

    @Test
    public void testCreateMinesweeper() {
        Minesweeper test = new Minesweeper(4, 4, 1);
        Tile[][] output = test.getTiles();
        for (Tile[] row : output) {
            for (Tile tile : row) {
                assertEquals(tile.toString(), "");
            }
        }
    }

    @Test
    public void testFirstTurnMinesweeper() {
        Minesweeper test = new Minesweeper(4, 4, 7);
        test.playTurn(1, 1);
        Tile[][] tiles = test.getTiles();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertFalse(tiles[i][j].hasMine());
            }
        }
    }

    @Test
    public void testFirstTurnMinesweeperCorner() {
        Minesweeper test2 = new Minesweeper(4, 4, 7);
        test2.playTurn(0, 0);
        Tile[][] tiles2 = test2.getTiles();
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                assertFalse(tiles2[i][j].hasMine());
            }
        }
    }

    @Test
    public void testMultiTurnMinesweeper() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("files/testFile.csv"))) {
            writer.write("""
                    4,4,4,
                    2,m,2,,
                    3,m,3,,
                    3,m,3,,
                    2u,m,2,,
                    """);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
        Minesweeper test = new Minesweeper("files/testFile.csv");
        test.playTurn(0, 0);
        test.playTurn(1, 0);
        String expected = """
                2%%%
                3%%%
                %%%%
                2%%%
                """;
        assertEquals(expected, test.asciiGameState(false));
        if (!(new File("files/testFile.csv")).delete()) {
            throw new RuntimeException("Failed to delete file: files/testFile.csv");
        }
    }

}
