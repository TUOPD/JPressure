package org.example.Assertion;

import org.example.SampleResult.SampleResult;

public class TcpAssertion implements Assertion {

    private final String expected;

    public TcpAssertion(String expected) {
        this.expected = expected;
    }
    public TcpAssertion() {
        this.expected = null;
    }
    @Override
    public void check(SampleResult result) {

        if (!result.isSuccess()) {
            result.setErrorMessage("TCP 执行异常");
            return;
        }

        if (result.getResponseData() == null ||
                !result.getResponseData().toString().contains(expected)) {

            result.setSuccess(false);
            result.setErrorMessage("TCP 返回内容不匹配");
        }
    }
}