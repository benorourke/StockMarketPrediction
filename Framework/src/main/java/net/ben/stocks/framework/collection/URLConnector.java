package net.ben.stocks.framework.collection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLConnector
{
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    private HttpURLConnection connection;

    private URLConnector(String strUrl, String requestMethod) throws IOException
    {
        URL url = new URL(strUrl);

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setInstanceFollowRedirects(false);
    }

    public String read() throws IOException
    {
        connection.getResponseCode(); // Think this is needed? TODO - Check
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        connection.disconnect();
        return content.toString();
    }

    public static URLConnector connect(String url) throws IOException
    {
        return new URLConnector(url, "GET");
    }

}
