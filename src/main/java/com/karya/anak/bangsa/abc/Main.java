package com.karya.anak.bangsa.abc;

public class Main {

    int maxRun;
    long[] runtimes;
    
    public static void main(String args[]) {
        Main tester = new Main();

        tester.test(4, 50, 1000);
    }

    private void test(int i, int trialLimit, int maxEpoch) {
        int maxLength = i;
        ABC abc;
        abc = new ABC(maxLength);
        abc.setLimit(trialLimit);
        abc.setMaxEpoch(maxEpoch);
        long testStart = System.nanoTime();
        long startTime;
        long endTime;
        long totalTime;
        int fail = 0;
        int success = 0;

        for(int j = 0; j < maxRun; ) {
            startTime = System.nanoTime();
            if(abc.algorithm()) {
                endTime = System.nanoTime();
                totalTime = endTime - startTime;

                System.out.println("Done");
                System.out.println("run "+(j+1));
                System.out.println("time in nanoseconds: "+totalTime);
                System.out.println("Success!");

                runtimes[j] = totalTime;
                j++;
                success++;

            } else {
                fail++;
                System.out.println("Fail!");
            }

            if(fail >= 100) {
                System.out.println("Cannot find solution with these params");
                break;
            }
        }

        System.out.println("Number of Success: " +success);
        System.out.println("Number of failures: "+fail);

        for (long runtime : runtimes) {
            //print runtime summary
            System.out.println(Long.toString(runtime));
        }

        long testEnd = System.nanoTime();
        System.out.println(Long.toString(testStart));
        System.out.println(Long.toString(testEnd));
        System.out.println(Long.toString(testEnd - testStart));

        printRuntimes();
    }

    private void printRuntimes() {
        for(long x: runtimes){
            System.out.println("run with time "+x+" nanoseconds");
        }
    }
}
