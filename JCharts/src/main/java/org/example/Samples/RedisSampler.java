package org.example.Samples;

import org.example.Assertion.Assertion;
import org.example.Assertion.RedisAssertion;
import org.example.Sample.Sampler;
import org.example.SampleResult.SampleResult;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RedisSampler implements Sampler {

    private final JedisPool pool;
    private static final AtomicLong counter = new AtomicLong();
    public RedisSampler(String host, int port) {
        this.pool = new JedisPool(host, port);
    }

    @Override
    public SampleResult sample() {

        SampleResult result = new SampleResult();
        result.sampleStart();

        try (Jedis jedis = pool.getResource()) {

            String key = "key:" + nextId().toString();
            jedis.set(key, "value");
            result.setSuccess(true);

        } catch (Exception e) {
            result.setSuccess(false);
            System.out.println(e);

        }

        result.sampleEnd();
        return result;
    }
    @Override
    public List<Assertion> defaultAssertions() {
        return List.of(new RedisAssertion());
    }
    public static String nextId() {
        return String.valueOf(counter.incrementAndGet());
    }
}
