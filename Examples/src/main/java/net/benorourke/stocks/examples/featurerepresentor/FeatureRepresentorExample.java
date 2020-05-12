package net.benorourke.stocks.examples.featurerepresentor;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.FeatureRepresentor;
import net.benorourke.stocks.framework.preprocess.FeatureRepresentorManager;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;

import java.util.List;

public class FeatureRepresentorExample
{

    public static void main(String[] args) {
        Framework framework = new Framework();
        framework.initialise();

        framework.getFeatureRepresentorManager().getDocumentRepresentors().put(
                new FeatureRepresentorManager.Metadata("Custom Representor",
                                             "Will turn clean documents into an array of 5 zeros",
                                      5),
                new CustomRepresentor());
    }

    public static class CustomRepresentor implements FeatureRepresentor<CleanedDocument> {

        @Override
        public DataType<CleanedDocument> getTypeFor() {
            return DataType.CLEANED_DOCUMENT;
        }

        @Override
        public void initialise(List<CleanedDocument> allData) { }

        @Override
        public int getVectorSize() {
            return 5;
        }

        @Override
        public double[] getVectorRepresentation(CleanedDocument datapoint) {
            return new double[5];
        }

        @Override
        public String getName() {
            return "Custom Representor";
        }

        @Override
        public CombinationPolicy getCombinationPolicy() {
            // How to handle taking an average of this feature on a given day
            return CombinationPolicy.TAKE_MEAN_AVERAGE;
        }
    }

}
