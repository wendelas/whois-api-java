package de.malkusch.whoisApi;

import javax.annotation.concurrent.Immutable;

/**
 * A Whois API exception.
 *
 * @author markus@malkusch.de
 * @see <a href="http://whois-api.domaininformation.de/">Whois API</a>
 */
@Immutable
public final class RecoverableWhoisApiException extends Exception {

    private static final long serialVersionUID = 7962183069281101132L;
    
    public RecoverableWhoisApiException(final String message) {
        super(message);
    }

}
