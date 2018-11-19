package com.imranjuma.maze;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.imranjuma.maze.views.buttonCellControl;

public class MainActivity extends Activity {

    // Here I have the number of cells per row, in the assingnment we needed to have a
    // 10 x width of the device
    private static final int BUTTONS_PER_SQUARE = 10;

    // Here are my margins, this allowed for different allignment taks
    private static final int MARGIN = 1;

    // Here I'll create a layout location for all of my buttons
    private LinearLayout concentrate;

    // My Griding Area
    private buttonCellControl[][] buttonCell;

    // I thought the best way to complete the project would be using
    // Inflation Layout's I found the source here
    // https://developer.android.com/reference/android/view/LayoutInflater.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        concentrate = (LinearLayout) findViewById(R.id.mazeContainer);

        // Here we have a post method that will run the code after we compile
        // Once the post is created this will create our dimentions

        concentrate.post(new Runnable() {
            @Override
            public void run() {
                inflateLayout(concentrate.getWidth(), concentrate.getHeight());
            }
        });
    }

    // Here I will inflate my layout, and see how much free space we have
    private void inflateLayout(int usableWidth, int usableHight) {

        // Calculate the weidth & height for each button by the cell amount see line 11
        int numlength = usableWidth / BUTTONS_PER_SQUARE;
        int numRows = usableHight / numlength;

        // Creating the layout with fixed assets that cannot be changed
        LinearLayout.LayoutParams layoutPrefrences =
                new LinearLayout.LayoutParams(numlength - MARGIN * 2,
                numlength - MARGIN * 2);
        layoutPrefrences.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);

        // Here is my Initialization
        buttonCell = new buttonCellControl[numRows][BUTTONS_PER_SQUARE];
        for (int horizontalFiling = 0; horizontalFiling < numRows; horizontalFiling++) {
            // Here we will create another Linear Layout to display the buttons of the
            // correct row
            LinearLayout tenSectionLayout = new LinearLayout(this);
            tenSectionLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int verticalFiling = 0; verticalFiling < BUTTONS_PER_SQUARE; verticalFiling++) {
                // Here we can create a new cell
                final buttonCellControl cell = new buttonCellControl(this);

                // Here we will get the button for the cell that we require for the layout
                cell.button.setLayoutParams(layoutPrefrences);

                // Adding Click Listener
                cell.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Sending this message to the Cell.Java class
                        cell.onClick();
                    }
                });

                cell.leftToRightPosition = horizontalFiling;
                cell.upToDownPosition = verticalFiling;

                buttonCell[horizontalFiling][verticalFiling] = cell;
                tenSectionLayout.addView(cell.button);
            }
            concentrate.addView(tenSectionLayout);
        }
        // This will make the first cell created to be the begging of the maize
        buttonCell[0][0].setState(buttonCellControl.START_CELL);

        // This will make the last cell the destnation for the
        buttonCell[numRows - 1][BUTTONS_PER_SQUARE - 1].setState(buttonCellControl.END_CELL);
    }

    // Here is my solving methof for how the "Solve Button" will compute
    @SuppressLint("SetTextI18n")
    public void onClickSolve(View view) {

        // If we are currently in a state, where we are moving the position of
        // start point or end point, then this will block the user from
        // Solving the puzzle

        // if selecting source or destination, do not allow user to start solving.
        if (buttonCellControl.selectingStartPoint || buttonCellControl.selectingEndPoint) {
            showToast("You Must First Select A Start & End Point");
            return;
        }

        // If we are currently in progress of solving the puzzle, and you hit the solve
        // button again, this will just restart and reset the term case
        if (buttonCellControl.solvingProcess) {
            buttonCellControl.solvingProcess = false;
            buttonCellControl.selectingEndPoint = false;
            buttonCellControl.selectingStartPoint = false;
            recreate();
            return;
        }

        // Making the solve flag possible
        buttonCellControl.solvingProcess = true;

        Button button = ((Button) view);
        button.setText("Reset Maze");

        // Starting the process of solving the maze
        new Thread(new Runnable() {
            @Override
            public void run() {
                // check if maze can be solved.
                if (solveMaze(buttonCell, buttonCellControl.startingXPosition,
                        buttonCellControl.startingYPosition)) {
                    showToast("Solved Successfully");
                } else {
                    showToast("Solve Not Possible");
                }
            }
        }).start();
    }

    // Here we will show the message to the user for when they have solved or
    // completed the maze
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    // This is the method that will be used when we are trying to solve the maze
    // This method will work only in the event's that have been mentioned below.
    private static boolean solveMaze(
            buttonCellControl[][] myMazeArray, int currentPositionHorizontal,
            int currentPositionVertical) {

        // if solivng process is already running, do nothing and return.
        // this is used to stop previous processing when user presses the reset button.
        if (!buttonCellControl.solvingProcess) {
            return false;
        }

        // If the user has made an error we will return false
        if (currentPositionHorizontal < 0 || currentPositionVertical < 0 ||
                currentPositionHorizontal >= myMazeArray.length ||
                currentPositionVertical >= myMazeArray[0].length) {
            return false;
        }

        // if current position is a wall or already visited one, return false.
        buttonCellControl current = myMazeArray[currentPositionHorizontal]
                [currentPositionVertical];
        if (current.getState() == buttonCellControl.WALL_CELL ||
                current.getState() == buttonCellControl.CELL_PATH) {
            return false;
        }

        // if the destination cell is found, return True, and the maze is complete
        if (current.getState() == buttonCellControl.END_CELL) {
            return true;
        }

        // if the current cell is found to be dead, no need to proceed
        // from here, return false. This means the maze cannot be solved
        if (myMazeArray[currentPositionHorizontal][currentPositionVertical].deadCell) return false;

        // update the state from empty to path.
        myMazeArray[currentPositionHorizontal][currentPositionVertical].
                setState(buttonCellControl.CELL_PATH);


        // Once the maze has been solved, we will have a wait time
        // and once this time happens we will show the user the Path
        // That we have found to solve the maze
        // wait for 100 ms for displaying step by step. I made it 300 to have some delay
        // To not confuse the user.

        // Basically the way the algoirth works is it goes down
        // then if it cant go down anymore it goes right
        // if it cant go right anymore it goes up
        // if it cannot go up anymore it goes left
        // if it cannot do any of the four directions it cannot solve and then returns the message.

        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }

        // This is the first step the algorithm will take
        // Solve the maze in the downwards direction
        if (solveMaze(myMazeArray, currentPositionHorizontal
                + 1, currentPositionVertical)) {
            return true;
        }
        // Once step one is done we are here, step two by
        // Solving the maze in the right most direction
        if (solveMaze(myMazeArray, currentPositionHorizontal,
                currentPositionVertical + 1)) {
            return true;
        }

        // Once step two is done we are here, step three by
        // Solving the maze in the upwards direction
        if (solveMaze(myMazeArray,
                currentPositionHorizontal - 1, currentPositionVertical)) {
            return true;
        }

        // Once step three is done we are here, step four by
        // Solving the maze in the left most direction
        if (solveMaze(myMazeArray, currentPositionHorizontal,
                currentPositionVertical - 1)) {
            return true;
        }

        // The maze has reached a end point an cannot be solved
        myMazeArray[currentPositionHorizontal][currentPositionVertical].
                setState(buttonCellControl.CELL_DEAD_END);
        myMazeArray[currentPositionHorizontal][currentPositionVertical].
                deadCell = true;
        return false;
    }

}