# Minesweeper
I coded Minesweeper in Java for my CIS 1200 final project. This implementation uses Java Swing to render the game GUI.
# Features
- Fully functional Minsweeper game, inclulding a reset game button and instructions on how to play.
- Mine generation algorithm that guarantees you open at least a 3x3 area on your first uncovered tile. No pesky first turn mine hits or uncovering only a single tile!
- Autosaving feature. Will resume your last unfinished game upon reopening the window.
# Java Class Overview
- **Minesweeper** is the game model that handles the core game logic.
- **GameGrid** handles the rendering and controlling of the game model via the GUI.
- **RunMinesweeper** handles the top-level GUI that the game is displayed on.
- **Tile** is a class I made to handle the state of each of the tiles in the minesweeper grid.
- **GameState** is an enum I made to make the state of the game easier to handle.
- **MenuUtilities** contains other functionality that is present in the top-level GUI, but not a part of the core game model.
