import java.awt.*;
import java.applet.*;
import java.awt.event.*;

public class Connect4 extends Applet
{
    /* Possible piece values in the game field. */
    private final int EMPTY = 0;
    private final int PLAYER1 = 1;
    private final int PLAYER2 = 2;

    /* Coordinates for drawing the game field. */
    private int XOFFSET = 50;
    private int YOFFSET = 50;
    private int XSIZE = 30;
    private int YSIZE = 30;

    /* Dimensions of the game field. */
    private int WIDTH = 8;
    private int HEIGHT = 8;
    private int MAXDEPTH = 5;
    private int SEARCHSTARTCOLUMN;
    
    /* The current game field. Since each move just adds a piece on the field and does not change
     * anything else, we can use the same array throughout the whole minimax search instead of having
     * to create a new array for each state.
     */
    private int[][] field;
    /* The array that tells if a piece is real or just put there by the minimax search. */
    private boolean[][] reallyHasAPiece;
    /* Current height of each column. */
    private int[] columnHeight;
    /* Tells whose turn it is to move next. */
    private boolean isHumansTurn;
    /* Tells whether the game is still on. */
    private boolean gameOn;
    /* Tells whether the game should restart. */
    private boolean humanWantsRestart;
    /* The coordinates of the most recent move. */
    private int mrX, mrY;
    /* The AWT components used by the game. */
    private Button restart;
    private Choice maxDepth, width, height, whoStarts;

    /* Counter for evaluating the usefulness of alpha-beta search. */
    private int abCutoffCount;
    private boolean isAlive;
    
    /** Starts a new game. */
    public void startGame() {
        /* Read the game parameters from the choice boxes. */
        WIDTH = width.getSelectedIndex() + 5;
        HEIGHT = height.getSelectedIndex() + 5;
        MAXDEPTH = maxDepth.getSelectedIndex() + 4;
        
        /* Calculate the rest of the game parameters from those. */
        XSIZE = (getWidth() - 100) / WIDTH;
        YSIZE = (getHeight() - 100) / HEIGHT;
        if(XSIZE > YSIZE) { XSIZE = YSIZE; }
        if(YSIZE > XSIZE) { YSIZE = XSIZE; }
        XOFFSET = (getWidth() - XSIZE * WIDTH) /2;
        YOFFSET = (getHeight() - YSIZE * HEIGHT) /2;
        SEARCHSTARTCOLUMN = WIDTH / 2;    
        
        /* Initialize the object fields. */
        field = new int[WIDTH][HEIGHT];
        reallyHasAPiece = new boolean[WIDTH][HEIGHT];
        columnHeight = new int[WIDTH];
        gameOn = true;
        humanWantsRestart = false;
        if(whoStarts.getSelectedIndex() == 0) {
            mrX = -1; mrY = -1;
        }
        else {
            mrX = SEARCHSTARTCOLUMN;
            mrY = 0;
            field[mrX][mrY] = PLAYER2;
            reallyHasAPiece[mrX][mrY] = true;
            columnHeight[mrX] = 1;
        }
        isHumansTurn = true;        
        repaint();
    }
    
