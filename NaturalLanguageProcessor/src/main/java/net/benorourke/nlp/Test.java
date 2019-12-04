package net.benorourke.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import net.benorourke.nlp.stanford.StanfordPreprocessor;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Test {

//    public static void main(String[] args) {
//        final List<String> texts = Arrays.asList("I am happy.", "This is a neutral sentence.", "I am very angry, I hate this.");
//        final Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
//        final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//        for (String text : texts) {
//            final Annotation doc = new Annotation(text);
//            pipeline.annotate(doc);
//            for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
//                final String strSentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
//                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
//                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
//                System.out.println(sentence + " (" + strSentiment + ", " + sentiment + ")");
//            }
//        }
//    }

//    public static void main(String[] args) {
//        StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
//        System.out.println(lemmatizer.lemmatize("Test Going gone go going"));
//    }

    public static void main(String[] args) {
        Preprocessor preprocessor = new StanfordPreprocessor(Preprocess.values());
        System.out.println(preprocessor.preprocess("Test Going gone go going"));
    }

}
