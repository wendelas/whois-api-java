package de.malkusch.whoisApi;

import javax.annotation.concurrent.Immutable;

/**
 * A Whois API exception.
 *
 * @author markus@malkusch.de
 * @see https://market.mashape.com/malkusch/whois
 */
@Immutable
public class WhoisApiException extends RuntimeException {

    private static final long serialVersionUID = 7962183069281101132L;

    public WhoisApiException(final Throwable cause) {
        super(cause);
    }

    public WhoisApiException(final String message) {
        super(message);
    }

}
