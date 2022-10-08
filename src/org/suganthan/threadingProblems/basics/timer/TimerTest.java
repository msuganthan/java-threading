package org.suganthan.threadingProblems.basics.timer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * If a {@link TimerTask} misbehaves and never terminates, all other tasks would
 * not be executed.
 *
 * If a task takes too long to execute, it can block timely execution of other tasks.
 * Say two tasks are submitted and the first is scheduled to execute after 100 ms and
 * the second is scheduled to execute after 500ms. Now if the first task takes 5 minutes
 * to execute then the second task would get delayed by 5 minutes rather than the
 * intended 500ms.
 *
 */
public class TimerTest {
    public static void main(String[] args) throws InterruptedException {
        TimerTask badTask = new TimerTask() {
            @Override
            public void run() {
                while (true);
            }
        };

        TimerTask goodTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Hello I am a well behaved task");
            }
        };

        Timer timer = new Timer();
        timer.schedule(badTask, 100);
        timer.schedule(goodTask, 500);

        /**
         * In the below example, we schedule a task which throws a {@link RuntimeException} and ends up killing the lone
         * worker thread {@link Timer} posses. The subsequent submission of the task report the {@link Timer} is cancelled
         * when in fact the previously submitted task crashed the {@link Timer}
         */
        TimerTask badTask1 = new TimerTask() {

            @Override
            public void run() {
                throw new RuntimeException("Something Bad Happened");
            }
        };

        TimerTask goodTask1 = new TimerTask() {

            @Override
            public void run() {
                System.out.println("Hello I am a well-behaved task");
            }
        };
        timer.schedule(badTask1, 10);
        Thread.sleep(500);
        timer.schedule(goodTask1, 10);
    }
}
