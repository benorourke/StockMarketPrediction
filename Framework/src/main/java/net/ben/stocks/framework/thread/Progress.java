package net.ben.stocks.framework.thread;

public class Progress
{
    /**
     * Between 0 and 100.
     */
    private double progress;

    public Progress()
    {
        this.progress = 0;
    }

    public double getProgress()
    {
        return progress;
    }

    public void setProgress(double progress)
    {
        this.progress = progress;
    }

}
