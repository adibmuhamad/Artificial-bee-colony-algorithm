package com.karya.anak.bangsa.abc;

public class Honey implements Comparable<Honey> {

    private int maxLength;
    private int nectar[];
    private int trials;
    private int conflicts;
    private double fitness;
    private double selectionProbability;

    Honey(int size) {
        this.maxLength = size;
        this.nectar = new int[maxLength];
        this.conflicts = 0;
        this.trials = 0;
        this.fitness = 0.0;
        this.selectionProbability = 0.0;
        initNectar();
    }

    private void initNectar() {
        for(int i = 0; i < maxLength; i++) {
            nectar[i] = i;
        }
    }

    public int compareTo(Honey o) {
        return this.conflicts - o.getConflicts();
    }

    void computeConflicts() {
        //compute the number of conflicts to calculate fitness
        String board[][] = new String[maxLength][maxLength];
        int x,y,tempx,tempy;
        //to check for diagonal
        int dx[] = new int[] {-1, 1, -1, 1};
        //paired with dx to check for diagonal
        int dy[] = new int[] {-1, 1, 1, -1};

        boolean done;
        int conflicts = 0;

        board = clearBoard(board);
        board = plotQueens(board);

        // Walk through each of the Queens and compute the number of conflicts.
        for(int i = 0; i < maxLength; i++) {
            x = i;
            y = this.nectar[i];

            // Check diagonals.
            for(int j = 0; j < 4; j++) {
                tempx = x;
                tempy = y;
                done = false;

                while(!done) {
                    tempx += dx[j];
                    tempy += dy[j];

                    if((tempx < 0 || tempx >= maxLength) || (tempy < 0 || tempy >= maxLength)) {
                        done = true;
                    } else {
                        if(board[tempx][tempy].equals("Q")) {
                            conflicts++;
                        }
                    }
                }
            }
        }
        this.conflicts = conflicts;
    }

    //Plots the queens in the board.
    private String[][] plotQueens(String[][] board) {
        for(int i = 0; i < maxLength; i++) {
            board[i][this.nectar[i]] = "Q";
        }
        return board;
    }

    private String[][] clearBoard(String[][] board) {
        // Clear the board.
        for(int i = 0; i < maxLength; i++) {
            for(int j = 0; j < maxLength; j++) {
                board[i][j] = "";
            }
        }
        return board;
    }

    //Gets the conflicts of the Honey
    int getConflicts() {
        return conflicts;
    }
    // Gets the selection probability of the honey.
    double getSelectionProbability() {
        return selectionProbability;
    }

    // sets the selection probability of the honey.
    void setSelectionProbability(double mSelectionProbability) {
        this.selectionProbability = mSelectionProbability;
    }

    // Gets the fitness of a honey.
    double getFitness() {
        return fitness;
    }

    // Sets the fitness of the honey.
    void setFitness(double mFitness) {
        this.fitness = mFitness;
    }

    // Gets the data on a specified index.
    int getNectar(int index) {
        return nectar[index];
    }

    // Gets the index on a specified data
    int getIndex(int value) {
        int k = 0;
        for(; k < maxLength; k++) {
            if(nectar[k] == value) {
                break;
            }
        }
        return k;
    }

    // Sets the data on a specified index.
    void setNectar(int index, int value) {
        this.nectar[index] = value;
    }

    // Gets the number of trials of a solution.
    int getTrials() {
        return trials;
    }

    // Sets the number of trials of a solution.
    void setTrials(int trials) {
        this.trials = trials;
    }
}
