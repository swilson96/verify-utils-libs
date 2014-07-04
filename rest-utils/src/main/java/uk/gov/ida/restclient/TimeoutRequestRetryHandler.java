package uk.gov.ida.restclient;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class TimeoutRequestRetryHandler implements HttpRequestRetryHandler {
    private final int numRetries;

    public TimeoutRequestRetryHandler(int numRetries) {
        this.numRetries = numRetries;
    }

    @Override
    public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext) {
        if( e instanceof ConnectTimeoutException && executionCount < numRetries)
            return true;

        return false;
    }
}
