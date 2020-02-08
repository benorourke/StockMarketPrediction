package net.ben.stocks.framework.collection.datasource;

import net.ben.stocks.framework.collection.datasource.alphavantage.AlphaVantage;
import net.ben.stocks.framework.collection.datasource.newsapi.NewsAPI;
import net.ben.stocks.framework.series.data.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataSourceManager
{
    private final List<DataSource> dataSources;

    // TODO - API Key Cycling

    public DataSourceManager()
    {
        dataSources = new ArrayList<DataSource>()
        {{
            add(new NewsAPI("78d93a9d68584e61be38b1d90217d1e7")); // TODO - Make this configurable
            add(new AlphaVantage("ZJULNKK5LP9TFN4P")); // TODO - make this configurable
        }};
    }

    public List<DataSource> getDataSources()
    {
        return dataSources;
    }

    public <T extends Data> List<DataSource<T>> getDataSourcesByClass(Class<T> clazz)
    {
        return getDataSources()
                    .stream()
                    .filter(source -> clazz.isAssignableFrom(source.getDataClass()))
                    .map(source -> (DataSource<T>) source)
                    .collect(Collectors.toList());
    }

}