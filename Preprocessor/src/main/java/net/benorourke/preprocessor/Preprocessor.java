package net.benorourke.preprocessor;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Preprocessor {

    public static void main(String[] args) {
        final List<String> texts = Arrays.asList("I am happy.", "This is a neutral sentence.", "I am very angry, I hate this.");
        final Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        for (String text : texts) {
            printSentiment(pipeline, text);
        }
    }

    /**
     * Print the sentiments of each sentence in a given text.
     *
     * @param pipeline the NLP pipeline
     * @param text the text to analyse
     */
    public static void printSentiment(StanfordCoreNLP pipeline, String text) {
        final Annotation doc = new Annotation(text);
        pipeline.annotate(doc);
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            final String strSentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
            System.out.println(sentence + " (" + strSentiment + ", " + sentiment + ")");
        }
    }

}
