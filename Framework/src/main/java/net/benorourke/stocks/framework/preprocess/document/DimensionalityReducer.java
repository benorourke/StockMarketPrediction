package net.benorourke.stocks.framework.preprocess.document;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.util.DateUtil;

import java.util.*;
import java.util.stream.Collectors;

public class DimensionalityReducer extends Preprocess<List<Document>, List<CleanedDocument>>
{
    private static final int PROGRESS_ITERATIONS = 50;
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(new String[]
            {
                "a", "an", "and", "are", "as", "at", "be", "but", "by",
                "for", "if", "in", "into", "is", "it",
                "no", "not", "of", "on", "or", "such",
                "that", "the", "their", "then", "there", "these",
                "they", "this", "to", "was", "will", "with"
            }));

    private StanfordCoreNLP pipeline;

    @Override
    public void initialise()
    {
        Framework.info("Creating Pipeline [Cleaning]");
        pipeline = new StanfordCoreNLP(createProperties());
        Framework.info("Created Pipeline [Cleaning]");
    }

    @Override
    public List<CleanedDocument> preprocess(List<Document> data)
    {
        List<CleanedDocument> res = new ArrayList<CleanedDocument>();
        int total = data.size(), count = 0;
        for (Document document : data)
        {
            Framework.debug("Cleaning document on " + DateUtil.formatDetailed(document.getDate()));

            List<String> cleanedTerms = clean(document.getContent());
            CleanedDocument cleaned = new CleanedDocument(document.getDate(), document.getContent(),
                                                          cleanedTerms, document.getDocumentType());
            res.add(cleaned);

            count ++;
            if(count % PROGRESS_ITERATIONS == 0)
                onProgressChanged(( (double) count / (double) total) * 100 );
        }

        onProgressChanged(100.0D);
        return res;
    }

    /**
     * Lemmatise, lowercase, remove stop-words and remove non-words.
     *
     * @param documentText
     * @return
     */
    public List<String> clean(String documentText)
    {
        documentText = documentText.toLowerCase().replaceAll("[^a-zA-Z ]", "");

        List<String> lemmas = new LinkedList<String>();
        Annotation document = new Annotation(documentText);
        pipeline.annotate(document);
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class))
        {
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class))
            {
                String lemmatisedToken = token.get(CoreAnnotations.LemmaAnnotation.class);
                if(!isStopword(lemmatisedToken))
                    lemmas.add(lemmatisedToken);
            }
        }

        return lemmas;
    }

    private boolean isStopword(String word)
    {
        return STOPWORDS.contains(word);
    }

    private Properties createProperties()
    {
        Properties props = new Properties();
        String annotators = "tokenize, ssplit, pos, lemma";
        props.put("annotators", annotators);
        return props;
    }

}
