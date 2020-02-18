package net.benorourke.stocks.framework.preprocessor.stanford;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import net.benorourke.stocks.framework.preprocessor.Preprocess;
import net.benorourke.stocks.framework.preprocessor.Preprocessor;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.ProcessedData;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class StanfordPreprocessor implements Preprocessor<Document, ProcessedDocument>
{

    @Override
    public void initialise()
    {

    }

    @Override
    public ProcessedDocument preprocess(Document data)
    {
        return null;
    }

    //    private final StanfordCoreNLP pipeline;
//
//    public StanfordPreprocessor(Preprocess... preprocess)
//    {
//        super(preprocess);
//
//        pipeline = newPipeline();
//    }
//
//    @Override
//    public String preprocess(String documentText)
//    {
//        List<String> lemmas = new LinkedList<String>();
//        Annotation document = new Annotation(documentText);
//        pipeline.annotate(document);
//        for (CoreMap sentence: document.get(CoreAnnotations.SentencesAnnotation.class))
//        {
//            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class))
//            {
//                lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
//            }
//        }
//        return reassemble(lemmas);
//    }
//
//    private StanfordCoreNLP newPipeline()
//    {
//        return new StanfordCoreNLP(createProperties());
//    }
//
//    private Properties createProperties()
//    {
//        Properties props = new Properties();
//
//        String annotators = "tokenize, ssplit, pos";
//        if (hasPreprocess(Preprocess.LEMMATISATION))
//            annotators += ", lemma";
////        if (hasPreprocess(Preprocess.STOPWORD_REMOVAL)) { -> TODO Create custom annotator
////            annotators += ", stopword";
////            props.setProperty("customAnnotatorClass.stopword", "net.benorourke.nlp.standford.StopwordsAnnotation");
////        }
//
//        props.put("annotators", annotators);
//        return props;
//    }
//
//    private String reassemble(List<String> tokens)
//    {
//        return tokens.stream().collect(Collectors.joining(" "));
//    }

}
