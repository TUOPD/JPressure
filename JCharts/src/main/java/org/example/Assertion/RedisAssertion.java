package org.example.Assertion;


import org.example.SampleResult.SampleResult;

public class RedisAssertion implements Assertion {

    private final boolean requireNotNull;

    public RedisAssertion(boolean requireNotNull) {
        this.requireNotNull = requireNotNull;
    }
    public RedisAssertion() {
        this.requireNotNull = false;
    }
    @Override
    public void check(SampleResult result) {

        if (!result.isSuccess()) {
            result.setErrorMessage("Redis 执行异常");
            return;
        }

        if (requireNotNull && result.getResponseData() == null) {
            result.setSuccess(false);
            result.setErrorMessage("Redis 返回为空");
        }
    }
}