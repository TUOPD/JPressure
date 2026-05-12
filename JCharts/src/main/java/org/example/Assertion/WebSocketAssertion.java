package org.example.Assertion;

import org.example.SampleResult.SampleResult;

public class WebSocketAssertion implements Assertion {
    public WebSocketAssertion() {

    }
    @Override
    public void check(SampleResult result) {

        if (!result.isSuccess()) {
            result.setErrorMessage("WebSocket 连接或发送失败");
        }
    }
}