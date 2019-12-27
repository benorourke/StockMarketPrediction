package net.ben.stocks.framework.collection;

import net.ben.stocks.framework.collection.quote.AlphaVantage;
import net.ben.stocks.framework.collection.text.NewsAPI;
import net.ben.stocks.framework.series.data.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataSourceManager
{
    private final List<DataSource> dataSources;

    public DataSourceManager()
    {
        dataSources = new ArrayList<DataSource>()
        {{
            add(new NewsAPI("78d93a9d68584e61be38b1d90217d1e7")); // TODO - Make this configurable
            add(new AlphaVantage(apiKey));
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
                    .filter(source -> clazz.isAssignableFrom(source.getDataClazz()))
                    .map(source -> (DataSource<T>) source)
                    .collect(Collectors.toList());
    }

}
