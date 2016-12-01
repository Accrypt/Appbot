package io.mzb.Appbot.threads;

import java.util.Collection;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, AppbotTask> taskHashMap = new HashMap<>();

    //Kill a task from id
    public void killTask(int id) {
        taskHashMap.get(id).kill();
    }

    //Run task outside of main thread
    public int runTask(Runnable runnable) {
        AppbotTask ar = new AppbotTask(runnable);
        return ar.getId();
    }

    //Run task outside of main thread with delay in ms
    public int runTask(Runnable runnable, long delay) {
        AppbotTask ar = new AppbotTask(runnable, delay);
        return ar.getId();
    }

    // Run task outside of main thread with delay in ms and repeat every ms
    public int runTask(Runnable runnable, long delay, long repeat) {
        AppbotTask ar = new AppbotTask(runnable, delay, repeat);
        return ar.getId();
    }

    public Collection<AppbotTask> getTasks() {
        return taskHashMap.values();
    }

}
