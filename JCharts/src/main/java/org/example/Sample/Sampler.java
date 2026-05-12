package org.example.Sample;

import org.example.Assertion.Assertion;
import org.example.SampleResult.SampleResult;

import java.util.Collections;
import java.util.List;

public interface Sampler {
    SampleResult sample();
    default List<Assertion> defaultAssertions() {
        return Collections.emptyList();
    }
}
