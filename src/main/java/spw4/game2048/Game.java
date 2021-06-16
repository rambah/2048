package spw4.game2048;

import java.util.*;

public class Game {

    public static int BOARD_SIZE = 4;

    private int[][] tiles;
    private int score;
    private boolean automaticSpawningEnabled;

    public Game() {
        this(true);
    }

    public Game(boolean automaticSpawningEnabled) {
        this.tiles = new int[BOARD_SIZE][BOARD_SIZE];
        this.automaticSpawningEnabled = automaticSpawningEnabled;
    }

    public int getScore() {
        return this.score;
    }

    public boolean isOver() {
        if (isWon())
            return true;

        // check if empty tile is available.
        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                if (this.getTileAt(r, c) == 0)  return false;
            }
        }

        // check if two equal tiles are next to each other.
        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                int tileNumber = this.getTileAt(r, c);
                // check if up neighbour is equal
                if (1 < r && tileNumber == this.getTileAt(r - 1, c)) return false;
                // check if down neighbour is equal
                if (r < Game.BOARD_SIZE - 2 && tileNumber == this.getTileAt(r + 1, c)) return false;
                // check if left neighbour is equal
                if (1 < c && tileNumber == this.getTileAt(r, c - 1)) return false;
                // check if right neighbour is equal
                if (c < Game.BOARD_SIZE - 2 && tileNumber == this.getTileAt(r, c + 1)) return false;
            }
        }
        return true;
    }

    public boolean isWon() {
        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                if (this.getTileAt(r, c) == 2048) return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Score: ").append(this.getScore()).append("\n");

        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            for (int c = 0; c < Game.BOARD_SIZE - 1; c++) {
                sb.append(this.getTileString(r, c)).append("\t\t");
            }
            sb.append(this.getTileString(r, Game.BOARD_SIZE - 1)).append("\n");
        }
        return sb.toString();
    }

    public void initialize() {
        this.tiles = new int[BOARD_SIZE][BOARD_SIZE];
        automaticSpawning(); automaticSpawning(); // spawn two tiles
    }

    public void move(Direction direction) {
        switch (direction) {
            case up:
                moveUp();
                break;
            case down:
                moveDown();
                break;
            case right:
                moveRight();
                break;
            case left:
                moveLeft();
                break;
        }
        automaticSpawning();
    }

    public void setTileAt(int row, int column, int num) throws IllegalArgumentException {
        if (row < 0 || column < 0 || row >= BOARD_SIZE || column >= BOARD_SIZE) throw new IllegalArgumentException("The given tile position is invalid.");
        if (!(num == 0 || isPowerOfTwo(num))) throw new IllegalArgumentException("The given tile number is invalid.");

        this.tiles[row][column] = num;
    }

    public int getTileAt(int row, int column) throws IllegalArgumentException {
        if (row < 0 || column < 0 || row >= BOARD_SIZE || column >= BOARD_SIZE) throw new IllegalArgumentException("The given tile position is invalid.");
        return this.tiles[row][column];
    }

    private String getTileString(int row, int col) {
        int tileNum = this.getTileAt(row, col);
        return tileNum == 0 ? "." : Integer.toString(tileNum);
    }

    // returns true, when a new tile could be spawned
    private boolean automaticSpawning() {
        if (automaticSpawningEnabled) {
            Random rand = new Random();

            // count empty tiles
            int emptyTiles = 0;
            for (int r = 0; r < Game.BOARD_SIZE; r++) {
                for (int c = 0; c < Game.BOARD_SIZE; c++) {
                    if (this.getTileAt(r, c) == 0) emptyTiles++;
                }
            }
            if (emptyTiles == 0) return false;

            // generate random number between 0 to (emptyTiles - 1)
            int randPosition = rand.nextInt(emptyTiles);

            // calculate probability for tile number (2: 90%, 4: 10%)
            int tileNumProbability = rand.nextInt(10);
            int randTileNum = tileNumProbability == 0 ? 4 : 2;

            for (int r = 0; r < Game.BOARD_SIZE; r++) {
                for (int c = 0; c < Game.BOARD_SIZE; c++) {
                    if (this.getTileAt(r, c) == 0) randPosition--;
                    if (randPosition == -1) {
                        this.setTileAt(r, c, randTileNum);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isPowerOfTwo(int n) {
        return n != 0 && ((n & (n - 1)) == 0);
    }

    private void moveUp() {
        for (int c = 0; c < Game.BOARD_SIZE; c++) {
            LinkedList<Integer> tiles = new LinkedList<>();
            // fill with non-empty tiles
            for (int r = 0; r < Game.BOARD_SIZE; r++) {
                int currTile = this.getTileAt(r, c);
                if (currTile != 0) tiles.addLast(currTile);
            }

            if (tiles.isEmpty()) continue;

            int[] newLine = new int[Game.BOARD_SIZE];
            int currIdx = 0;

            newLine[currIdx] = tiles.removeFirst();
            while (!tiles.isEmpty()) {
                int nextTile = tiles.removeFirst();
                if (newLine[currIdx] == nextTile) {
                    newLine[currIdx] *= 2;
                    this.score += newLine[currIdx];
                    currIdx++;
                } else {
                    if (newLine[currIdx] != 0) currIdx++;
                    newLine[currIdx] = nextTile;
                }
            }
            for (int r = 0; r < Game.BOARD_SIZE; r++) {
                this.setTileAt(r, c, newLine[r]);
            }
        }
    }

    private void moveDown() {
        for (int c = 0; c < Game.BOARD_SIZE; c++) {
            LinkedList<Integer> tiles = new LinkedList<>();
            // fill with non-empty tiles
            for (int r = Game.BOARD_SIZE - 1; r >= 0; r--) {
                int currTile = this.getTileAt(r, c);
                if (currTile != 0) tiles.addFirst(currTile);
            }

            if (tiles.isEmpty()) continue;

            int[] newLine = new int[Game.BOARD_SIZE];
            int currIdx = Game.BOARD_SIZE - 1;

            newLine[currIdx] = tiles.removeLast();
            while (!tiles.isEmpty()) {
                int nextTile = tiles.removeLast();
                if (newLine[currIdx] == nextTile) {
                    newLine[currIdx] *= 2;
                    this.score += newLine[currIdx];
                    currIdx--;
                } else {
                    if (newLine[currIdx] != 0) currIdx--;
                    newLine[currIdx] = nextTile;
                }
            }
            for (int r = 0; r < Game.BOARD_SIZE; r++) {
                this.setTileAt(r, c, newLine[r]);
            }
        }
    }

    private void moveRight() {
        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            LinkedList<Integer> tiles = new LinkedList<>();
            // fill with non-empty tiles
            for (int c = Game.BOARD_SIZE - 1; c >= 0; c--) {
                int currTile = this.getTileAt(r, c);
                if (currTile != 0) tiles.addFirst(currTile);
            }

            if (tiles.isEmpty()) continue;

            int[] newLine = new int[Game.BOARD_SIZE];
            int currIdx = Game.BOARD_SIZE - 1;

            newLine[currIdx] = tiles.removeLast();
            while (!tiles.isEmpty()) {
                int nextTile = tiles.removeLast();
                if (newLine[currIdx] == nextTile) {
                    newLine[currIdx] *= 2;
                    this.score += newLine[currIdx];
                    currIdx--;
                } else {
                    if (newLine[currIdx] != 0) currIdx--;
                    newLine[currIdx] = nextTile;
                }
            }
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                this.setTileAt(r, c, newLine[c]);
            }
        }
    }

    private void moveLeft() {
        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            LinkedList<Integer> tiles = new LinkedList<>();
            // fill with non-empty tiles
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                int currTile = this.getTileAt(r, c);
                if (currTile != 0) tiles.addLast(currTile);
            }

            if (tiles.isEmpty()) continue;

            int[] newLine = new int[Game.BOARD_SIZE];
            int currIdx = 0;

            newLine[currIdx] = tiles.removeFirst();
            while (!tiles.isEmpty()) {
                int nextTile = tiles.removeFirst();
                if (newLine[currIdx] == nextTile) {
                    newLine[currIdx] *= 2;
                    this.score += newLine[currIdx];
                    currIdx++;
                } else {
                    if (newLine[currIdx] != 0) currIdx++;
                    newLine[currIdx] = nextTile;
                }
            }
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                this.setTileAt(r, c, newLine[c]);
            }
        }
    }
}
