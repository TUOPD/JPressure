package org.example.Samples;

import org.example.Assertion.Assertion;
import org.example.Assertion.TcpAssertion;
import org.example.Sample.Sampler;
import org.example.SampleResult.SampleResult;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class TcpSampler implements Sampler {

    private final String host;
    private final int port;
    private final String message;

    public TcpSampler(String host, int port, String message) {
        this.host = host;
        this.port = port;
        this.message = message;
    }

    @Override
    public SampleResult sample() {

        SampleResult result = new SampleResult();
        result.sampleStart();

        try (Socket socket = new Socket(host, port)) {

            OutputStream os = socket.getOutputStream();
            os.write(message.getBytes());
            os.flush();

            InputStream is = socket.getInputStream();
            byte[] buffer = new byte[1024];
            is.read(buffer);

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
        return List.of(new TcpAssertion());
    }
}