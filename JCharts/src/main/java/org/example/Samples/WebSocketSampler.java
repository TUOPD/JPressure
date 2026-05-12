package org.example.Samples;

import org.example.Assertion.Assertion;
import org.example.Assertion.WebSocketAssertion;
import org.example.Sample.Sampler;
import org.example.SampleResult.SampleResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WebSocketSampler implements Sampler {

    private final String url;

    public WebSocketSampler(String url) {
        this.url = url;
    }

    @Override
    public SampleResult sample() {

        SampleResult result = new SampleResult();
        result.sampleStart();

        try {
            HttpClient client = HttpClient.newHttpClient();

            CompletableFuture<WebSocket> ws =
                    client.newWebSocketBuilder()
                            .buildAsync(URI.create(url),
                                    new WebSocket.Listener() {});

            ws.join();

            result.setSuccess(true);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }

        result.sampleEnd();
        return result;
    }
    @Override
    public List<Assertion> defaultAssertions() {
        return List.of(new WebSocketAssertion());
    }
}