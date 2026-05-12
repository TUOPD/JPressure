package org.example.Assertion;

import org.example.SampleResult.SampleResult;

public class StatusAssertion implements Assertion {

    private final int expected;

    public StatusAssertion(int expected) {
        this.expected = expected;
    }

    @Override
    public void check(SampleResult result) {
        if (result.getStatusCode() != expected) {
            result.setSuccess(false);
            result.setErrorMessage("状态码断言失败");
        }
    }
}