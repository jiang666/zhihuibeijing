package com.hao.diyviewpracticedemo.widget;

import java.util.ArrayList;
import java.util.Random;

/**
 * 数独类
 */
public class Sudoku {

    private int[][] board;

    /**
     * 数独终盘的种子
     */
    private int[][] seedArray = {
            {9, 7, 8, 3, 1, 2, 6, 4, 5},
            {3, 1, 2, 6, 4, 5, 9, 7, 8},
            {6, 4, 5, 9, 7, 8, 3, 1, 2},
            {7, 8, 9, 1, 2, 3, 4, 5, 6},
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {4, 5, 6, 7, 8, 9, 1, 2, 3},
            {8, 9, 7, 2, 3, 1, 5, 6, 4},
            {2, 3, 1, 5, 6, 4, 8, 9, 7},
            {5, 6, 4, 8, 9, 7, 2, 3, 1}
    };


    public Sudoku(int[][] origin){
        this.board = origin;
    }

    /**
     * 解数独，回溯法
     * @param board 数独矩阵
     * @return true 数独有解
     */
    public boolean solveSudoku(int[][] board) {
        boolean[][] rows = new boolean[9][10];
        boolean[][] cols = new boolean[9][10];
        boolean[][] boxes = new boolean[9][10];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    int num = board[i][j];
                    rows[i][num] = true;
                    cols[j][num] = true;
                    boxes[(i / 3) * 3 + j / 3][num] = true;
                }
            }
        }
        return bactTrack(board, rows, cols, boxes, 0, 0);

    }

    private boolean bactTrack(int[][] board, boolean[][] rows, boolean[][] cols, boolean[][] boxes, int i, int j) {
        if (j == board[0].length) {
            j = 0;
            i++;
            if (i == board.length) {
                return true;
            }
        }
        if (board[i][j] == 0) {
            for (int num = 1; num <= 9; num++) {
                int boxIndex = (i / 3) * 3 + j / 3;
                boolean isUesd = rows[i][num] || cols[j][num] || boxes[boxIndex][num];
                if (!isUesd) {
                    rows[i][num] = true;
                    cols[j][num] = true;
                    boxes[boxIndex][num] = true;

                    board[i][j] = num;

                    if (bactTrack(board, rows, cols, boxes, i, j + 1)) {
                        return true;
                    }

                    board[i][j] = 0;
                    rows[i][num] = false;
                    cols[j][num] = false;
                    boxes[boxIndex][num] = false;

                }
            }
        } else {
            return bactTrack(board, rows, cols, boxes, i, j + 1);
        }

        return false;
    }

    /**
     * 数独终盘生成
     * @param seedArray 数组终盘的种子
     */
    private void creatSudokuArray(int[][] seedArray) {
        //产生一个1-9的不重复长度为9的一维数组
        ArrayList<Integer> randomList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            int randomNum = random.nextInt(9) + 1;
            while (true) {
                if (!randomList.contains(randomNum)) {
                    randomList.add(randomNum);
                    break;
                }
                randomNum = random.nextInt(9) + 1;
            }
        }

        /*
          通过一维数组和原数组生成随机的数独矩阵
          遍历二维数组里的数据，在一维数组找到当前值的位置，并把一维数组
          当前位置加一处位置的值赋到当前二维数组中。目的就是将一维数组为
          依据，按照随机产生的顺序，将这个9个数据进行循环交换，生成一个随
          机的数独矩阵。
         */

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    if (seedArray[i][j] == randomList.get(k)) {
                        seedArray[i][j] = randomList.get((k + 1) % 9);
                        break;
                    }
                }
                board[i][j] = seedArray[i][j];
            }
        }
    }

    /**
     * 给指定数独终盘挖空，利用随机数，随机次数为level，也就是对每行挖level次空
     * 难度对应easy--5, middle--7, hard--9
     * 每次挖空后判断是否有解，若无解重新挖空
     * @param level 挖空等级
     */
    public void resetSudoku(int level) {
        int[][] copies = new int[board.length][board[0].length];
        if (level == 0) {
            level = 5;
        }
        creatSudokuArray(seedArray);
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < level; j++) {
                int ran = random.nextInt(9);
                board[i][ran] = 0;
            }
        }
        if (!solveSudoku(copies)) {
            resetSudoku(level);
        }
    }

    /**
     * 检验数独是否是有效的，思想和求解一样
     */
    public boolean ckeckSudoku(int[][] board) {
        boolean[][] rows = new boolean[9][9];
        boolean[][] cols = new boolean[9][9];
        boolean[][] boxes = new boolean[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int num = board[i][j];
                if (num != 0) {
                    int boxIndex = (i / 3) * 3 + j / 3;
                    int numIndex = num - 1;
                    if (rows[i][numIndex] || cols[j][numIndex] || boxes[boxIndex][numIndex]) {
                        return false;
                    } else {
                        rows[i][numIndex] = cols[j][numIndex] = boxes[boxIndex][numIndex] = true;
                    }
                }
            }
        }
        return true;
    }
}
