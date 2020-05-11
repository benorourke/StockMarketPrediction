package net.benorourke.stocks.framework.collection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A utility class to connect to URLs and get a response.
 */
public class URLConnector
{
    public static final int RESPONSE_OK = 200;

    public static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    private HttpURLConnection connection;

    /**
     * Create a new instance and open the connection.
     *
     * @param strUrl the URL to connect to
     * @param requestMethod the method to use
     * @throws IOException if the connection could not be opened
     */
    private URLConnector(String strUrl, String requestMethod) throws IOException
    {
        URL url = new URL(strUrl);

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setInstanceFollowRedirects(false);
    }

    /**
     * Read the response to the request.
     *
     * @return the response as a String
     * @throws IOException if the response was unable to be read
     */
    public String read() throws IOException
    {
        connection.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
        {
            content.append(inputLine);
        }
        in.close();

        connection.disconnect();
        return content.toString();
    }

    public int getResponseCode() throws IOException
    {
        return connection.getResponseCode();
    }
    
    public static URLConnector connect(String url) throws IOException
    {
        return new URLConnector(url, "GET");
    }

}
