package net.benorourke.stocks.examples.task;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.exception.TaskStartException;
import net.benorourke.stocks.framework.thread.*;

import java.util.concurrent.TimeUnit;

public class CountingTaskExample {

    public static void main(String[] args)
    {
        Framework framework = new Framework();
        framework.initialise();
        try {
            framework.getTaskManager().scheduleRepeating(new CountingTask(5), result -> {

                Framework.info("Successfully counted to " + result.countedTo);
                System.exit(0);

            }, 1000, 1000, TimeUnit.MILLISECONDS);
        } catch (TaskStartException e) {
            e.printStackTrace();
        }

        // Keep checking to see if any tasks have finished and their callbacks need consuming
        while (true) {
            framework.getTaskManager().consumeCallbacks();

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) { }
        }
    }

    public static class CountingResult extends Result {
        public int countedTo;
    }

    public static class CountingTask implements Task<TaskDescription, CountingResult> {
        private static final TaskType TYPE = () -> "Counting Task";

        private Progress progress;
        private int countTo, currentNumber;

        public CountingTask(int countTo) {
            this.countTo = countTo;
            this.currentNumber = 0;
        }

        @Override
        public TaskType getType() { return TYPE; }

        @Override
        public TaskDescription getDescription() {
            return new TaskDescription(TYPE) {
                /* Return false */
                public boolean equals(Object object) { return false; }
            };
        }

        @Override
        public Progress createTaskProgress() {
            return progress = new Progress();
        }

        @Override
        public void run() {
            currentNumber ++;
            progress.setProgress(100 * (double) currentNumber / (double) countTo);
            Framework.info("Current percentage: " + progress.getProgress());
        }

        @Override
        public boolean isFinished() { return currentNumber >= countTo; }

        @Override
        public CountingResult getResult() { return new CountingResult() {{countedTo = currentNumber;}}; }
    }

}
