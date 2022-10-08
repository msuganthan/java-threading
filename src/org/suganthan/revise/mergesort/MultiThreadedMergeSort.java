package org.suganthan.revise.mergesort;

import java.util.Map;

public class MultiThreadedMergeSort {
    public static void main(String[] args) {

    }
}

class MergeSort {
    private static int SIZE = 25;
    private int[] input = new int[SIZE];
    private int[] scratch = new int[SIZE];

    void mergeSort(final int start, final int end, final int[] input) {
        if (start == end) {
            return;
        }

        final int mid = start + ((end - start) / 2);

        //sort first half
        Thread worker1 = new Thread(() -> mergeSort(start, mid, input));

        //sort second half
        Thread worker2 = new Thread(() -> mergeSort(mid + 1, end, input));

        worker1.start();
        worker2.start();

        try {
            worker1.join();
            worker2.join();
        } catch (InterruptedException ie) {}

        int i = start;
        int j = mid + 1;
        int k;

        for (k = 0; k <= end; k++) {
            scratch[k] = input[k];
        }

        k = start;

        while (k <= end) {

            if (i <= mid && j <= end) {
                input[k] = Math.min(scratch[i], scratch[j]);

                if (input[k] == scratch[i]) {
                    i++;
                } else {
                    j++;
                }
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
