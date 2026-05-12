package org.example.Samples;
import org.example.Assertion.Assertion;
import org.example.Assertion.StatusAssertion;
import org.example.Data.TestRequest;
import org.example.Sample.Sampler;
import org.example.SampleResult.SampleResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class HttpSampler implements Sampler {

    private final String url;
    private final String method;
    private final List<TestRequest.Header> headers;
    private final String body;

    private final HttpClient client;

    public HttpSampler(String url,
                       String method,
                       List<TestRequest.Header> headers,
                       String body) {

        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;

        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    @Override
    public SampleResult sample() {

        SampleResult result = new SampleResult();
        result.sampleStart();

        try {

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url));

            // 设置方法
            switch (method) {
                case "POST":
                case "PUT":
                    builder.method(method,
                            HttpRequest.BodyPublishers.ofString(
                                    body == null ? "" : body));
                    break;
                case "DELETE":
                    builder.DELETE();
                    break;
                default:
                    builder.GET();
            }

            // 设置 Header
            if (headers != null) {
                for (TestRequest.Header h : headers) {
                    if (h.getKey() != null && !h.getKey().isEmpty()) {
                        builder.header(h.getKey(), h.getValue());
                    }
                }
            }

            HttpResponse<String> response =
                    client.send(builder.build(),
                            HttpResponse.BodyHandlers.ofString());

            result.setStatusCode(response.statusCode());
            result.setSuccess(response.statusCode() == 200);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }

        result.sampleEnd();
        return result;
    }
    @Override
    public List<Assertion> defaultAssertions() {
        return List.of(new StatusAssertion(200));
    }
}