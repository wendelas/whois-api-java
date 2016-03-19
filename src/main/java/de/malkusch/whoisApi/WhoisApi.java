package de.malkusch.whoisApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A Whois API.
 *
 * This is a client library for the
 * <a href="http://whois-api.domaininformation.de/">Whois API service</a>.
 * Register there to get an API key.
 *
 * With this API you can check if a domain name is available, get its whois data
 * or query an arbitrary whois server. The service is using this
 * <a href="https://github.com/whois-server-list/whois-server-list">Whois server
 * list</a>. Also it avoids hitting any rate limits on the whois servers.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code
 * WhoisApi whoisApi = new WhoisApi("apiKey);
 * System.out.println(whoisApi.isAvailable("example.net") ? "available" : "registered");
 * }
 * </pre>
 *
 * @author markus@malkusch.de
 * @see <a href="http://whois-api.domaininformation.de/">Whois API</a>
 */
@ThreadSafe
public class WhoisApi {

    private final String apiKey;

    private final URI baseUri;

    private static final String DEFAULT_BASE_URI = "https://whois-v0.p.mashape.com/";

    private static final String API_KEY_HEADER = "X-Mashape-Key";

    /**
     * Builds the Whois API client.
     * 
     * Register at http://whois-api.domaininformation.de/ to get an API key.
     * 
     * @param apiKey
     *            api key
     */
    public WhoisApi(final String apiKey) {
        this(apiKey, URI.create(DEFAULT_BASE_URI));
    }

    /**
     * Builds the Whois API client.
     * 
     * Register at http://whois-api.domaininformation.de/ to get an API key.
     * 
     * @param apiKey
     *            api key
     * @param baseUri
     *            API endpoint
     */
    public WhoisApi(final String apiKey, final URI baseUri) {
        if (apiKey == null || baseUri == null) {
            throw new NullPointerException();
        }
        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("API key is empty");
        }

        this.apiKey = apiKey;
        this.baseUri = baseUri;
    }

    /**
     * Checks if a domain is available and returns the whois response.
     * 
     * @param domain
     *            domain name, e.g. "example.net"
     * @return the combined domain check and the whois response
     *
     * @throws WhoisApiException
     *             if the api request caused an error
     * @throws RecoverableWhoisApiException
     *             if the API failed, but you can try again.
     */
    public CheckResult check(final String domain) throws RecoverableWhoisApiException {
        if (domain == null) {
            throw new NullPointerException();
        }
        if (domain.isEmpty()) {
            throw new IllegalArgumentException("Domain is empty.");
        }

        URI uri;
        try {
            uri = URI.create(baseUri.toString() + "/check?domain=" + URLEncoder.encode(domain, "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

        try (InputStream response = sendRequest(uri)) {
            JSONObject json = new JSONObject(IOUtils.toString(response));
            CheckResult result = new CheckResult(json.getBoolean("available"), json.getString("whoisResponse"));
            return result;

        } catch (IOException e) {
            throw new RecoverableWhoisApiException(e);
        }
    }

    /**
     * Checks if a domain is available.
     * 
     * If a domain is available (i.e. not registered) this method will return
     * true
     * 
     * @param domain
     *            domain name, e.g. "example.net"
     * @return true if the domain is available, false otherwise.
     *
     * @throws WhoisApiException
     *             if the api request caused an error
     * @throws RecoverableWhoisApiException
     *             if the API failed, but you can try again.
     */
    public boolean isAvailable(final String domain) throws RecoverableWhoisApiException {
        return check(domain).isAvailable();
    }

    /**
     * Returns the whois data for a domain.
     * 
     * @param domain
     *            domain name, e.g. "example.net"
     * @return whois lookup response
     *
     * @throws WhoisApiException
     *             if the api request caused an error
     * @throws RecoverableWhoisApiException
     *             if the API failed, but you can try again.
     */
    public String whois(final String domain) throws RecoverableWhoisApiException {
        return check(domain).whoisResponse();
    }

    /**
     * Queries a whois server.
     * 
     * @param host
     *            hostname of the whois server, e.g. "whois.verisign-grs.com"
     * @param query
     *            query, e.g. "example.net"
     * 
     * @return response from the whois server
     *
     * @throws WhoisApiException
     *             if the api request caused an error
     * @throws RecoverableWhoisApiException
     *             if the API failed, but you can try again.
     */
    public InputStream query(final String host, final String query) throws RecoverableWhoisApiException {
        if (host == null || query == null) {
            throw new NullPointerException();
        }
        if (host.isEmpty() || query.isEmpty()) {
            throw new IllegalArgumentException("Host and query should not be empty.");
        }

        try {
            URI uri = URI.create(baseUri.toString() + "/whois?host=" + URLEncoder.encode(host, "UTF-8") + "&query="
                    + URLEncoder.encode(query, "UTF-8"));

            return sendRequest(uri);

        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns a list of all top and second level domains, which are known to
     * the Whois API.
     *
     * @return all available top and second level domains
     * @throws RecoverableWhoisApiException
     *             if the API failed, but you can try again.
     */
    public Collection<String> domains() throws RecoverableWhoisApiException {
        URI uri = URI.create(baseUri.toString() + "/domains");
        try (InputStream response = sendRequest(uri)) {
            JSONArray array = new JSONArray(IOUtils.toString(response));

            String[] domains = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                domains[i] = array.getString(i);
            }
            return Arrays.asList(domains);

        } catch (JSONException e) {
            throw new WhoisApiException(e);

        } catch (IOException e) {
            throw new RecoverableWhoisApiException(e);
        }
    }

    private InputStream sendRequest(URI uri) throws RecoverableWhoisApiException {
        try {
            HttpGet get = new HttpGet(uri);
            get.addHeader(API_KEY_HEADER, apiKey);

            HttpResponse response = HttpClients.createDefault().execute(get);
            StatusLine statusLine = response.getStatusLine();

            switch (statusLine.getStatusCode()) {
            case 200:
                return response.getEntity().getContent();

            case 502:
            case 504:
                String message = String.format("Try again (%d): %s", statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
                throw new RecoverableWhoisApiException(message);

            default:
                String message2 = String.format("API failed (%d): %s", statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
                throw new WhoisApiException(message2);

            }

        } catch (ClientProtocolException e) {
            throw new WhoisApiException(e);

        } catch (IOException e) {
            throw new RecoverableWhoisApiException(e);
        }
    }

}
