# Whois API

This is a client library for the [Whois API service](http://whois-api.domaininformation.de/).
With this API you can

- Check if a domain name is available
- Get its whois data or query an arbitrary whois server
- Don't worry about rate limits on the respective whois server

The service supports all domains of the
[Whois Server list](https://github.com/whois-server-list/whois-server-list),
which is more than 500 top level domains.

# Installation

This package is available in Maven central:

```xml
<dependency>
    <groupId>de.malkusch.whois-server-list</groupId>
    <artifactId>whois-api</artifactId>
    <version>0.0.8</version>
</dependency>
```

# Usage

You'll need an api key to use this library. Get if from
the [Whois API](http://whois-api.domaininformation.de/).

```java
WhoisApi whoisApi = new WhoisApi("apiKey");
```

- [`WhoisApi.isAvailable(String)`](http://whois-server-list.github.io/whois-api-java/apidocs/de/malkusch/whoisApi/WhoisApi.html#isAvailable-java.lang.String-)
    checks if a domain name is available.
- [`WhoisApi.whois(String)`](http://whois-server-list.github.io/whois-api-java/apidocs/de/malkusch/whoisApi/WhoisApi.html#whois-java.lang.String-)
    returns the whois data of a domain.
- [`WhoisApi.query(String, String)`](http://whois-server-list.github.io/whois-api-java/apidocs/de/malkusch/whoisApi/WhoisApi.html#query-java.lang.String-java.lang.String-)
    queries an arbitrary whois server.
- [`WhoisApi.domains()`](http://whois-server-list.github.io/whois-api-java/apidocs/de/malkusch/whoisApi/WhoisApi.html#domains--)
    lists all top and second level domains which can be used by the Whois API.

## Example

```java
WhoisApi whoisApi = new WhoisApi("apiKey");
System.out.println(whoisApi.isAvailable("example.net") ? "available" : "registered");
```

# License and authors

This project is free and under the WTFPL.
Responsable for this project is Markus Malkusch markus@malkusch.de.

[![Build Status](https://travis-ci.org/whois-server-list/whois-api-java.svg?branch=master)](https://travis-ci.org/whois-server-list/whois-api-java)
