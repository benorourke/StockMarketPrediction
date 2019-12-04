package net.benorourke.nlp.stanford;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import net.benorourke.nlp.Preprocess;
import net.benorourke.nlp.Preprocessor;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class StanfordPreprocessor extends Preprocessor {
    private final StanfordCoreNLP pipeline;

    public StanfordPreprocessor(Preprocess... preprocess) {
        super(preprocess);

        pipeline = newPipeline();
    }

    @Override
    public String preprocess(String documentText) {
        List<String> lemmas = new LinkedList<String>();
        Annotation document = new Annotation(documentText);
        pipeline.annotate(document);
        for (CoreMap sentence: document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }
        return reassemble(lemmas);
    }

    private StanfordCoreNLP newPipeline() {
        return new StanfordCoreNLP(createProperties());
    }

    private Properties createProperties() {
        Properties props = new Properties();

        String annotators = "tokenize, ssplit, pos";
        if (hasPreprocess(Preprocess.LEMMATISATION))
            annotators += ", lemma";
//        if (hasPreprocess(Preprocess.STOPWORD_REMOVAL)) { -> TODO Create custom annotator
//            annotators += ", stopword";
//            props.setProperty("customAnnotatorClass.stopword", "net.benorourke.nlp.standford.StopwordsAnnotation");
//        }

        props.put("annotators", annotators);
        return props;
    }

    private String reassemble(List<String> tokens) {
        return tokens.stream().collect(Collectors.joining(" "));
    }

}
