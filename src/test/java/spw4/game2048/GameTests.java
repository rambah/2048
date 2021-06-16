package spw4.game2048;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameTests {


    @DisplayName("Game.isOver when a move is still possible returns false")
    @Test
    void isOverWhenMoveIsAvailableReturnsFalse() {
        Game game = new Game();

        boolean result = game.isOver();

        assertThat(result).isFalse();
    }

    @DisplayName("Game.isOver when all tiles are occupied but a move (down or up) is still possible returns false")
    @Test
    void isOverWhenAllTilesAreOccupiedAndMoveIsAvailableReturnsFalse() {
        Game game = new Game(false);
        game.setTileAt(0, 0, 2); game.setTileAt(0, 1, 4); game.setTileAt(0, 2, 8); game.setTileAt(0, 3, 16);
        game.setTileAt(1, 0, 32); game.setTileAt(1, 1, 4); game.setTileAt(1, 2, 128); game.setTileAt(1, 3, 256);
        game.setTileAt(2, 0, 2); game.setTileAt(2, 1, 4); game.setTileAt(2, 2, 8); game.setTileAt(2, 3, 16);
        game.setTileAt(3, 0, 32); game.setTileAt(3, 1, 64); game.setTileAt(3, 2, 128); game.setTileAt(3, 3, 256);

        boolean result = game.isOver();

        assertThat(result).isFalse();
    }

    @DisplayName("Game.isOver when no move can be done anymore returns true")
    @Test
    void isOverWhenMoveIsNotAvailableReturnsTrue() {
        Game game = new Game(false);
        game.setTileAt(0, 0, 2); game.setTileAt(0, 1, 4); game.setTileAt(0, 2, 8); game.setTileAt(0, 3, 16);
        game.setTileAt(1, 0, 32); game.setTileAt(1, 1, 64); game.setTileAt(1, 2, 128); game.setTileAt(1, 3, 256);
        game.setTileAt(2, 0, 2); game.setTileAt(2, 1, 4); game.setTileAt(2, 2, 8); game.setTileAt(2, 3, 16);
        game.setTileAt(3, 0, 32); game.setTileAt(3, 1, 64); game.setTileAt(3, 2, 128); game.setTileAt(3, 3, 256);

        boolean result = game.isOver();

        assertThat(result).isTrue();
    }

    @DisplayName("Game.isOver when the game is won returns true")
    @Test
    void isOverWhenGameIsWonReturnsTrue() {
        Game game = new Game(false);
        game.setTileAt(0, 0, 2048);

        boolean result = game.isOver();

        assertThat(game.isWon()).isTrue();
        assertThat(result).isTrue();
    }

    @DisplayName("Game.isWon when the '2048' tile is not present returns false")
    @Test
    void isWonWhenTile2048IsNotPresentReturnsFalse() {
        Game game = new Game();

        boolean result = game.isWon();

        assertThat(result).isFalse();
    }

    @DisplayName("Game.isWon when the '2048' tile is present returns true")
    @Test
    void isWonWhenTile2048IsPresentReturnsTrue() {
        Game game = new Game(false);
        game.setTileAt(0, 0, 2048);

        boolean result = game.isWon();

        assertThat(result).isTrue();
    }

    @DisplayName("Game.toString returns a string in the expected format at any given point")
    @Test
    void toStringReturnsExpectedString() {
        Game game = new Game(false);
        game.setTileAt(0, 0, 4); game.setTileAt(0, 3, 8); game.setTileAt(3, 3, 2048);
        game.setTileAt(1, 2, 16); game.setTileAt(2, 1, 32); game.setTileAt(3, 0, 2);

        String result = game.toString();

        String expected = "Score: 0\n" +
                "4\t\t.\t\t.\t\t8\n" +
                ".\t\t.\t\t16\t\t.\n" +
                ".\t\t32\t\t.\t\t.\n" +
                "2\t\t.\t\t.\t\t2048\n";
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("Game.getScore at the beginning and middle of the game returns correct score")
    @Test
    void getScoreAtStartAndMidGameReturnsCorrectScore() {
        Game game = new Game(false);
        game.setTileAt(0, 0, 2); game.setTileAt(0, 1, 2);

        int score = game.getScore();

        assertThat(score).isEqualTo(0);

        // right move; 2 + 2 = 4; score should be 4
        game.move(Direction.right);
        score = game.getScore();

        assertThat(score).isEqualTo(4);

        // up move; 4 + 4 = 8; score should be 12
        game.setTileAt(3, 3, 4);

        game.move(Direction.up);
        score = game.getScore();

        assertThat(score).isEqualTo(12);

        // left move; 8 + 8 = 16; score should be 28
        game.setTileAt(0, 2, 8);

        game.move(Direction.left);
        score = game.getScore();

        assertThat(score).isEqualTo(28);

        // down move; 16 + 2 = ?; score should stay 28
        game.setTileAt(2, 0, 2);

        game.move(Direction.down);
        score = game.getScore();

        assertThat(score).isEqualTo(28);
    }

    @DisplayName("Game.move when direction is up the board stays valid")
    @Test
    void moveWhenDirectionIsUp() {
        Game game = new Game(false);
        game.setTileAt(0, 0, 4); game.setTileAt(2, 0, 4);
        game.setTileAt(0, 1, 2); game.setTileAt(1, 1, 8);
        game.setTileAt(0, 2, 4); game.setTileAt(1, 2, 4); game.setTileAt(3, 2, 8);
        game.setTileAt(0, 3, 4); game.setTileAt(2, 3, 4); game.setTileAt(3, 3, 4);

        game.move(Direction.up);

        // column 0 should change, two tiles will be merged to one
        assertThat(game.getTileAt(0, 0)).isEqualTo(8);
        assertThat(game.getTileAt(2, 0)).isEqualTo(0);

        // column 1 should be unchanged
        assertThat(game.getTileAt(0, 1)).isEqualTo(2);
        assertThat(game.getTileAt(1, 1)).isEqualTo(8);

        // column 2 should change, two tiles will be merged to one, position of one tile will be changed
        assertThat(game.getTileAt(0, 2)).isEqualTo(8);
        assertThat(game.getTileAt(1, 2)).isEqualTo(8);
        assertThat(game.getTileAt(3, 2)).isEqualTo(0);
        
        // column 3 should change, two tiles will be merged to one and one tile will is being moved up
        assertThat(game.getTileAt(0, 3)).isEqualTo(8);
        assertThat(game.getTileAt(1, 3)).isEqualTo(4);
        assertThat(game.getTileAt(2, 3)).isEqualTo(0);
        assertThat(game.getTileAt(3, 3)).isEqualTo(0);
    }

    @DisplayName("Game.move when direction is down the board stays valid")
    @Test
    void moveWhenDirectionIsDown() {
        Game game = new Game(false);
        game.setTileAt(0, 0, 4); game.setTileAt(2, 0, 4);
        game.setTileAt(0, 1, 2); game.setTileAt(1, 1, 8);
        game.setTileAt(0, 2, 4); game.setTileAt(1, 2, 4); game.setTileAt(3, 2, 8);
        game.setTileAt(0, 3, 4); game.setTileAt(2, 3, 4); game.setTileAt(3, 3, 4);

        game.move(Direction.down);

        // column 0 should change, two tiles will be merged two one
        assertThat(game.getTileAt(0, 0)).isEqualTo(0);
        assertThat(game.getTileAt(2, 0)).isEqualTo(0);
        assertThat(game.getTileAt(3, 0)).isEqualTo(8);

        // column 1 should change, positions will be changed
        assertThat(game.getTileAt(0, 1)).isEqualTo(0);
        assertThat(game.getTileAt(1, 1)).isEqualTo(0);
        assertThat(game.getTileAt(2, 1)).isEqualTo(2);
        assertThat(game.getTileAt(3, 1)).isEqualTo(8);

        // column 2 should change, two tiles will be merged to one, position of one tile will be changed
        assertThat(game.getTileAt(0, 2)).isEqualTo(0);
        assertThat(game.getTileAt(1, 2)).isEqualTo(0);
        assertThat(game.getTileAt(2, 2)).isEqualTo(8);
        assertThat(game.getTileAt(3, 2)).isEqualTo(8);

        // column 3 should change, two tiles will be merged to one. one tile will stay unchanged
        assertThat(game.getTileAt(0, 3)).isEqualTo(0);
        assertThat(game.getTileAt(1, 3)).isEqualTo(0);
        assertThat(game.getTileAt(2, 3)).isEqualTo(4);
        assertThat(game.getTileAt(3, 3)).isEqualTo(8);
    }

    @DisplayName("Game.move when direction is right the board stays valid")
    @Test
    void moveWhenDirectionIsRight() {
        Game game = new Game(false);
        game.setTileAt(0, 0, 4); game.setTileAt(0, 2, 4);
        game.setTileAt(1, 0, 2); game.setTileAt(1, 1, 8);
        game.setTileAt(2, 0, 4); game.setTileAt(2, 1, 4); game.setTileAt(2, 3, 8);
        game.setTileAt(3, 0, 4); game.setTileAt(3, 2, 4); game.setTileAt(3, 3, 4);

        game.move(Direction.right);

        // row 0 should change, two tiles will be merged two one
        assertThat(game.getTileAt(0, 0)).isEqualTo(0);
        assertThat(game.getTileAt(0, 2)).isEqualTo(0);
        assertThat(game.getTileAt(0, 3)).isEqualTo(8);

        // row 1 should change, positions will be changed
        assertThat(game.getTileAt(1, 0)).isEqualTo(0);
        assertThat(game.getTileAt(1, 1)).isEqualTo(0);
        assertThat(game.getTileAt(1, 2)).isEqualTo(2);
        assertThat(game.getTileAt(1, 3)).isEqualTo(8);

        // row 2 should change, two tiles will be merged to one, position of one tile will be changed
        assertThat(game.getTileAt(2, 0)).isEqualTo(0);
        assertThat(game.getTileAt(2, 1)).isEqualTo(0);
        assertThat(game.getTileAt(2, 2)).isEqualTo(8);
        assertThat(game.getTileAt(2, 3)).isEqualTo(8);

        // row 3 should change, two tiles will be merged to one. one tile will stay unchanged
        assertThat(game.getTileAt(3, 0)).isEqualTo(0);
        assertThat(game.getTileAt(3, 1)).isEqualTo(0);
        assertThat(game.getTileAt(3, 2)).isEqualTo(4);
        assertThat(game.getTileAt(3, 3)).isEqualTo(8);
    }

    @DisplayName("Game.move when direction is left the board stays valid")
    @Test
    void moveWhenDirectionIsLeft() {
        Game game = new Game(false);
        game.setTileAt(0, 0, 4); game.setTileAt(0, 2, 4);
        game.setTileAt(1, 0, 2); game.setTileAt(1, 1, 8);
        game.setTileAt(2, 0, 4); game.setTileAt(2, 1, 4); game.setTileAt(2, 3, 8);
        game.setTileAt(3, 0, 4); game.setTileAt(3, 2, 4); game.setTileAt(3, 3, 4);

        game.move(Direction.left);

        // row 0 should change, two tiles will be merged to one
        assertThat(game.getTileAt(0, 0)).isEqualTo(8);
        assertThat(game.getTileAt(0, 2)).isEqualTo(0);

        // row 1 should be unchanged
        assertThat(game.getTileAt(1, 0)).isEqualTo(2);
        assertThat(game.getTileAt(1, 1)).isEqualTo(8);

        // row 2 should change, two tiles will be merged to one, position of one tile will be changed
        assertThat(game.getTileAt(2, 0)).isEqualTo(8);
        assertThat(game.getTileAt(2, 1)).isEqualTo(8);
        assertThat(game.getTileAt(2, 3)).isEqualTo(0);

        // row 3 should change, two tiles will be merged to one and one tile will is being moved up
        assertThat(game.getTileAt(3, 0)).isEqualTo(8);
        assertThat(game.getTileAt(3, 1)).isEqualTo(4);
        assertThat(game.getTileAt(3, 2)).isEqualTo(0);
        assertThat(game.getTileAt(3, 3)).isEqualTo(0);
    }

    @DisplayName("Game.initialize at any given point clears the board and adds 2 random tiles")
    @Test
    void initializeAtAnyGivenPointCreatesANewBoardWithTwoTiles() { 
        Game game = new Game();
        
        game.initialize();

        // Count non-zero tiles; should be 2
        int tileCount = 0;
        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                assertThat(game.getTileAt(r, c)).isIn(0, 2, 4);
                if (game.getTileAt(r, c) != 0) tileCount++;
            }
        }
        assertThat(tileCount).isEqualTo(2);
    }

    @DisplayName("Game.setTileAt sets or replaces the tile at the given location on the board")
    @ParameterizedTest(name = "row = {0}, column = {1}, number = {2}")
    @CsvSource({"0, 0, 2", "0, 1, 2", "3, 2, 8", "3, 3, 32", "0, 0, 1024"})
    void setTileAtSetsTileAtGivenLocation(int row, int col, int number) {
        Game game = new Game(false);

        game.setTileAt(row, col, number);

        assertThat(game.getTileAt(row, col)).isEqualTo(number);
        // Assert that every other tile is set to zero
        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                if (!(row == r && col == c)) {
                    assertThat(game.getTileAt(r, c)).isEqualTo(0);
                }
            }
        }
    }

    @DisplayName("Game.setTileAt when the given position or tile number is invalid throws IllegalArgumentException")
    @ParameterizedTest(name = "row = {0}, column = {1}, number = {2}")
    @CsvSource({"0, 0, 3", "3, 1, -1", "5, 0, 8", "-1, 3, 32", "3, 3, 1023"})
    void setTileAtWhenPositionIsInvalidThrowsException(int row, int col, int number) {
        Game game = new Game(false);

        assertThrows(IllegalArgumentException.class, () -> { game.setTileAt(row, col, number); });
    }

    @DisplayName("Game.getTileAt when the given position is invalid throws IllegalArgumentException")
    @ParameterizedTest(name = "row = {0}, column = {1}")
    @CsvSource({"4, 4", "0, -1", "5, 0", "-1, -1", "-1, 5"})
    void getTileAtWhenPositionIsInvalidThrowsException(int row, int col) {
        Game game = new Game(false);

        assertThrows(IllegalArgumentException.class, () -> { game.getTileAt(row, col); });
    }

}
