package me.kirkhorn.knut.android_sudoku;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.kirkhorn.knut.android_sudoku.fragments.CellGroupFragment;
import me.kirkhorn.knut.android_sudoku.model.Board;

/**
 * Created by Knut on 19.11.2017.
 */

public class GameActivity extends AppCompatActivity implements CellGroupFragment.OnFragmentInteractionListener {
    private final String TAG = "GameActivity";
    private TextView clickedCell;
    private int clickedGroup;
    private int clickedCellId;
    private Board startBoard;
    private Board currentBoard;
    int textViews[] = new int[]{R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
            R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9, R.id.button_remove};

    Button buttonCheckBoard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        int difficulty = getIntent().getIntExtra("difficulty", 0);
        ArrayList<Board> boards = readGameBoards(difficulty);
        startBoard = chooseRandomBoard(boards);
        currentBoard = new Board();
        currentBoard.copyValues(startBoard.getGameCells());


        for (int textView1 : textViews) {
            TextView textKeyboard = findViewById(textView1);
            textKeyboard.setOnClickListener(v -> {
                int inputNum = 0;
                switch (v.getId()) {
                    case R.id.button_1 : inputNum = 1; break;
                    case R.id.button_2 : inputNum = 2; break;
                    case R.id.button_3 : inputNum = 3; break;
                    case R.id.button_4 : inputNum = 4; break;
                    case R.id.button_5 : inputNum = 5; break;
                    case R.id.button_6 : inputNum = 6; break;
                    case R.id.button_7 : inputNum = 7; break;
                    case R.id.button_8 : inputNum = 8; break;
                    case R.id.button_9 : inputNum = 9; break;
                }

                Intent intent = new Intent();
                intent.putExtra("chosenNumber", inputNum);
               // intent.putExtra("isUnsure", checkBoxChecked);
                setResult(RESULT_OK, intent);
                finish();
            });
        }



        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 1; i < 10; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i-1]);
            thisCellGroupFragment.setGroupId(i);
        }

        //Appear all values from the current board
        CellGroupFragment tempCellGroupFragment;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int column = j / 3;
                int row = i / 3;

                int fragmentNumber = (row * 3) + column;
                tempCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber]);
                int groupColumn = j % 3;
                int groupRow = i % 3;

                int groupPosition = (groupRow * 3) + groupColumn;
                int currentValue = currentBoard.getValue(i, j);

                if (currentValue != 0) {
                    tempCellGroupFragment.setValue(groupPosition, currentValue);
                }
            }
        }
    }

    private ArrayList<Board> readGameBoards(int difficulty) {
        ArrayList<Board> boards = new ArrayList<>();
        int fileId;
        if (difficulty == 1) {
            fileId = R.raw.normal;
        } else if (difficulty == 0) {
            fileId = R.raw.easy;
        } else {
            fileId = R.raw.hard;
        }

        InputStream inputStream = getResources().openRawResource(fileId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            List<String> puzzleList = new ArrayList<>();
            String line = bufferedReader.readLine();

            while (line != null) {
                line = bufferedReader.readLine();
                puzzleList.add(line);
                if (line == null) {
                    break;
                }
            }

            line = puzzleList.get((int)(Math.random()*(puzzleList.size()-1)));
            line = line.replace(".", "0");


                int row = 0;
                int column = 0;
                Board board = new Board();
                // read all lines in the board
                for (int i = 0; i < line.length(); i++) {
                    line.charAt(i);
                    if(i > 0 && i % 9 == 0) {
                        row ++;
                    }
                    column = i % 9;
                    board.setValue(row, column, Integer.parseInt(String.valueOf(line.charAt(i))));
                }
                boards.add(board);
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        //reading from internal storage (/data/data/<package-name>/files)
//        String fileName = "boards-";
//        if (difficulty == 0) {
//            fileName += "easy";
//        } else if (difficulty == 1) {
//            fileName += "normal";
//        } else {
//            fileName += "hard";
//        }
//
//        FileInputStream fileInputStream;
//        try {
//            fileInputStream = this.openFileInput(fileName);
//            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//            BufferedReader internalBufferedReader = new BufferedReader(inputStreamReader);
//            String line = internalBufferedReader.readLine();
//
//            int row = 0;
//            int column = 0;
//            List<String> puzzleList = new ArrayList<>();
//            while (line != null) {
//                line = internalBufferedReader.readLine();
//                puzzleList.add(line);
//                if (line == null) {
//                    break;
//                }
//            }
//
//            internalBufferedReader.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return boards;
    }

    private Board chooseRandomBoard(ArrayList<Board> boards) {
        int randomNumber = (int) (Math.random() * boards.size());
        return boards.get(randomNumber);
    }

    private boolean isStartPiece(int group, int cell) {
        int row = ((group-1)/3)*3 + (cell/3);
        int column = ((group-1)%3)*3 + ((cell)%3);
        return startBoard.getValue(row, column) != 0;
    }

    private boolean checkAllGroups() {
        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 0; i < 9; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i]);
            if (!thisCellGroupFragment.checkGroupCorrect()) {
                return false;
            }
        }
        return true;
    }

    public void onCheckBoardButtonClicked(View view) {
        currentBoard.isBoardCorrect();
        if(checkAllGroups() && currentBoard.isBoardCorrect()) {
            Toast.makeText(this, getString(R.string.board_correct), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.board_incorrect), Toast.LENGTH_SHORT).show();
        }
    }

    public void onGoBackButtonClicked(View view) {
        finish();
    }

    public void onShowInstructionsButtonClicked(View view) {
        Intent intent = new Intent("me.kirkhorn.knut.InstructionsActivity");
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int row = ((clickedGroup-1)/3)*3 + (clickedCellId/3);
            int column = ((clickedGroup-1)%3)*3 + ((clickedCellId)%3);

            buttonCheckBoard = findViewById(R.id.buttonCheckBoard);
            if (data.getBooleanExtra("removePiece", false)) {
                clickedCell.setText("");
                clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell));
                currentBoard.setValue(row, column, 0);
                buttonCheckBoard.setVisibility(View.INVISIBLE);
            } else {
                int number = data.getIntExtra("chosenNumber", 1);
                clickedCell.setText(String.valueOf(number));
                currentBoard.setValue(row, column, number);

                boolean isUnsure = data.getBooleanExtra("isUnsure", false);
                if (isUnsure) {
                    clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell_unsure));
                } else {
                    clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell));
                }

                if (currentBoard.isBoardFull()) {
                    buttonCheckBoard.setVisibility(View.VISIBLE);
                } else {
                    buttonCheckBoard.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onFragmentInteraction(int groupId, int cellId, View view, TextView txt) {
        clickedCell = (TextView) view;
        clickedGroup = groupId;
        clickedCellId = cellId;

        int row = ((clickedGroup-1)/3)*3 + (clickedCellId/3);
        int column = ((clickedGroup-1)%3)*3 + ((clickedCellId)%3);

        Log.i(TAG, "Clicked group " + groupId + ", cell " + cellId);
        if (!isStartPiece(groupId, cellId)) {
            for (int textView1 : textViews) {
                TextView textKeyboard = findViewById(textView1);
                textKeyboard.setOnClickListener(v -> {
                    int inputNum = 0;
                    switch (v.getId()) {
                        case R.id.button_1:
                            inputNum = 1;
                            break;
                        case R.id.button_2:
                            inputNum = 2;
                            break;
                        case R.id.button_3:
                            inputNum = 3;
                            break;
                        case R.id.button_4:
                            inputNum = 4;
                            break;
                        case R.id.button_5:
                            inputNum = 5;
                            break;
                        case R.id.button_6:
                            inputNum = 6;
                            break;
                        case R.id.button_7:
                            inputNum = 7;
                            break;
                        case R.id.button_8:
                            inputNum = 8;
                            break;
                        case R.id.button_9:
                            inputNum = 9;
                            break;
                        case R.id.button_remove:
                            if (!clickedCell.getText().toString().equals("")) {
                                currentBoard.setValue(row, column, 0);
                                clickedCell.setText("");
                                //buttonCheckBoard.setVisibility(View.INVISIBLE);
                            }
                            break;
                    }

                    clickedCell.setText(String.valueOf(inputNum));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell));
                    }
                    currentBoard.setValue(row, column, inputNum);

                });

                }
            } else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell));
            }
                Toast.makeText(this, getString(R.string.start_piece_error), Toast.LENGTH_SHORT).show();
        }
    }
}
