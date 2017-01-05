package io.mzb.Appbot.threads;

import java.util.Collection;
import java.util.HashMap;

public class TaskManager {

    // Holds all tasks by their id
    private HashMap<Integer, AppbotTask> taskHashMap = new HashMap<>();

    /**
     * Kills a task from the id
     * @param id Id of the task you want to kill
     */
    public void killTask(int id) {
        taskHashMap.get(id).kill();
    }

    /**
     * Runs a new task with no delay or loop
     * @param runnable The runnable to execute
     * @return The id of the task that has been started
     */
    public int runTask(Runnable runnable) {
        AppbotTask ar = new AppbotTask(runnable);
        return ar.getId();
    }

    /**
     * Runs a task with a delay but no loop
     * @param runnable The runnable to be executed after the delay
     * @param delay How long to wait before running the runnable
     * @return The id of the task started
     */
    public int runTask(Runnable runnable, long delay) {
        AppbotTask ar = new AppbotTask(runnable, delay);
        return ar.getId();
    }

    /**
     * Run a new task with a delay and a loop
     * @param runnable The runnable to be executed
     * @param delay The delay to wait before starting
     * @param repeat Repeated the runnable with this delay
     * @return The id of the task started
     */
    public int runTask(Runnable runnable, long delay, long repeat) {
        AppbotTask ar = new AppbotTask(runnable, delay, repeat);
        return ar.getId();
    }

    /**
     * @return Runs a list of all tasks currently running
     */
    public Collection<AppbotTask> getTasks() {
        return taskHashMap.values();
    }

}
