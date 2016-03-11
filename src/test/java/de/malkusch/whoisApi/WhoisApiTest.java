package de.malkusch.whoisApi;

import org.junit.Test;

/**
 * A test for WhoisApi.
 * 
 * @author markus@malkusch.de
 * @see <a href="http://whois-api.domaininformation.de/">Whois API</a>
 */
public class WhoisApiTest {

    @Test(expected = NullPointerException.class)
    public void isAvailableShouldNotAcceptNull() throws RecoverableWhoisApiException {
        WhoisApi api = new WhoisApi("key");
        api.isAvailable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isAvailableShouldNotAcceptEmptyString() throws RecoverableWhoisApiException {
        WhoisApi api = new WhoisApi("key");
        api.isAvailable("");
    }

    @Test(expected = NullPointerException.class)
    public void whoisShouldNotAcceptNull() throws RecoverableWhoisApiException {
        WhoisApi api = new WhoisApi("key");
        api.whois(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whoisShouldNotAcceptEmptyString() throws RecoverableWhoisApiException {
        WhoisApi api = new WhoisApi("key");
        api.whois("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void queryShouldNotAcceptEmptyHost() throws RecoverableWhoisApiException {
        WhoisApi api = new WhoisApi("key");
        api.query("", "example.net");
    }

    @Test(expected = NullPointerException.class)
    public void queryShouldNotAcceptNullAsHost() throws RecoverableWhoisApiException {
        WhoisApi api = new WhoisApi("key");
        api.query(null, "example.net");
    }

    @Test(expected = IllegalArgumentException.class)
    public void queryShouldNotAcceptEmptyQuery() throws RecoverableWhoisApiException {
        WhoisApi api = new WhoisApi("key");
        api.query("example.net", "");
    }

    @Test(expected = NullPointerException.class)
    public void queryShouldNotAcceptNullAsQuery() throws RecoverableWhoisApiException {
        WhoisApi api = new WhoisApi("key");
        api.query("example.net", null);
    }

}
