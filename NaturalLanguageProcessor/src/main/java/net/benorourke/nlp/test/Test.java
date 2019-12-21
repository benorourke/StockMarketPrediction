package net.benorourke.nlp.test;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

public class Test {

    public static void main(String[] args) {
        String[] corpus = {"This is a happy sentence.", "Neutral sentence.", "I hate this."};
        final Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        for(String strDoc : corpus) {
            final Annotation doc = new Annotation(strDoc);
            pipeline.annotate(doc);
            for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
                final String strSentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                System.out.println(sentence + " (" + strSentiment + ", " + sentiment + ")");
            }
        }
    }

//    public static void main(String[] args) {
//        Preprocessor preprocessor = new StanfordPreprocessor(Preprocess.values());
//        System.out.println(preprocessor.preprocess("Test Going gone go going"));
//    }

//    public static void main(String[] args) {
//        TFIDF bow = new TFIDF();
//
//        Document doc1 = new Document(Document.DocumentType.NEWS_HEADLINE,
//                                     "This is a test",
//                                     Arrays.asList("This", "is", "a", "test"));
//        Document doc2 = new Document(Document.DocumentType.NEWS_HEADLINE,
//                                "This is another test",
//                                     Arrays.asList("This", "is", "another", "test"));
//        Document[] corpus = new Document[]{doc1, doc2};
//
//        double tfidf1 = bow.tfidf(corpus, doc1, "a");
//        double tfidf2 = bow.tfidf(corpus, doc2, "another");
//        double tfidf3 = bow.tfidf(corpus, doc1, "This");
//        System.out.println("tfidf_a = " + tfidf1);
//        System.out.println("tfidf_another = " + tfidf2);
//        System.out.println("tfidf_This = " + tfidf3);
//    }

}
