# Whois API

This is a client library for the [Whois API service](https://market.mashape.com/malkusch/whois).
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
    <version>0.0.1</version>
</dependency>
```

# Usage

You'll need a "Mashape-Key" to use this library. Register at 
[Mashape's marketplace](https://market.mashape.com/) and subscribe
to the [Whois API](https://market.mashape.com/malkusch/whois).

```java
WhoisApi whoisApi = new WhoisApi("apiKey");
```

- `WhoisApi.isAvailable(String)` checks if a domain name is available.
- `WhoisApi.whois(String)` returns the whois data of a domain.
- `WhoisApi.query(String, String)` queries an arbitrary whois server.
- `WhoisApi.domains()` lists all top and second level domains which can be used by the Whois API.

## Example

```java
WhoisApi whoisApi = new WhoisApi("apiKey");
System.out.println(whoisApi.isAvailable("example.net") ? "available" : "registered");
```

# License and authors

This project is free and under the WTFPL.
Responsable for this project is Markus Malkusch markus@malkusch.de.

[![Build Status](https://travis-ci.org/whois-server-list/whois-api-java.svg?branch=master)](https://travis-ci.org/whois-server-list/whois-api-java)
