
import java.util.*;

public class Sudoku {

    private Cell[][] squareContents;
    private List<Cell> remainingCells;
    private int lastSort;

    /* Used to sort the cells in order of remaining possibilities. */
    private class CellComparator implements Comparator<Cell> {
        public int compare(Cell c1, Cell c2) {
            return c2.digitRuledOutCount - c1.digitRuledOutCount;
        }
    }
    private CellComparator comp = new CellComparator();

    /* Information about a single cell. */
    public class Cell {
        private int x;
        private int y;
        public int inSquare;
        public int[] digitRuledOut;
        public int digitRuledOutCount;
        public int value;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
            this.inSquare = 3 * (x / 3) + (y / 3);
            this.digitRuledOutCount = 9;
            this.digitRuledOut = new int[10];
            this.value = 0;
        }

        /* Rules out digit v in this cell. */
        private boolean ruleOutDigit(int v, int depth) {
            if(value == 0 && digitRuledOut[v] == 0) {
                digitRuledOut[v] = depth;
                if(--digitRuledOutCount == 0) { return true; }
            }
            return false;
        }

        /* Rules in digit v in this cell. */
        private void ruleInDigit(int v, int depth) {
            if(digitRuledOut[v] == depth) {
                digitRuledOut[v] = 0;
                digitRuledOutCount++;
            }
        }

        /* Sets the value of this cell and rules it out in other cells. */
        public boolean setValue(int value, int depth) {
            this.value = value;
            for(int i = 0; i < 9; i++) {
                if(board[x][i].ruleOutDigit(value, depth)) return true;
                if(board[i][y].ruleOutDigit(value, depth)) return true;
                if(squareContents[inSquare][i].ruleOutDigit(value, depth)) return true;
            }
            return false;
        }

        /* Erases the value of this cell, ruling it in in other cells. */
        public void eraseValue(int depth) {
            for(int i = 0; i < 9; i++) {
                board[x][i].ruleInDigit(value, depth);
                board[i][y].ruleInDigit(value, depth);
                squareContents[inSquare][i].ruleInDigit(value, depth);
            }
            this.value = 0;
        }
    }

    public Cell[][] board;
    
    /* Initialize the solver. */
    public boolean solve(int[][] initBoard) {
        board = new Cell[9][9];
        remainingCells = new ArrayList<Cell>();
        squareContents = new Cell[9][9];
        int[] squareCount = new int[9];

        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 9; y++) {
                Cell c = new Cell(x,y);
                board[x][y] = c;
                squareContents[c.inSquare][squareCount[c.inSquare]++] = board[x][y];
            }
        }

        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 9; y++) {
                if(initBoard[x][y] > 0) {
                    board[x][y].setValue(initBoard[x][y], 100);
                }
                else {
                    remainingCells.add(board[x][y]);
                }
            }
        }
        Collections.sort(remainingCells, comp);
        return backtrack(1);       
    }

    /* The backtracking search algorithm. */
    public boolean backtrack(int depth) {
        if(remainingCells.isEmpty()) { return true; }
        if(depth == lastSort + 5 || depth == lastSort - 5) {
            Collections.sort(remainingCells, comp);
            lastSort = depth;
        }
        Cell current = (Cell)remainingCells.remove(remainingCells.size()-1);

        for(int v = 1; v <= 9; v++) {
            if(current.digitRuledOut[v] != 0) continue;
            if(current.setValue(v, depth)) {
                current.eraseValue(depth);
            }
            else {
                if(backtrack(depth + 1)) return true;
                current.eraseValue(depth);
            }
        }

        remainingCells.add(current);
        return false;
    }

    /* A few test boards. */
    public void test() {
        int[][] testBoard = {
            {0,6,0,1,0,4,0,5,0},
            {0,0,8,3,0,5,6,0,0},
            {2,0,0,0,0,0,0,0,1},

            {8,0,0,4,0,7,0,0,6},
            {0,0,6,0,0,0,3,0,0},
            {7,0,0,9,0,1,0,0,4},

            {5,0,0,0,0,0,0,0,2},
            {0,0,7,2,0,6,9,0,0},
            {0,4,0,5,0,8,0,7,0}
        };

        solve(testBoard);
    }

    public void test2() {
        int[][] testBoard = {
            {0,0,0,0,0,0,0,3,8},
            {0,2,3,4,0,8,0,0,0},
            {0,8,0,5,2,0,1,0,9},

            {0,0,0,6,7,4,0,5,0},
            {0,0,0,0,0,0,0,0,0},
            {0,1,0,3,5,9,0,0,0},

            {1,0,5,0,4,7,0,9,0},
            {0,0,0,9,0,2,7,1,0},
            {2,9,0,0,0,0,0,0,0}
        };

        solve(testBoard);
    }

}