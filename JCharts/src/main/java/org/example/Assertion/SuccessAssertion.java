package org.example.Assertion;


import org.example.SampleResult.SampleResult;

public class SuccessAssertion implements Assertion {
    public SuccessAssertion(){};
    @Override
    public void check(SampleResult result) {

        if (!result.isSuccess()) {
            result.setErrorMessage("执行失败");
        }
    }
}