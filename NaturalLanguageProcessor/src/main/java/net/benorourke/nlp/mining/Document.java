package net.benorourke.nlp.mining;

import java.util.List;

public class Document {
    public enum DocumentType {NEWS_HEADLINE, TWEET}

    private DocumentType type;
    private String text;
    private List<String> words;
    private int size;

    public Document(DocumentType type, String text, List<String> words) {
        this.type = type;
        this.text = text;
        this.words = words;
        this.size = words.size();
    }

    public DocumentType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public List<String> getWords() {
        return words;
    }

    public int size() {
        return size;
    }

}
