package net.benorourke.stocks.framework.collection.datasource;

import net.benorourke.stocks.framework.collection.datasource.alphavantage.AlphaVantage;
import net.benorourke.stocks.framework.collection.datasource.newsapi.NewsAPI;
import net.benorourke.stocks.framework.collection.datasource.twitter.TwitterFree;
import net.benorourke.stocks.framework.collection.datasource.twitter.TwitterPremium30Day;
import net.benorourke.stocks.framework.collection.datasource.twitter.TwitterPremiumFullArchive;
import net.benorourke.stocks.framework.series.data.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The manager class for all present data sources.
 */
public class DataSourceManager
{
    private final List<DataSource> dataSources;

    public DataSourceManager()
    {
        // Add the pre-set datasources
        dataSources = new ArrayList<>();
        dataSources.add(new AlphaVantage());
        dataSources.add(new NewsAPI());
        dataSources.add(new TwitterFree());
        dataSources.add(new TwitterPremium30Day());
        dataSources.add(new TwitterPremiumFullArchive());
    }

    public List<DataSource> getDataSources()
    {
        return dataSources;
    }

    /**
     * Get a datasource by it's class.
     *
     * @param clazz the class
     * @param <T> the inferred type
     * @return the DataSource
     */
    public <T extends DataSource> DataSource getDataSourceByClass(Class<T> clazz)
    {
        return dataSources.stream()
                          .filter(s -> s.getClass().equals(clazz))
                          .findFirst()
                          .orElse(null);
    }

    /**
     * Get a datasource by its name.
     *
     * @param name the name
     * @return the datasource
     */
    public DataSource getDataSourceByName(String name)
    {
        return dataSources.stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Check whether a datasource exists by it's class.
     *
     * @param clazz the class
     * @param <T> the inferred type
     * @return whether it exists
     */
    public <T extends DataSource> boolean dataSourceExists(Class<T> clazz)
    {
        return getDataSourceByClass(clazz) != null;
    }

    /**
     * Get all of the datasources that collect a certain type of data.
     *
     * @param clazz the data class
     * @param <T> the inferred type
     * @return the List of all datasources collecting the specified type
     */
    public <T extends Data> List<DataSource<T>> getDataSourcesByData(Class<T> clazz)
    {
        return dataSources.stream()
                          .filter(source -> clazz.isAssignableFrom(source.getDataClass()))
                          .map(source -> (DataSource<T>) source)
                          .collect(Collectors.toList());
    }

}
