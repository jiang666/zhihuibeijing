package com.hao.diyviewpracticedemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.hao.diyviewpracticedemo.view.FadeInTextView;
import com.hao.diyviewpracticedemo.widget.Sudoku;

import androidx.appcompat.app.AppCompatActivity;

public class SudukuActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private FadeInTextView fadeInTextView;
    private Context mContext = this;
    private EditText[][] editTexts = new EditText[9][9];
    private int[][] board = new int[9][9];

    private Sudoku sudoku = new Sudoku(board);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suduku);
        tableLayout = findViewById(R.id.tableLayout);

        initEdits();

        fadeInTextView = findViewById(R.id.tv_anim);

        Button btnGenerate = findViewById(R.id.btn_generate_easy);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sudoku.resetSudoku(0);
                setEditTexts();
            }
        });
        findViewById(R.id.btn_generate_middle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sudoku.resetSudoku(7);
                setEditTexts();

            }
        });
        findViewById(R.id.btn_generate_hard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sudoku.resetSudoku(9);
                setEditTexts();

            }
        });
        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sudoku.ckeckSudoku(board)) {
                    fadeInTextView.setTextString("正确。。").startFadeInAnimation();
                } else {
                    Toast.makeText(mContext, "请验证!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btnAnswer = findViewById(R.id.btn_answer);
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sudoku.solveSudoku(board);
                setEditTexts();
            }
        });
    }

    private void initEdits() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < tableRow.getVirtualChildCount(); j++) {
                editTexts[i][j] = (EditText) tableRow.getVirtualChildAt(j);
                final int row = i;
                final int col = j;
                editTexts[i][j].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String strNum = s.toString();
                        if (strNum.equals("")) {
                            board[row][col] = 0;
                        } else {
                            if (strNum.equals("0")) {
                                Toast.makeText(mContext, "1-9的数字!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();

                            } else {
                                board[row][col] = Integer.parseInt(s.toString());

                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
        }

        setEditTexts();
    }

    private void setEditTexts() {
        for (int i = 0; i < editTexts.length; i++) {
            for (int j = 0; j < editTexts[0].length; j++) {
                if (board[i][j] == 0) {
                    editTexts[i][j].setTextColor(Color.MAGENTA);
                    editTexts[i][j].setText("");
                } else {
                    editTexts[i][j].setTextColor(Color.RED);
                    editTexts[i][j].setText(String.valueOf(board[i][j]));
                }
            }
        }
    }

}
