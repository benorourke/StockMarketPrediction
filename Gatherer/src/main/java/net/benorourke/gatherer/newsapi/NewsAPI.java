package net.benorourke.gatherer.newsapi;

import net.benorourke.gatherer.DataSource;
import net.benorourke.gatherer.TextualData;
import net.benorourke.gatherer.exception.FailedRetrievalException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class NewsAPI extends DataSource
{
    private String apiKey;

    public NewsAPI(String apiKey)
    {
        this.apiKey = apiKey;
    }

    @Override
    public Collection<TextualData> retrieve() throws FailedRetrievalException
    {
        String result = readConnection(newConnection(buildUrlExtension()));
        return null;
    }

    private String buildUrlExtension()
    {
        return "/v2/top-headlines?country=gb&apiKey=" + apiKey;
    }

    private HttpURLConnection newConnection(String urlExtension) throws FailedRetrievalException
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
            throw new FailedRetrievalException(e);
        }
    }

    private String readConnection(HttpURLConnection con) throws FailedRetrievalException
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
            throw new FailedRetrievalException(e);
        }
    }


}
