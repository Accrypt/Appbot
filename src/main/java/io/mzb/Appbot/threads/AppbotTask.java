package io.mzb.Appbot.threads;

public class AppbotTask implements Runnable {

    private static int currentCount = 0;

    private int id;

    private Thread thread;
    private long delay;
    private long repeat;
    private Runnable exc;
    private boolean kill = false;

    public AppbotTask(Runnable exc) {
        thread = new Thread(this, "Appbot-Thread " + currentCount);
        this.id = currentCount;
        currentCount++;
        this.exc = exc;
        this.delay = 0l;
        this.repeat = -1l;

        thread.start();
    }

    public AppbotTask(Runnable exc, long delay) {
        thread = new Thread(this, "Appbot-DelayThread " + currentCount);
        this.id = currentCount;
        currentCount++;
        this.exc = exc;
        this.delay = delay;
        this.repeat = -1l;

        thread.start();
    }

    public AppbotTask(Runnable exc, long delay, long repeat) {
        thread = new Thread(this, "Appbot-RepeatThread " + currentCount);
        this.id = currentCount;
        currentCount++;
        this.exc = exc;
        this.delay = delay;
        this.repeat = repeat;

        thread.start();
    }

    public int getId() {
        return id;
    }

    public void kill() {
        kill = true;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            thread.stop();
        }
    }

    public void run() {
        try {
            thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(repeat > 0l) {
            while(true) {
                if(kill) {
                    break;
                }

                exc.run();

                try {
                    thread.sleep(repeat);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            exc.run();
        }
    }

}
