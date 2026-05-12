package org.example.Assertion;

import org.example.SampleResult.SampleResult;

public class MysqlAssertion implements Assertion {

    private final boolean requireAffect;

    public MysqlAssertion(boolean requireAffect) {
        this.requireAffect = requireAffect;
    }
    public MysqlAssertion() {
        this.requireAffect = false;
    }
    @Override
    public void check(SampleResult result) {

        if (!result.isSuccess()) {
            result.setErrorMessage("SQL 执行异常");
            return;
        }

        if (requireAffect && result.getAffectedRows() <= 0) {
            result.setSuccess(false);
            result.setErrorMessage("SQL 未影响任何行");
        }
    }
}