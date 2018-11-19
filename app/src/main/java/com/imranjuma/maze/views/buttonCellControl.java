package com.imranjuma.maze.views;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;

public class buttonCellControl {

    // Different positions that the maze can come in contact with
    public static final int EMPTY_CELL = 0;
    public static final int WALL_CELL = 1;
    public static final int START_CELL = 2;
    public static final int END_CELL = 3;
    public static final int CELL_PATH = 4;
    public static final int CELL_DEAD_END = 5;

    // The main button element, each cell is a button
    public Button button;

    // Different flag varibles used to terermin what is going on in the application
    // This can be seen as selection Start Cell, selecting End Cell, and Solving process
    public static boolean selectingStartPoint, selectingEndPoint, solvingProcess;

    // Here we have the start and end points of the Start and End cell.
    public static int startingXPosition, startingYPosition;

    // The current location, also seen as the X and Y points of the cell instance
    public int leftToRightPosition, upToDownPosition;

    // The current state of the cell in potision
    private int cellAt = EMPTY_CELL;

    // Flag placed if the cell is dead
    public boolean deadCell = false;

    public buttonCellControl(Context context) {
        button = new Button(context);
        button.setPadding(0, 0, 0, 0);
        button.setTextColor(Color.WHITE);
        redraw();
    }

    // called when the button is clicked.
    public void onClick() {
        // If we are in process, don't do anything
        // Just return
        if (solvingProcess) return;

        // Actions when clicking on the Start Cell
        if (cellAt == START_CELL) {

            // If the user places the end of the maze and the start of the maze at the same point,
            // don't allow and post this message
            if (selectingEndPoint) {
                Toast.makeText(button.getContext(), "The Start & End Of The Maze Cannot Be The Same.", Toast.LENGTH_SHORT).show();
                return;
            }

            setState(WALL_CELL);
            Toast.makeText(button.getContext(), "Click On The Start Cell.", Toast.LENGTH_SHORT).show();
            buttonCellControl.selectingStartPoint = true;

        } else if (cellAt == END_CELL) {

            // If the user places the start of the maze and the end of the maze at the same point,
            // don't allow and post this message
            if (selectingStartPoint) {
                Toast.makeText(button.getContext(), "The Start & End Of The Maze Cannot Be The Same.", Toast.LENGTH_SHORT).show();
                return;
            }

            setState(WALL_CELL);
            Toast.makeText(button.getContext(), "Click On the Exit Cell.", Toast.LENGTH_SHORT).show();
            buttonCellControl.selectingEndPoint = true;

        } else if (selectingStartPoint) {

            // When moving the Start Cell, update it when moved
            setState(START_CELL);
            selectingStartPoint = false;
            selectingEndPoint = false;

        } else if (selectingEndPoint) {

            // When moving the final destination cell, update it here.
            setState(END_CELL);
            selectingStartPoint = false;
            selectingEndPoint = false;

        } else {
            // Toggle the empty cell
            switch (cellAt) {
                case EMPTY_CELL:
                    setState(WALL_CELL);
                    break;
                case WALL_CELL:
                    setState(EMPTY_CELL);
                    break;
            }
        }
    }

    // refresh the button's background and text after updating the state.
    // Here we will update the buttons background and what message it will display,
    // Thus when the maze is being solved it will display a X and when the Maze is being built
    // The Start Maze, will be Green and show S for start, and the End will show red and be E for End

    private void redraw() {
        // We will first clear the background and any text that is on
        button.setBackgroundColor(Color.LTGRAY);
        button.setText(null);

        // Once the above step is complete we will add our background and text with the right state
        switch (cellAt) {
            case EMPTY_CELL:
                break;
            case WALL_CELL:
                button.setBackgroundColor(Color.BLACK);
                break;
            case START_CELL:
                button.setBackgroundColor(Color.GREEN);
                break;
            case END_CELL:
                button.setBackgroundColor(Color.RED);
                break;
            case CELL_DEAD_END:
                button.setBackgroundColor(Color.rgb(150,25,14)); // Darker Red
                break;
            case CELL_PATH:
                button.setBackgroundColor(Color.LTGRAY);
                button.setText("X");
                break;
        }

        if (cellAt == START_CELL || cellAt == END_CELL) {
            button.setText(cellAt == START_CELL ? "Start" : "End");
        }
    }

    // Updating the State Cell
    public void setState(int state) {
        this.cellAt = state;
        if (state == START_CELL) {
            startingXPosition = leftToRightPosition;
            startingYPosition = upToDownPosition;
        }
        button.post(new Runnable() {
            @Override
            public void run() {
                redraw();
            }
        });
    }

    // Here we will get the State of the cell
    public int getState() {
        return cellAt;
    }

}
