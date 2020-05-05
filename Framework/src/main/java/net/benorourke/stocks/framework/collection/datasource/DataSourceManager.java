package net.benorourke.stocks.framework.collection.datasource;

import net.benorourke.stocks.framework.collection.datasource.alphavantage.AlphaVantage;
import net.benorourke.stocks.framework.collection.datasource.newsapi.NewsAPI;
import net.benorourke.stocks.framework.collection.datasource.twitter.Twitter4J;
import net.benorourke.stocks.framework.series.data.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataSourceManager
{
    private final List<DataSource> dataSources;

    public DataSourceManager()
    {
        dataSources = new ArrayList<>();
        dataSources.add(new AlphaVantage());
        dataSources.add(new NewsAPI());
        dataSources.add(new Twitter4J());
    }

    public List<DataSource> getDataSources()
    {
        return dataSources;
    }

    public <T extends DataSource> DataSource getDataSourceByClass(Class<T> clazz)
    {
        return dataSources.stream()
                          .filter(s -> s.getClass().equals(clazz))
                          .findFirst()
                          .orElse(null);
    }

    public <T extends DataSource> boolean dataSourceExists(Class<T> clazz)
    {
        return getDataSourceByClass(clazz) != null;
    }

    public <T extends Data> List<DataSource<T>> getDataSourcesByData(Class<T> clazz)
    {
        return dataSources.stream()
                          .filter(source -> clazz.isAssignableFrom(source.getDataClass()))
                          .map(source -> (DataSource<T>) source)
                          .collect(Collectors.toList());
    }

}
