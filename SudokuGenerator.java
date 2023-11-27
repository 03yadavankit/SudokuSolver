import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SudokuGenerator extends JPanel {

    private static final int SIZE = 9;
    private static final int CELL_SIZE = 35;
    private static final int TOP_LEFT_X = 50;
    private static final int TOP_LEFT_Y = 50;
    private static final int SLEEP_TIME = 1000;

    private int[][] grid;
    private List<Integer> numberList;
    private int counter;

    public SudokuGenerator() {
        grid = new int[SIZE][SIZE];
        numberList = new ArrayList<>();
        for (int i = 1; i <= SIZE; i++) {
            numberList.add(i);
        }
        counter = 0;
        Collections.shuffle(numberList);
        fillGrid();
    }

    private void fillGrid() {
        if (fillGridRecursively()) {
            drawGrid();
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            shuffleGrid();
            removeNumbers();
            drawGrid();
        }
    }

    private boolean fillGridRecursively() {
        int i = 0;
        while (i < SIZE * SIZE) {
            int row = i / SIZE;
            int col = i % SIZE;
            if (grid[row][col] == 0) {
                Collections.shuffle(numberList);
                for (int value : numberList) {
                    if (isValidMove(row, col, value)) {
                        grid[row][col] = value;
                        if (checkGrid() && fillGridRecursively()) {
                            return true;
                        }
                        grid[row][col] = 0;
                    }
                }
                return false;
            }
            i++;
        }
        return true;
    }

    private boolean isValidMove(int row, int col, int value) {
        return !isInRow(row, value) && !isInColumn(col, value) && !isInSubgrid(row - row % 3, col - col % 3, value);
    }

    private boolean isInRow(int row, int value) {
        for (int col = 0; col < SIZE; col++) {
            if (grid[row][col] == value) {
                return true;
            }
        }
        return false;
    }

    private boolean isInColumn(int col, int value) {
        for (int row = 0; row < SIZE; row++) {
            if (grid[row][col] == value) {
                return true;
            }
        }
        return false;
    }

    private boolean isInSubgrid(int startRow, int startCol, int value) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (grid[startRow + row][startCol + col] == value) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void shuffleGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = 0;
            }
        }
        fillGridRecursively();
    }

    private void removeNumbers() {
        int attempts = 5;
        while (attempts > 0) {
            int row = (int) (Math.random() * SIZE);
            int col = (int) (Math.random() * SIZE);
            while (grid[row][col] == 0) {
                row = (int) (Math.random() * SIZE);
                col = (int) (Math.random() * SIZE);
            }
            int backup = grid[row][col];
            grid[row][col] = 0;

            int[][] copyGrid = new int[SIZE][SIZE];
            for (int r = 0; r < SIZE; r++) {
                System.arraycopy(grid[r], 0, copyGrid[r], 0, SIZE);
            }

            counter = 0;
            solveGrid(copyGrid);

            if (counter != 1) {
                grid[row][col] = backup;
                attempts--;
            }
        }
    }

    private void solveGrid(int[][] grid) {
        fillGridRecursively(grid);
    }

    private void drawGrid() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSudokuGrid(g);
    }

    private void drawSudokuGrid(Graphics g) {
        int x = TOP_LEFT_X;
        int y = TOP_LEFT_Y;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 18));

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] != 0) {
                    g.drawString(Integer.toString(grid[row][col]), x + col * CELL_SIZE + 9, y - row * CELL_SIZE - CELL_SIZE + 8);
                }
            }
        }

        for (int i = 0; i < SIZE; i++) {
            if (i % 3 == 0) {
                g.setColor(Color.BLACK);
                g.drawLine(x, y - i * CELL_SIZE, x + SIZE * CELL_SIZE, y - i * CELL_SIZE);
                g.drawLine(x + i * CELL_SIZE, y, x + i * CELL_SIZE, y - SIZE * CELL_SIZE);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine(x, y - i * CELL_SIZE, x + SIZE * CELL_SIZE, y - i * CELL_SIZE);
                g.drawLine(x + i * CELL_SIZE, y, x + i * CELL_SIZE, y - SIZE * CELL_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sudoku Generator");
        SudokuGenerator sudokuGenerator = new SudokuGenerator();
        frame.add(sudokuGenerator);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
