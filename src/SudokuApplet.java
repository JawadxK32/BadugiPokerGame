import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class SudokuApplet extends JApplet{

    private JTextField[][] digits;
    private JButton start;
    private JButton clear;
    private JPanel topPanel;
    private JPanel digitPanel;

    public void init() {
        this.setLayout(new BorderLayout());
        topPanel = new JPanel();
        start = new JButton("Solve");
        clear = new JButton("Clear");
        topPanel.add(start);
        topPanel.add(clear);
        start.addActionListener(new StartListener());
        clear.addActionListener(new ClearListener());
        digitPanel = new JPanel();
        digitPanel.setLayout(new GridLayout(9,9));
        digits = new JTextField[9][9];
        Font f = new Font("Times", Font.BOLD, 24);
        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 9; y++) {
                digits[x][y] = new JTextField(2);
                digits[x][y].setFont(f);
                digits[x][y].setBackground((3 * (x / 3) + (y / 3)) % 2 == 0 ? 
                new Color(200,200,200) : Color.WHITE);
                digitPanel.add(digits[x][y]);
            }
        }
        add(topPanel, BorderLayout.NORTH);
        add(digitPanel, BorderLayout.CENTER);
    }

    private class ClearListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            for(int i = 0; i < 9; i++) {
                for(int j = 0; j < 9; j++) {
                    digits[i][j].setText("");
                }
            }
        }
    }

    private class StartListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            new Thread(new Runnable() {
                    public void run() {
                        int[][] initBoard = new int[9][9];

                        for(int i = 0; i < 9; i++) {
                            for(int j = 0; j < 9; j++) {
                                String s = digits[i][j].getText();
                                if(!s.equals("")) {
                                    int v;
                                    try {
                                        v = Integer.parseInt(s);
                                        if(v < 1 || v > 9) return;
                                    } catch(NumberFormatException e) {
                                        return;
                                    }
                                    initBoard[i][j] = v;
                                }
                            }
                        }

                        start.setText("Busy");
                        Sudoku s = new Sudoku();
                        if(s.solve(initBoard)) {
                            for(int i = 0; i < 9; i++) {
                                for(int j = 0; j < 9; j++) {
                                    digits[i][j].setText("" + s.board[i][j].value);
                                }
                            }
                        }
                        start.setText("Solve");
                    }
                }).start();
        }
    }
}