package net.ben.stocks.framework.collection;

import net.ben.stocks.framework.data.Data;
import net.ben.stocks.framework.data.DataType;
import net.ben.stocks.framework.data.Document;
import net.ben.stocks.framework.exception.FailedCollectionException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class NewsAPI implements DataSource
{
    private String apiKey;

    public NewsAPI(String apiKey)
    {
        this.apiKey = apiKey;
    }

    @Override
    public Data retrieveNext() throws FailedCollectionException
    {
        String result = readConnection(newConnection(buildUrlExtension()));
        return new Document(new Date(), result);
    }

    private String buildUrlExtension()
    {
        return "/v2/top-headlines?country=gb&apiKey=" + apiKey;
    }

    private HttpURLConnection newConnection(String urlExtension) throws FailedCollectionException
    {
        URL url = null;
        try
        {
            url = new URL("https://newsapi.org/".concat(urlExtension));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setInstanceFollowRedirects(false);
            return con;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new FailedCollectionException(e);
        }
    }

    private String readConnection(HttpURLConnection con) throws FailedCollectionException
    {
        try
        {
            con.getResponseCode(); // Think this is needed? TODO - Check
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        }
        catch (IOException e)
        {
            throw new FailedCollectionException(e);
        }
    }


}
