package com.karya.anak.bangsa.abc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class ABC {
    private int maxLength;
    private int numberFood;
    private int limit;
    private int maxEpooch;
    private int minShuffle;
    private int maxShuffle;

    private Random rand;
    private ArrayList<Honey> foodSources;
    private Honey mHoney;
    private int epoch;

    ABC(int n) {
        maxLength = n;
        int numberBee = 40;
        numberFood = numberBee /2;
        limit = 50;
        maxEpooch = 1000;
        minShuffle = 8;
        maxShuffle = 20;
        mHoney = null;
        epoch = 0;
    }

    boolean algorithm() {
        foodSources = new ArrayList<Honey>();
        ArrayList<Honey> solutions = new ArrayList<Honey>();
        rand = new Random();
        boolean done = false;
        epoch = 0;

        initialize();
        memorizeBestFoodSource();

        while(!done) {
            if(epoch < maxEpooch) {
                if(mHoney.getConflicts() == 0) {
                    done = true;
                }
                sendEmployedBees();
                getFitness();
                calculateProbabilities();
                sendOnlookerBees();
                memorizeBestFoodSource();
                sendScoutBees();

                epoch++;
                System.out.println("Epoch: " + epoch);
            } else {
                done = true;
            }
        }

        if(epoch == maxEpooch) {
            System.out.println("No Solution found");
            done = false;
        }

        System.out.println("done.");
        System.out.println("Completed " + epoch + " epochs.");

        for(Honey h: foodSources) {
            if(h.getConflicts() == 0) {
                System.out.println("SOLUTION");
                solutions.add(h);
                printSolution(h);
                System.out.println("conflicts:"+h.getConflicts());
            }
        }
        return done;
    }

    // Sends to optimize the solution
    private void sendEmployedBees() {
        int neighborBeeIndex;
        Honey currentBee;
        Honey neighborBee;

        for(int i = 0; i < numberFood; i++) {
            //A randomly chosen solution is used in producing a mutant solution of the solution i
            //neighborBee = getRandomNumber(0, Food_Number-1);
            neighborBeeIndex = getExclusiveRandomNumber(numberFood-1, i);
            currentBee = foodSources.get(i);
            neighborBee = foodSources.get(neighborBeeIndex);
            sendToWork(currentBee, neighborBee);
        }
    }

    // Sends the onlooker bees to optimize the solution. Onlooker bees work on the best solutions from the employed bees. best solutions have high selection probability.
    private void sendOnlookerBees() {
        int i = 0;
        int t = 0;
        int neighborBeeIndex;
        Honey currentBee;
        Honey neighborBee;

        while(t < numberFood) {
            currentBee = foodSources.get(i);
            if(rand.nextDouble() < currentBee.getSelectionProbability()) {
                t++;
                neighborBeeIndex = getExclusiveRandomNumber(numberFood-1, i);
                neighborBee = foodSources.get(neighborBeeIndex);
                sendToWork(currentBee, neighborBee);
            }
            i++;
            if(i == numberFood) {
                i = 0;
            }
        }
    }

    private void sendToWork(Honey currentBee, Honey neighborBee) {
        int newValue;
        int tempValue;
        int tempIndex;
        int prevConflicts;
        int currConflicts;
        int parameterToChange;

        //get number of conflicts
        prevConflicts = currentBee.getConflicts();
        parameterToChange = getRandomNumber(0, maxLength-1);
        tempValue = currentBee.getNectar(parameterToChange);
        newValue = (int)(tempValue+(tempValue - neighborBee.getNectar(parameterToChange))*(rand.nextDouble()-0.5)*2);

        if(newValue < 0) {
            newValue = 0;
        }
        if(newValue > maxLength-1) {
            newValue = maxLength-1;
        }

        //get the index of the new value
        tempIndex = currentBee.getIndex(newValue);

        //swap
        currentBee.setNectar(parameterToChange, newValue);
        currentBee.setNectar(tempIndex, tempValue);
        currentBee.computeConflicts();
        currConflicts = currentBee.getConflicts();

        //greedy selection
        if(prevConflicts < currConflicts) {
            //No improvement
            currentBee.setNectar(parameterToChange, tempValue);
            currentBee.setNectar(tempIndex, newValue);
            currentBee.computeConflicts();
            currentBee.setTrials(currentBee.getTrials() + 1);
        } else {
            //improved solution
            currentBee.setTrials(0);
        }

    }

    private void sendScoutBees() {
        Honey currentBee;
        int shuffles;

        for(int i =0; i < numberFood; i++) {
            currentBee = foodSources.get(i);
            if(currentBee.getTrials() >= limit) {
                shuffles = getRandomNumber(minShuffle, maxShuffle);
                for(int j = 0; j < shuffles; j++) {
                    randomlyArrange(i);
                }
                currentBee.computeConflicts();
                currentBee.setTrials(0);

            }
        }
    }

    private void getFitness() {
        // Lowest errors = 100%, Highest errors = 0%
        Honey thisFood;
        double bestScore;
        double worstScore;

        // The worst score would be the one with the highest energy, best would be lowest.
        worstScore = Collections.max(foodSources).getConflicts();

        // Convert to a weighted percentage.
        bestScore = worstScore - Collections.min(foodSources).getConflicts();

        for(int i = 0; i < numberFood; i++) {
            thisFood = foodSources.get(i);
            thisFood.setFitness((worstScore - thisFood.getConflicts()) * 100.0 / bestScore);
        }
    }

    private void calculateProbabilities() {
        Honey thisFood;
        double maxfit = foodSources.get(0).getFitness();

        for(int i = 1; i < numberFood; i++) {
            thisFood = foodSources.get(i);
            if(thisFood.getFitness() > maxfit) {
                maxfit = thisFood.getFitness();
            }
        }

        for(int j = 0; j < numberFood; j++) {
            thisFood = foodSources.get(j);
            thisFood.setSelectionProbability((0.9*(thisFood.getFitness()/maxfit))+0.1);
        }
    }

    //Initializes all of the solutions' placement of queens in ramdom positions.
    private void initialize() {
        int newFoodIndex;
        int shuffles;

        for(int i = 0; i < numberFood; i++) {
            Honey newHoney = new Honey(maxLength);

            foodSources.add(newHoney);
            newFoodIndex = foodSources.indexOf(newHoney);

            shuffles = getRandomNumber(minShuffle, maxShuffle);

            for(int j = 0; j < shuffles; j++) {
                randomlyArrange(newFoodIndex);
            }

            foodSources.get(newFoodIndex).computeConflicts();
        } // i
    }

    private int getRandomNumber(int low, int high) {
        return (int)Math.round((high - low) * rand.nextDouble() + low);
    }

    private int getExclusiveRandomNumber(int high, int except) {
        boolean done = false;
        int getRand = 0;

        while(!done) {
            getRand = rand.nextInt(high);
            if(getRand != except){
                done = true;
            }
        }
        return getRand;
    }

    private void randomlyArrange(int index) {
        int positionA = getRandomNumber(0, maxLength - 1);
        int positionB = getExclusiveRandomNumber(maxLength - 1, positionA);
        Honey thisHoney = foodSources.get(index);
        int temp = thisHoney.getNectar(positionA);
        thisHoney.setNectar(positionA, thisHoney.getNectar(positionB));
        thisHoney.setNectar(positionB, temp);
    }

    // Memorizes the best solution
    private void memorizeBestFoodSource() {
        mHoney = Collections.min(foodSources);
    }

    private void printSolution(Honey solution) {
        String board[][] = new String[maxLength][maxLength];

        // Clear the board.
        for(int x = 0; x < maxLength; x++) {
            for(int y = 0; y < maxLength; y++) {
                board[x][y] = "";
            }
        }

        for(int x = 0; x < maxLength; x++) {
            board[x][solution.getNectar(x)] = "Q";
        }

        // Display the board.
        System.out.println("Board:");
        for(int y = 0; y < maxLength; y++) {
            for(int x = 0; x < maxLength; x++) {
                if(board[x][y].equals("Q")) {
                    System.out.print("Q ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.print("\n");
        }
    }

    // sets the max epoch
    void setMaxEpoch(int newMaxEpoch) {
        this.maxEpooch = newMaxEpoch;
    }

    // sets the limit for trials for all food sources
    void setLimit(int newLimit) {
        this.limit = newLimit;
    }
}
