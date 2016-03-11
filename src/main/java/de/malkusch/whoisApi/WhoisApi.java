package de.malkusch.whoisApi;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import javax.annotation.concurrent.ThreadSafe;

import org.json.JSONArray;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

/**
 * A Whois API.
 *
 * This is a client library for the
 * <a href="http://whois-api.domaininformation.de/">Whois API service</a>. Register there to get an API key.
 *
 * With this API you can check if a domain name is available, get its whois data
 * or query an arbitrary whois server. The service is using this
 * <a href="https://github.com/whois-server-list/whois-server-list">Whois server list</a>.
 * Also it avoids hitting any rate limits on the whois servers.
 * <p>
 * Example:
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
        if (domain == null) {
            throw new NullPointerException();
        }
        if (domain.isEmpty()) {
            throw new IllegalArgumentException("Domain is empty.");
        }

        String uri = baseUri.toString() + "/check?domain={domain}";
        HttpRequest request = Unirest.get(uri).routeParam("domain", domain);

        return sendRequest(request, request::asJson).getObject().getBoolean("available");
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
        if (domain == null) {
            throw new NullPointerException();
        }
        if (domain.isEmpty()) {
            throw new IllegalArgumentException("Domain is empty.");
        }

        String uri = baseUri.toString() + "/check?domain={domain}";
        HttpRequest request = Unirest.get(uri).routeParam("domain", domain);

        return sendRequest(request, request::asJson).getObject().getString("whoisResponse");
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

        String uri = baseUri.toString() + "/whois?host={host}&query={query}";
        HttpRequest request = Unirest.get(uri).routeParam("host", host).routeParam("query", query);

        return sendRequest(request, request::asBinary);
    }

    /**
     * Returns a list of all top and second level domains, which are known to
     * the Whois API.
     *
     * @return all available top and second level domains
     */
    public Collection<String> domains() {
        String uri = baseUri.toString() + "/domains";
        HttpRequest request = Unirest.get(uri);

        JSONArray response;
        try {
            response = sendRequest(request, request::asJson).getArray();

        } catch (RecoverableWhoisApiException e) {
            throw new IllegalStateException(e);
        }

        String[] domains = new String[response.length()];
        for (int i = 0; i < response.length(); i++) {
            domains[i] = response.getString(i);
        }
        return Arrays.asList(domains);
    }

    private interface RequestCall<T> {

        HttpResponse<T> call() throws UnirestException;

    }

    private <T> T sendRequest(final HttpRequest request, RequestCall<T> requestCall)
            throws RecoverableWhoisApiException {

        request.header(API_KEY_HEADER, apiKey);

        HttpResponse<T> response;
        try {
            response = requestCall.call();

        } catch (UnirestException e) {
            throw new WhoisApiException(e);
        }

        switch (response.getStatus()) {
        case 200:
            return response.getBody();

        case 502:
        case 504:
            String message = String.format("Try again (%d): %s", response.getStatus(), convert(response));
            throw new RecoverableWhoisApiException(message);

        default:
            String message2 = String.format("API failed (%d): %s", response.getStatus(), convert(response));
            throw new WhoisApiException(message2);

        }
    }

    private static String convert(final HttpResponse<?> response) {
        try (Scanner scanner = new Scanner(response.getRawBody())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

}
