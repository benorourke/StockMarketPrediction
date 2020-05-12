package net.benorourke.stocks.userinterface;

import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.TaskDescription;

import java.util.Map;
import java.util.UUID;

/**
 * A listener instance for the updates of tasks and their percentage progresses.
 */
public interface TaskUpdateAdapter
{

    /**
     * This will be called on the BackgroundThread
     *
     * @param descriptions
     * @param progresses
     */
    void update(Map<UUID, TaskDescription> descriptions, Map<UUID, Progress> progresses);

}
