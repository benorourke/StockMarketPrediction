package net.benorourke.stocks.userinterface.util;

import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.thread.TaskType;
import net.benorourke.stocks.userinterface.scene.asyncinflater.InflationTask;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Constants
{
    public static final String APPLICATION_NAME = "Stock Market Prediction";
    public static final int APPLICATION_WIDTH_MIN = 1280;
    public static final int APPLICATION_HEIGHT_MIN = 720;

    public static final String TASKS_NAME = "Running Tasks";
    public static final int TASKS_WIDTH_MIN = 512;
    public static final int TASKS_HEIGHT_MIN = 288;
    public static final long UPDATE_TASKS_EVERY = 200;
    /**
     * Tasks not to show in the TasksController.
     */
    public static final List<TaskType> TASKS_TO_IGNORE = Arrays.asList(InflationTask.TYPE);

    public static final int TASK_POOL_SIZE = 10;

    public static final long COLLECTION_DELAY = 200;
    public static final long COLLECTION_INTERVAL = 200;

    public static final long PREPROCESSING_DELAY = 200;
    public static final long PREPROCESSING_INTERVAL = 200;

    private Constants() {}

}
