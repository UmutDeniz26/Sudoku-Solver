import java.util.Arrays;
import java.util.Random;

class Solution {

    Random random = new Random();
    long startTime = System.nanoTime();
    static long allProcessTime = System.nanoTime();
    static long measuredTime = 0;
    static long mainTimeCnt = 0;

    public static void main(String[] args) {

        long totalTime = 0;
        int loopCnt = 0;
        int totalLoopCnt = 0;
        int loopCount = 5;
        for (int i = 0; i < loopCount; i++) {

            char[][] board = new char[][] { { '.', '.', '5', '3', '.', '.', '.', '.', '.' },
                    { '8', '.', '.', '.', '.', '.', '.', '2', '.' },
                    { '.', '7', '.', '.', '1', '.', '5', '.', '.' },
                    { '4', '.', '.', '.', '.', '5', '3', '.', '.' },
                    { '.', '1', '.', '.', '7', '.', '.', '.', '6' },
                    { '.', '.', '3', '2', '.', '.', '.', '8', '.' },
                    { '.', '6', '.', '5', '.', '.', '.', '.', '9' },
                    { '.', '.', '4', '.', '.', '.', '.', '3', '.' },
                    { '.', '.', '.', '.', '.', '9', '7', '.', '.' } };
            Solution solution = new Solution();

            long startTime = System.currentTimeMillis();
            loopCnt = solution.solveSudoku(board);
            long endTime = System.currentTimeMillis();

            long elapsedTime = endTime - startTime;
            totalTime += elapsedTime;
            totalLoopCnt += loopCnt;
            System.out.println("Solution " + (i + 1) + "\n");
            System.out.println("Elapsed time: " + elapsedTime + "ms" + "  -  Avg Time: " + totalTime / (i + 1) + "ms" +
                    "  -  Total Time: " + totalTime + "ms");
            System.out.println("Elapsed loop: " + loopCnt + "  " + "  -  Avg loop: " + totalLoopCnt / (i + 1) + "  " +
                    "  -  Total loop: " + totalLoopCnt + "  ");
            System.out.println("loop/time ratio: " + totalLoopCnt / totalTime);
            System.out.println("Histogram of PossMoves: " + Arrays.toString(BestHistogramOfPossibleMoves));
            if (i == loopCount - 1) {
                printBoard(board);

            }

        }
        long allTime = System.nanoTime() - allProcessTime;

        double timeRatio = (double) measuredTime / allTime * 100;
        System.out.println("Time Ratio: %" + timeRatio);

    }

    static int[] histogramOfPossibleMoves = new int[] { 0, 0, 0, 0, 0, 0 };
    static int[] BestHistogramOfPossibleMoves = new int[] { 0, 0, 0, 0, 0, 0 };

    public int solveSudoku(char[][] board) {
        int minDotCounter = 81;
        int dotCounter = 0;
        char[][] first_board = new char[board.length][];
        for (int i = 0; i < board.length; i++) {
            first_board[i] = Arrays.copyOf(board[i], board[i].length);
        }
        int limit = 1;
        boolean[] output = new boolean[] { false, false, false };
        for (int i = 0; i < 500000; i++) {
            if (output[2]) {
                limit = 1;
            }

            if (output[0]) {
                limit += 1;
                output = opportunitiesSearch(board, limit);

            } else {

                output = opportunitiesSearch(board, limit);

            }

            dotCounter = howManyDotLeft(board);
            if (minDotCounter > dotCounter) {
                minDotCounter = dotCounter;
                BestHistogramOfPossibleMoves = histogramOfPossibleMoves;
            }

            // win check
            if (dotCounter == 0) {
                System.out.println("Congratss!!!");
                return i;
            }

            // reset
            if (output[1]) {
                histogramOfPossibleMoves = new int[] { 0, 0, 0, 0, 0, 0 };
                // printBoard(board);
                for (int j = 0; j < first_board.length; j++) {
                    board[j] = Arrays.copyOf(first_board[j], first_board[j].length);
                }
                limit = 1;
                output = new boolean[] { false, false, false };
            }
        }
        System.out.println("Best Score: " + minDotCounter);
        return -1;
    }

    public boolean exploratoryAnalysis(char[][] board, int searchLimit) {
        char element;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                element = board[i][j];
                if (element == '.') {

                    char[] possibleMoves = possibleMovesCalculator(board, j, i);
                    if (possibleMoves.length == searchLimit) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean[] opportunitiesSearch(char[][] board, int minOppLimit) {
        char element;
        boolean increaseLimitFlag = false;
        boolean resetFlag = false;
        boolean resetLimitFlag = false;
        int failCounter = 0;
        int mappingCounter = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                element = board[i][j];
                if (element == '.') {

                    char[] possibleMoves = possibleMovesCalculator(board, j, i);

                    if (possibleMoves.length == 0) {
                        resetFlag = true;
                        break;
                    } else if (possibleMoves.length < minOppLimit + 1) {

                        if (exploratoryAnalysis(board, 1) && minOppLimit != 1) {
                            return new boolean[] { false, false, true };
                        }
                        playMove(board, (9 * i + j), possibleMoves[random.nextInt(possibleMoves.length)]);
                        histogramOfPossibleMoves[possibleMoves.length - 1]++;
                        increaseLimitFlag = false;
                    } else {
                        failCounter++;
                    }
                    mappingCounter++;

                }
            }
        }
        if (failCounter == mappingCounter) {
            increaseLimitFlag = true;
        }
        return new boolean[] { increaseLimitFlag, resetFlag, resetLimitFlag };
    }

    public char[] possibleMovesCalculator(char[][] board, int x, int y) {

        char[] possibleMoves = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        for (int row = 0; row < 9; row++) {
            possibleMoves = removeAnElementFromAnArray(possibleMoves, board[row][x]);
        }
        for (int column = 0; column < 9; column++) {
            possibleMoves = removeAnElementFromAnArray(possibleMoves, board[y][column]);
        }
        for (int Y_position = 0; Y_position <= 2; Y_position++) {
            for (int X_position = 0; X_position <= 2; X_position++) {
                possibleMoves = removeAnElementFromAnArray(possibleMoves,
                        OneDimensionQuery(board, 9 * Y_position + X_position + 3 * (x / 3) + 27 * (y / 3)));
            }
        }
        return possibleMoves;
    }

    public int howManyDotLeft(char[][] board) {
        int counter = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == '.') {
                    counter++;
                }
            }
        }
        return counter;
    }

    public char[] removeAnElementFromAnArray(char[] array, char target) {
        char[] copy = Arrays.copyOf(array, array.length);
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            for (int i = index; i < array.length - 1; i++) {
                array[i] = array[i + 1];
            }
            array = Arrays.copyOf(array, array.length - 1);

            return array;
        } else {
            return copy;
        }
    }

    public static void printBoard(char[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }

    }

    public void playMove(char[][] board, int index, char value) {
        board[(int) (index / 9)][index % 9] = value;
    }

    public char OneDimensionQuery(char[][] board, int index) {
        return board[(int) (index / 9)][index % 9];
    }
}
