package net.benorourke.stocks.framework.preprocess.document.representer.sentiment;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.series.data.DocumentType;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Test
{

    public static void main(String[] args)
    {
        BinarySentimentFeatureRepresenter binary = new BinarySentimentFeatureRepresenter();
        NormalisedSentimentFeatureRepresenter normalised = new NormalisedSentimentFeatureRepresenter();

        binary.initialise(new ArrayList<>());
        normalised.initialise(new ArrayList<>());

        List<String> cleaned = Arrays.asList("am", "very", "happy");
        CleanedDocument testDocument = new CleanedDocument(new Date(), "am very happy", cleaned,
                                                           DocumentType.NEWS_HEADLINE);

        Framework.info(binary.determineSentiment(testDocument) + ":");
        Framework.info("   Binary: " + StringUtil.formatDoubles(binary.getVectorRepresentation(testDocument)));
        Framework.info("   Normalised: " + StringUtil.formatDoubles(normalised.getVectorRepresentation(testDocument)));
    }

}