    /** Initialize the applet. */
    public void init() {
        isAlive = true;

        /* Initialize the components and event listeners. */
        this.addMouseListener(new MyMouseListener());
        restart = new Button("Restart");
        this.add(restart);
        restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                humanWantsRestart = true;
            }
        });
        maxDepth = new Choice(); add(maxDepth);
        for(int i = 4; i < 10; i++) { maxDepth.add(i + " ply"); }
        maxDepth.select("" + MAXDEPTH);
        maxDepth.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                MAXDEPTH = maxDepth.getSelectedIndex() + 4;
            }
        });
        width = new Choice(); add(width);
        for(int i = 5; i < 20; i++) { width.add(i + ""); }
        width.select("" + WIDTH);
        height = new Choice(); add(height);
        for(int i = 5; i < 20; i++) { height.add(i + ""); }
        height.select("" + HEIGHT);
        whoStarts = new Choice(); add(whoStarts);
        whoStarts.add("Human first");
        whoStarts.add("Computer first");

        /* Finish up the initialization. */
        startGame();
        (new Thread(new BackgroundThread())).start();
    }
    
    private class BackgroundThread implements Runnable {
        public void run() {
            while(isAlive) {
                try {
                    Thread.sleep(100);
                } catch(InterruptedException e) {}
                if(humanWantsRestart) { startGame(); }
                if(gameOn && !isHumansTurn) { makeComputerMove(); }
            }
        }
    }

    /**
     * Render the current game state on AWT Graphics component.
     * @param g The Graphics component to render the game on.
     */
    public void paint(Graphics g) {
        for(int i = 0; i < WIDTH; i++) {
            for(int j = 0; j < HEIGHT; j++) {
                if(gameOn) {
                    if(isHumansTurn && j == columnHeight[i] && longestRow(i,j,PLAYER2) > 3) {
                        g.setColor(Color.BLUE);
                    }
                    else {
                        g.setColor(new Color(220,220,220));
                    }
                    g.drawOval(XOFFSET + XSIZE * i + 2, YOFFSET + YSIZE * (HEIGHT - j - 1) + 2, XSIZE - 4, YSIZE - 4);
                }
                else {
                    g.setColor(Color.BLACK);
                    g.fillOval(XOFFSET + XSIZE * i + 2, YOFFSET + YSIZE * (HEIGHT - j - 1) + 2, XSIZE - 4, YSIZE - 4);
                }
            }
        }
        
        /* Draw the current playing field. */
        for(int i = 0; i < WIDTH; i++) {
            for(int j = 0; j < HEIGHT; j++) {
                if(reallyHasAPiece[i][j]) {
                    /* Draw the piece. */
                    if(field[i][j] == PLAYER1) { g.setColor(Color.RED); }
                    if(field[i][j] == PLAYER2) { g.setColor(Color.GREEN); }
                    g.fillOval(XOFFSET + XSIZE * i, YOFFSET + YSIZE * (HEIGHT - j - 1), XSIZE, YSIZE);
                    g.setColor(Color.BLACK);
                    /* Mark the most recent move. */
                    if(i == mrX && j == mrY) {
                        g.fillOval(XOFFSET + XSIZE * i + XSIZE / 3, YOFFSET + YSIZE * (HEIGHT - j - 1) + YSIZE / 3, XSIZE/3, YSIZE/3);
                    }
                    /* Draw the black border around the piece. */
                    g.drawOval(XOFFSET + XSIZE * i, YOFFSET + YSIZE * (HEIGHT - j - 1), XSIZE, YSIZE);                    
                }
            }
        }
    }
    
    /* Computes the length of the longest row of pieces of the player colour that emanates from
     *  the coordinates (x,y), with the assumption that (x,y) contains such a piece. If the length
     *  of a row is less than 4 and it is blocked from both ends, it only counts as 1.
     */
    private int longestRow(int x, int y, int player) {
        int longest = 1;
        int currentLen, j;
        boolean blocked = false;

        /* Check to E and W. */
        currentLen = 1;
        j = -1;
        while(x + j >= 0 && field[x + j][y] == player) {
            j--;
            currentLen++;
        }
        blocked = (x + j < 0 || field[x + j][y] != EMPTY);
        j = +1;
        while(x + j < WIDTH && field[x + j][y] == player) {
            j++;
            currentLen++;
        }
        if(currentLen < 4 && blocked && (x + j == WIDTH || field[x + j][y] != EMPTY)) { currentLen = 1; }
        if(currentLen > longest) { longest = currentLen; }
        
        /* Check to SW and NE. */
        blocked = false;
        currentLen = 1;
        j = -1;
        while(x + j >= 0 && y + j >= 0 && field[x + j][y + j] == player) {
            j--;
            currentLen++;
        }
        blocked = (x + j < 0 || y + j < 0 || field[x + j][y + j] != EMPTY);
        j = +1;
        while(x + j < WIDTH && y + j < HEIGHT && field[x + j][y + j] == player) {
            j++;
            currentLen++;
        }
        if(currentLen < 4 && blocked && (x + j == WIDTH || y + j == HEIGHT || field[x + j][y + j] != EMPTY)) { currentLen = 1; }
        if(currentLen > longest) { longest = currentLen; }
        
        /* Check to SE and NW. */
        blocked = false;
        currentLen = 1;
        j = -1;
        while(x + j >= 0 && y - j < HEIGHT && field[x + j][y - j] == player) {
            j--;
            currentLen++;
        }
        blocked = (x + j < 0 || y - j == HEIGHT || field[x + j][y - j] != EMPTY);
        j = +1;
        while(x + j < WIDTH && y - j >= 0 && field[x + j][y - j] == player) {
            j++;
            currentLen++;
        }
        if(currentLen < 4 && blocked && (x + j == WIDTH || y - j < 0 || field[x + j][y - j] != EMPTY)) { currentLen = 1; }
        if(currentLen > longest) { longest = currentLen; }

        /* Check to S. */
        currentLen = 1;
        j = -1;
        while(y + j >= 0 && field[x][y + j] == player) {
            j--;
            currentLen++;
        }
        if(currentLen < 4 && (y + j < 0 || field[x][y + j] != EMPTY)) { currentLen = 1; }
        if(currentLen > longest) { longest = currentLen; }
        
        /* For the purposes of this game, more than 4 is the same as exactly 4. */
        if(longest > 4) { longest = 4; }
        return longest;
    }
    
    /** 
     * The static evaluation function of the minimax algorithm. Evaluates the situation
     * assuming it is the turn of the player passed as the parameter.
     * @param HERO The player who is the hero of this evaluation.
     * @return An estimate of winning probability for hero in this state.
     */
    public double estimateValue(int HERO) {
        int VILLAIN = 3 - HERO;
        int forHero = 0, forVillain = 0;
        int max;
        
        /* Check if HERO has an immediate win. If he does, that's it. */
        for(int x = 0; x < WIDTH; x++) {
            if(columnHeight[x] < HEIGHT) {
                if(longestRow(x, columnHeight[x], HERO) > 3) { return +1.0; }
            }
        }

        /* So, no immediate win for HERO. Estimate the value of the state locally. */
        for(int x = 0; x < WIDTH; x++) {
            if(columnHeight[x] < HEIGHT) {

                /* See what HERO could get from this column. */
                max = longestRow(x, columnHeight[x], HERO);
                forHero += max*max*max;

                /* See what VILLAIN could get from this column. */                
                max = longestRow(x, columnHeight[x], VILLAIN);
                if(max > 3) { // Immediate win for VILLAIN, so this becomes a forced move for HERO.               
                    field[x][columnHeight[x]++] = HERO;
                    double value = estimateValue(VILLAIN);
                    field[x][--columnHeight[x]] = EMPTY;
                    return value;
                }
                else { forVillain += max*max*max; }                
            }
        }
        
        if(forHero == 0 && forVillain == 0) { return 0.5; }
        return (double)forHero / (forHero + forVillain);
    }

    /**
     * The dynamic negamax evaluation of game state.
     * @param depth The depth limit remaining in this recursion.
     * @param alpha The value of the current best move found for hero.
     * @param beta The value of the current best move found for villain.
     * @param HERO The player who is the hero in this evaluation.
     * @param VILLAIN The player who is the villain in this evaluation.
     * @return Estimate for the winning probability for hero.
     */
    public double negaMax(int depth, double alpha, double beta, int HERO, int VILLAIN) {
        /* Check for search depth limit cutoff. */
        if(depth < 1) { return estimateValue(HERO); }
        
        boolean freeColumnExists = false;
        int sign = +1;
        int i = SEARCHSTARTCOLUMN;
        for(int j = 0; j < WIDTH; j++) {
            /* Compute the next column to search. */
            i = i + sign*j;
            sign = -sign;
            if(columnHeight[i] < HEIGHT) {
                /* HERO could move here, so search to see what happens if he does. */
                freeColumnExists = true;
                /* Check if moving here is an instant win. */
                if(longestRow(i, columnHeight[i], HERO) > 3) { return 1.0; }
                /* Move here (but it's not a real move yet) and search forward. */
                field[i][columnHeight[i]++] = HERO;                  
                double value = 1 - negaMax(depth - 1, 1 - beta, 1 - alpha, VILLAIN, HERO);
                field[i][--columnHeight[i]] = EMPTY;
                /* Compare the value of the move to the current alpha and update if necessary. */
                if(value > alpha) { alpha = value; }
                /* Cut off the search if it is certain that VILLAIN will not allow a move here. */
                if(alpha >= beta) { abCutoffCount++; return beta; }
            }            
        }
        return freeColumnExists ? alpha : 0.5 ;
    }
    
    /** 
     * Makes the best move for the computer from the current situation.
     */
    public void makeComputerMove() {
        /* Initialize the search variables. */
        abCutoffCount = 0;
        int bestMove = -1;
        int sign = +1;
        double alpha = 0; // Value of best move found so far
        int col = SEARCHSTARTCOLUMN;
        for(int j = 0; j < WIDTH; j++) {
            /* Compute the next column to search. */
            col = col + sign*j;
            sign = -sign;
            if(columnHeight[col] < HEIGHT) {
                showStatus("Examining column " + col);
                /* Computer could move here, so let's see what happens if he does. */
                
                /* Ensure that after the loop, bestMove has a column which it is legal to move in. */
                if(bestMove == -1) { bestMove = col; }
                /* Check if this column is an immediate win. */
                if(longestRow(col, columnHeight[col], PLAYER2) > 3) {
                    bestMove = col;
                    break;
                }
                /* Move here (but it's not a real move yet) and search forward. */ 
                field[col][columnHeight[col]++] = PLAYER2;
                /* Evaluate the position from human's point of view and negate the result. */
                double value = 1 - negaMax(MAXDEPTH, 0, 1 - alpha, PLAYER1, PLAYER2);
                /* Undo the move. */
                field[col][--columnHeight[col]] = EMPTY;
                /* Update the values of beta and bestMove. */
                if(value > alpha) {
                    alpha = value;
                    bestMove = col;
                }
            }            
        }
        showStatus("Search finished with " + abCutoffCount + " a/b cutoffs.");
        /* If there is a possible move, make the best move found in the search. */
        if(bestMove != -1) {
            mrX = bestMove;
            mrY = columnHeight[bestMove];
            reallyHasAPiece[mrX][mrY] = true;
            if(longestRow(mrX, mrY, PLAYER2) > 3) {
                gameOn = false;
            }
            field[mrX][mrY] = PLAYER2;
            columnHeight[mrX]++;
            isHumansTurn = true;
            repaint();
        }
        else {
            /* Cannot make any move, so the game ended with a draw. */
            gameOn = false;
        }
    }

    private class MyMouseListener extends MouseAdapter {
        /** React to the mouse click inside the field. */
        public void mousePressed(MouseEvent me) {
            /* Check if the player is allowed to make a move. */
            if(!isHumansTurn || !gameOn) return;
            
            /* Get the mouse click coordinates and convert them to grid coordinates. */
            int x = me.getX(); int y = me.getY();
            if(x < XOFFSET || y < YOFFSET) return;
            x = (x - XOFFSET) / XSIZE;
            y = (y - YOFFSET) / YSIZE;
            if(y < 0 || y >= HEIGHT || x >= WIDTH) return;
            if(columnHeight[x] >= HEIGHT) return;
            
            /* Make a move to the column that was clicked. */
            mrX = x;
            mrY = columnHeight[x];
            reallyHasAPiece[mrX][mrY] = true;        
            if(longestRow(mrX, mrY, PLAYER1) > 3) { gameOn = false; }
            isHumansTurn = false;
            field[x][columnHeight[x]++] = PLAYER1;
            repaint();
        }
    }

    /** The method that is called when the applet is terminated. */
    public void destroy() {
        isAlive = false;    
    }

}
