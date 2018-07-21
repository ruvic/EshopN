package eshopn.models;



import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

/**
 *
 * @author mathermann
 */
public class HTTPRequest
{
    private String url;
    private String charset = "UTF-8";
    
    private final String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
    private final String CRLF = "\r\n"; // Line separator required by multipart/form-data.
    
    private final URLConnection connection;
    private final OutputStream output;
    private final PrintWriter writer;
    

    public HTTPRequest() throws MalformedURLException, IOException
    {
        this("http://127.0.0.1");
    }

    public HTTPRequest(String url) throws MalformedURLException, IOException
    {
        this(url, "GET");
    }

    public HTTPRequest(String url, String requestMethod) throws MalformedURLException, IOException
    {
        this.url = url;
        
        connection = new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        ((HttpURLConnection)connection).setRequestMethod(requestMethod);

        output = connection.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
    }
    
    
    public HTTPRequest appendParam(String paramName, String value)
    {
        // Send normal param.
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"" + paramName + "\"").append(CRLF);
        writer.append("Content-Type: text/plain; charset=" + getCharset()).append(CRLF);
        writer.append(CRLF).append(value).append(CRLF).flush();
        
        return this;
    }
    
    public HTTPRequest appendParam(String paramName, File file) throws IOException
    {
        return appendParam(paramName, file,false);
    }
    
    public HTTPRequest appendParam(String paramName, File file,boolean asTextFile) throws IOException
    {
        // Send file.
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + file.getName() + "\"").append(CRLF);
        
        if(asTextFile)
        {
            writer.append("Content-Type: text/plain; charset=" + getCharset()).append(CRLF); // Text file itself must be saved in this charset!
        }
        else
        {
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
        }
        
        writer.append(CRLF).flush();
        Files.copy(file.toPath(), output);
        output.flush(); // Important before continuing with writer!
        writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

        // End of multipart/form-data.
        writer.append("--" + boundary + "--").append(CRLF).flush();
        
        return this;
    }
    
    public int post() throws IOException
    {
        // Request is lazily fired whenever you need to obtain information about response.
//        System.out.println(((HttpURLConnection)connection).getContent().toString());
        return ((HttpURLConnection)connection).getResponseCode(); // Should be 200
    }
    

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }
}
