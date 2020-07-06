package org.suganthan.multiThreadedMergeSort;

import java.util.Random;

public class Demonstration {
    private static int SIZE = 25;
    private static Random random = new Random(System.currentTimeMillis());
    private static int[] input = new int[SIZE];

    static private void createTestData() {
        for (int i = 0; i < SIZE; i++) {
            input[i] = random.nextInt(10000);
        }
    }

    static private void printArray(int[] input) {
        System.out.println();
        for (int i = 0; i < input.length; i++)
            System.out.print(" " + input[i] + " ");
        System.out.println();
    }

    public static void main( String args[] ) {
        createTestData();

        System.out.println("Unsorted Array");
        printArray(input);
        long start = System.currentTimeMillis();
        (new MultiThreadedMergeSort()).mergeSort(0, input.length - 1, input);
        long end = System.currentTimeMillis();
        System.out.println("\n\nTime taken to sort = " + (end - start) + " milliseconds");
        System.out.println("Sorted Array");
        printArray(input);
    }
}

class MultiThreadedMergeSort {
    private static int SIZE = 25;
    private int[] input = new int[SIZE];
    private int[] scratch = new int[SIZE];

    void mergeSort(final int start, final int end, final int[] input) {
        if (start == end)
            return;

        final int mid = start + ((end - start) / 2);

        Thread worker1 = new Thread(new Runnable() {
            @Override
            public void run() {
                mergeSort(start, mid, input);
            }
        });

        Thread worker2 = new Thread(new Runnable() {
            @Override
            public void run() {
                mergeSort(mid + 1, end, input);
            }
        });

        worker1.start();
        worker2.start();

        try {
            worker1.join();
            worker2.join();
        } catch (InterruptedException interruptedException) {
            //swallow
        }

        int i = start;
        int j = mid + 1;
        int k;

        for (k = start; k <= end; k++)
            scratch[k] = input[k];

        k = start;

        while (k <= end) {
            if( i <= mid && j <= end) {
                input[k] = Math.min(scratch[i], scratch[j]);
                if (input[k] == scratch[i])
                    i++;
                else
                    j++;
            } else if (i <= mid && j > end) {
                input[k] = scratch[i];
                i++;
            } else {
                input[k] = scratch[j];
                j++;
            }
            k++;
        }

    }
}
