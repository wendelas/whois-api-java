package de.malkusch.whoisApi;

import javax.annotation.concurrent.Immutable;

/**
 * A Domain check result.
 *
 * @author markus@malkusch.de
 * @see <a href="http://whois-api.domaininformation.de/">Whois API</a>
 */
@Immutable
public final class CheckResult {

    /**
     * True if the domain is available.
     */
    private final boolean available;

    /**
     * The response from the whois server.
     */
    private final String whoisResponse;

    /**
     * Builds the CheckResult.
     *
     * @param available
     *            true if the domain is available.
     * @param whoisResponse
     *            the response from the whois server.
     */
    CheckResult(boolean available, String whoisResponse) {

        if (whoisResponse == null) {
            throw new NullPointerException();
        }

        this.available = available;
        this.whoisResponse = whoisResponse;
    }

    /**
     * Returns if the domain is available.
     * 
     * @return true if the domain is available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Returns the whois server response.
     * 
     * @return whois server response
     */
    public String whoisResponse() {
        return whoisResponse;
    }

}
