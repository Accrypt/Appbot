package io.mzb.Appbot.threads;

public class AppbotTask implements Runnable {

    // Total number of tasks running/have been ran
    private static int currentCount = 0;

    // Id of task instance
    private int id;

    // Thread task is running on
    private Thread thread;
    // The delay to start with
    private long delay;
    // How long to wait between loops
    private long repeat;
    // The runnable to execute each loop
    private Runnable exc;
    // Should the task be killed
    private boolean kill = false;

    /**
     * Start a simple thread, no delay, no loop
     * @param exc Runnable to be executed in a new thread
     */
    AppbotTask(Runnable exc) {
        thread = new Thread(this, "Appbot-Thread " + currentCount);
        this.id = currentCount;
        currentCount++;
        this.exc = exc;
        this.delay = 0L;
        this.repeat = -1L;

        thread.start();
    }

    /**
     * Simple thread with a delay, but no loop
     * @param exc Runnable to be executed after the delay
     * @param delay How long to wait before executing
     */
    AppbotTask(Runnable exc, long delay) {
        thread = new Thread(this, "Appbot-DelayThread " + currentCount);
        this.id = currentCount;
        currentCount++;
        this.exc = exc;
        this.delay = delay;
        this.repeat = -1L;

        thread.start();
    }

    /**
     * Simple task with a delay and loop
     * @param exc Runnable to be executed after the initial delay and then repeat after the delay of repeat
     * @param delay Initial delay to wait before starting the loop
     * @param repeat Runnable is ran forever with this delay
     */
    AppbotTask(Runnable exc, long delay, long repeat) {
        thread = new Thread(this, "Appbot-RepeatThread " + currentCount);
        this.id = currentCount;
        currentCount++;
        this.exc = exc;
        this.delay = delay;
        this.repeat = repeat;

        thread.start();
    }

    /**
     * @return The current task id
     */
    int getId() {
        return id;
    }

    /**
     * Kills the current task
     */
    void kill() {
        kill = true;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failed to join and kill thread " + thread.getName());
        }
    }

    /**
     * Runs the task
     */
    public void run() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(repeat > 0L) {
            while(true) {
                if(kill) {
                    break;
                }

                exc.run();

                try {
                    Thread.sleep(repeat);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            exc.run();
        }
    }

}
