package net.benorourke.gatherer;

public class TextualData
{
    public enum DataType
    {
        NEWS_HEADLINE, TWEET;
    }

    private final DataType dataType;
    private String content;

    public TextualData(DataType dataType, String content)
    {
        this.dataType = dataType;
        this.content = content;
    }

    public DataType getDataType()
    {
        return dataType;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

}
