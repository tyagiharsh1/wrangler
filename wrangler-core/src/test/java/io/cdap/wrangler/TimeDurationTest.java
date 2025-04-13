package io.cdap.wrangler;

import io.cdap.wrangler.api.parser.TimeDuration;
import org.junit.Assert;
import org.junit.Test;

public class TimeDurationTest {

    @Test
    public void testValidDurations() {
        Assert.assertEquals(500, new TimeDuration("500ms").getMillis());
        Assert.assertEquals(2100, new TimeDuration("2.1s").getMillis());
        Assert.assertEquals(180000, new TimeDuration("3min").getMillis());
        Assert.assertEquals(3600000, new TimeDuration("1h").getMillis());
        Assert.assertEquals(5400000, new TimeDuration("1.5hr").getMillis());
        Assert.assertEquals(60000, new TimeDuration("1m").getMillis());
        Assert.assertEquals(60000, new TimeDuration("1min").getMillis());
        Assert.assertEquals(60000, new TimeDuration("1mins").getMillis());
        Assert.assertEquals(7200000, new TimeDuration("2hours").getMillis());
        Assert.assertEquals(60000, new TimeDuration("60s").getMillis());
    }

    @Test
    public void testCaseInsensitivity() {
        Assert.assertEquals(1000, new TimeDuration("1S").getMillis());
        Assert.assertEquals(1000, new TimeDuration("1Sec").getMillis());
        Assert.assertEquals(1000, new TimeDuration("1SECS").getMillis());
        Assert.assertEquals(3600000, new TimeDuration("1HOUR").getMillis());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullInput() {
        new TimeDuration(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyInput() {
        new TimeDuration("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFormat() {
        new TimeDuration("abc123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUnit() {
        new TimeDuration("10lightyears");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoUnit() {
        new TimeDuration("123");
    }
}
