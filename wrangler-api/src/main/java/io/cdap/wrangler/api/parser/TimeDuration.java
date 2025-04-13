package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for time duration strings (e.g., "500ms", "2.1s", "3min", "1h").
 */
public class TimeDuration implements Token {
    private static final Pattern TIME_PATTERN =
            Pattern.compile("(?i)^([0-9]*\\.?[0-9]+)(ms|s|sec|secs|m|min|mins|h|hr|hrs|hour|hours)$");

    private final long millis;
    private final String value;

    /**
     * Constructs a TimeDuration from the given string.
     *
     * @param input the time duration string (e.g. "2.5s", "1min")
     * @throws IllegalArgumentException if the input is null or not a valid time duration
     */
    public TimeDuration(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Time duration string cannot be null");
        }

        this.value = input.trim();

        Matcher matcher = TIME_PATTERN.matcher(this.value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time duration format: " + input);
        }

        double quantity = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2).toLowerCase();

        switch (unit) {
            case "ms":
                millis = (long) quantity;
                break;
            case "s":
            case "sec":
            case "secs":
                millis = (long) (quantity * 1000);
                break;
            case "m":
            case "min":
            case "mins":
                millis = (long) (quantity * 60 * 1000);
                break;
            case "h":
            case "hr":
            case "hrs":
            case "hour":
            case "hours":
                millis = (long) (quantity * 60 * 60 * 1000);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
    }

    /**
     * Returns the time duration in milliseconds.
     */
    public long getMillis() {
        return millis;
    }

    /**
     * Returns the original input string.
     */
    @Override
    public String value() {
        return value;
    }

    @Override
    public TokenType type() {
        return null;
    }

    @Override
    public JsonElement toJson() {
        return null;
    }

    @Override
    public String toString() {
        return "TimeDuration{" +
                "millis=" + millis +
                ", value='" + value + '\'' +
                '}';
    }
}
