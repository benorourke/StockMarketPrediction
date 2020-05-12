package net.benorourke.stocks.userinterface.util;

import net.benorourke.stocks.framework.thread.TaskType;
import net.benorourke.stocks.userinterface.scene.asyncinflater.InflationTask;

import java.util.Arrays;
import java.util.List;

public class Constants
{
    public static final String APPLICATION_NAME = "Stock Market Prediction";
    public static final int APPLICATION_WIDTH_MIN = 1280;
    public static final int APPLICATION_HEIGHT_MIN = 720;

    public static final String CREATE_SERIES_NAME = "New Series";
    public static final int CREATE_SERIES_WIDTH_MIN = 320;
    public static final int CREATE_SERIES_HEIGHT_MIN = 180;

    public static final String TASKS_NAME = "Running Tasks";
    public static final int TASKS_WIDTH_MIN = 512;
    public static final int TASKS_HEIGHT_MIN = 288;
    public static final long UPDATE_TASKS_EVERY = 200;
    /** Task types not to show in the TasksController. */
    public static final List<TaskType> TASKS_TO_IGNORE = Arrays.asList(InflationTask.TYPE);

    /** The number of threads to create on the */
    public static final int TASK_POOL_SIZE = 10;

    /** Delay for collection tasks. */
    public static final long COLLECTION_DELAY = 1000;
    /** Interval for collection tasks. */
    public static final long COLLECTION_INTERVAL = 1000;

    /** Delay for pre-processing tasks. */
    public static final long PREPROCESSING_DELAY = 200;
    /** Interval for pre-processing tasks. */
    public static final long PREPROCESSING_INTERVAL = 200;

    private Constants() {}

}
