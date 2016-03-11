package de.malkusch.whoisApi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

/**
 * An integration test for WhoisApi.
 * 
 * Please provide the environment variable API_KEY.
 * 
 * @author markus@malkusch.de
 * @see <a href="http://whois-api.domaininformation.de/">Whois API</a>
 */
public class WhoisApiIntegrationTest {

    private WhoisApi api;

    @Before
    public void buildApi() {
        String apiKey = System.getenv("API_KEY");
        assumeNotNull(apiKey);
        api = new WhoisApi(apiKey);
    }

    @Test
    public void isAvailableShouldReturnTrue() throws RecoverableWhoisApiException {
        assertTrue(api.isAvailable("fsdfdsfsdfdsfsdfs4321df.net"));
    }

    @Test
    public void isAvailableShouldReturnFalse() throws RecoverableWhoisApiException {
        assertFalse(api.isAvailable("example.net"));
    }

    @Test(expected = WhoisApiException.class)
    public void isAvailableShouldFail() throws RecoverableWhoisApiException {
        api.isAvailable("invalid");
    }

    @Test
    public void whoisShouldReturnTheLookup() throws RecoverableWhoisApiException {
        String lookup = api.whois("example.net");
        assertFalse(lookup.isEmpty());
    }

    @Test(expected = WhoisApiException.class)
    public void whoisShouldFail() throws RecoverableWhoisApiException {
        api.whois("invalid");
    }

    @Test(expected = WhoisApiException.class)
    public void queryShouldFail() throws RecoverableWhoisApiException {
        api.query("invalid", "example.net");
    }

    @Test
    public void querysShouldReturnTheResponse() throws RecoverableWhoisApiException, IOException {
        try (InputStream response = api.query("whois.nic.de", "example.net")) {
            assertNotEquals(-1, response.read());
        }
    }

    @Test
    public void domainsShouldIncludeKnownTLDs() {
        assertTrue(api.domains().contains("de"));
        assertTrue(api.domains().contains("com"));
        assertTrue(api.domains().contains("net"));
    }

}
