package io.cdap.wrangler.api.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** * Parser for time duration strings (e.g., "500ms", "2.1s", "3min", "1h").
 */
public class TimeDurationTest {
    private static final Pattern TIME_PATTERN = Pattern.compile("(?i)^([0-9]*\\.?[0-9]+)(ms|s|sec|secs|m|min|mins|h|hr|hrs|hour|hours)$");

    private long millis;

    public void TimeDuration(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Time duration string cannot be null");
        }

        Matcher matcher = TIME_PATTERN.matcher(input.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time duration format: " + input);
        }

        double value = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2).toLowerCase();

        switch (unit) {
            case "ms":
                millis = (long) value;
                break;
            case "s":
            case "sec":
            case "secs":
                millis = (long) (value * 1000);
                break;
            case "m":
            case "min":
            case "mins":
                millis = (long) (value * 60 * 1000);
                break;
            case "h":
            case "hr":
            case "hrs":
            case "hour":
            case "hours":
                millis = (long) (value * 60 * 60 * 1000);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
    }

    public TimeDurationTest(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }
}
