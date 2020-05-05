package net.benorourke.stocks.userinterface.scene.dashboard;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FlowStageTest
{
    private static final FlowStage[] STAGES = FlowStage.values();

    @Test
    public void isBefore_IncreasingOrdinalsComparingPrevious_ShouldReturnTrue()
    {
        for (int i = 0; i < STAGES.length - 1; i ++)
        {
            FlowStage current = STAGES[i];
            FlowStage next = STAGES[i + 1];

            assertEquals(true, next.isBefore(current));
        }
    }

    @Test
    public void isBefore_DecreasingOrdinalsComparingPrevious_ShouldReturnFalse()
    {
        for (int i = STAGES.length - 1; i > 1; i --)
        {
            FlowStage current = STAGES[i];
            FlowStage previous = STAGES[i - 1];

            assertEquals(false, previous.isBefore(current));
        }
    }

}
