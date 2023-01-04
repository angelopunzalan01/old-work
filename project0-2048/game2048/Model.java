package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author Angelo Punzalan
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private final Board _board;
    /** Current score. */
    private int _score;
    /** Maximum score so far.  Updated when game ends. */
    private int _maxScore;
    /** True iff game is ended. */
    private boolean _gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        _board = new Board(size);
        _score = _maxScore = 0;
        _gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        _board = new Board(rawValues);
        this._score = score;
        this._maxScore = maxScore;
        this._gameOver = gameOver;
    }

    /** Same as above, but gameOver is false. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore) {
        this(rawValues, score, maxScore, false);
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     * */
    public Tile tile(int col, int row) {
        return _board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return _board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (_gameOver) {
            _maxScore = Math.max(_score, _maxScore);
        }
        return _gameOver;
    }

    /** Return the current score. */
    public int score() {
        return _score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return _maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        _score = 0;
        _gameOver = false;
        _board.clear();
        setChanged();
    }

    /** Allow initial game board to announce a hot start to the GUI. */
    public void hotStartAnnounce() {
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        _board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     */
    public void tilt(Side side) {
        boolean tilted;
        tilted = false;
        _board.setViewingPerspective(side);
        for (int j = 0; j < _board.size(); j++) {
            for (int i = _board.size() - 1; i >= 0; i--) {
                Tile t = _board.tile(j, i);
                if (t != null) {
                    int highestPos = 3;
                    while (highestPos >= i) {
                        if (_board.tile(j, highestPos) == null) {
                            break;
                        }
                        highestPos--;
                    }
                    if (highestPos >= i) {
                        _board.move(j, highestPos, t);
                        tilted = true;
                        }
                    }
                }
            for (int i = 3; i >= 0; i-- ) {
                Tile curTile = _board.tile(j, i);
                // find out the next row's tile
                int nextPos = i - 1;
                if (nextPos < 0) {
                    break;
                }
                Tile nextTile = _board.tile(j, nextPos);
                // if one of the two tile is null we break this loop
                if (curTile == null || nextTile == null) {
                    break;
                }
                int nextValue = nextTile.value();
                if (nextValue == curTile.value()) {
                    // merge the two tiles whose value are equaled
                    _board.move(j, i, nextTile);
                    _score += curTile.value() * 2;
                    // move the tiles behind the two merged tiles to the place where the second tiles was
                    for (int k = nextPos - 1; k >= 0; k -- ) {
                        Tile tile = _board.tile(j, k);
                        if (tile == null) {
                            break;
                        }
                        if (k < _board.size()) {
                            _board.move(j, k + 1, tile);
                        }
                    }
                    tilted = true;
                }
            }
        }
        _board.setViewingPerspective(Side.NORTH);

        checkGameOver();
        if (tilted) {
            }

        _board.setViewingPerspective(Side.NORTH);
        checkGameOver();
        if (tilted) {
            setChanged();
        }
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        _gameOver = checkGameOver(_board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        /** iterate through each column and if any i, j, k, or l returns null, then return true */
        for (int i = 0; i < 4; i += 1) {
            if (b.tile(i, 0) == null) {
                return true;
            } else {
                for (int j = 0; j < 4; j += 1) {
                    if (b.tile(j, 3) == null) {
                        return true;
                    } else {
                        for (int k = 0; k < 4; k += 1) {
                            if (b.tile(k, 2) == null) {
                                return true;
                            } else {
                                for (int l = 0; l < 4; l += 1) {
                                    if (b.tile(k, 1) == null) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by this.MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        /** "given a tile object t" and for this function, iterate through each column and see if there is at least one empty
         * space. continue if there is empty space, moving on to check if any value */
        Tile t;
        for (int i = 0; i < 4; i += 1){
            if (b.tile(i, 3) == null){
                /** thank u w3schools for the "continue" syntax */
                continue;
            }
            if (b.tile(i, 3).value() == MAX_PIECE) {
                return true;
            }
        }
        for (int j = 0; j < 4; j += 1){
            if (b.tile(j, 2) == null){
                continue;
            }
            if (b.tile(j, 2).value() == MAX_PIECE){
                return true;
            }
        }
        for (int k = 0; k < 4; k += 1){
            if (b.tile(k, 1) == null){
                continue;
            }
            if (b.tile(k, 1).value() == MAX_PIECE){
                return true;
            }
        }
        for (int l = 0; l < 4; l += 1){
            if (b.tile(l, 0) == null){
                continue;
            }
            if (b.tile(l, 0).value() == MAX_PIECE){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        if (emptySpaceExists(b)) return true;
            for (int i=0; i < b.size(); i++) {
                for (int j=0; j < b.size(); j++) {
                    boolean leftRight = j + 1 < b.size() && b.tile(i, j).value() == b.tile(i, j + 1).value();
                    boolean upDown = i + 1 < b.size() && b.tile(i, j).value() == b.tile(i + 1, j).value();
                    if (upDown || leftRight) {
                        return true;
                    }
                }
            }
            return false;
    }

    /** Returns the model as a string, used for debugging. */
    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    /** Returns whether two models are equal. */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    /** Returns hash code of Model’s string. */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
