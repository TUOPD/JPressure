package org.example.Samples;

import org.example.Assertion.Assertion;
import org.example.Assertion.MysqlAssertion;
import org.example.Sample.Sampler;
import org.example.SampleResult.SampleResult;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class MysqlSampler implements Sampler {

    private final DataSource dataSource;
    private final String sql;

    public MysqlSampler(DataSource dataSource, String sql) {
        this.dataSource = dataSource;
        this.sql = sql;
    }

    @Override
    public SampleResult sample() {

        SampleResult result = new SampleResult();
        result.sampleStart();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();

            result.setSuccess(true);

        } catch (Exception e) {
            result.setSuccess(false);
        }

        result.sampleEnd();
        return result;
    }
    @Override
    public List<Assertion> defaultAssertions() {
        return List.of(new MysqlAssertion());
    }
}