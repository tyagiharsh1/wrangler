/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Token implementation for representing time durations like "500ms", "2s", "3min", etc.
 */
public class TimeDuration implements Token {
    private final long millis;
    private final String original;

    public TimeDuration(String value) {
        this.original = value;
        this.millis = parseMillis(value);
    }

    private long parseMillis(String input) {
        String lower = input.trim().toLowerCase();

        double number = Double.parseDouble(lower.replaceAll("[a-z]+", ""));

        if (lower.endsWith("ms")) return (long) number;
        if (lower.endsWith("s") || lower.endsWith("sec") || lower.endsWith("seconds")) return (long) (number * 1000);
        if (lower.endsWith("m") || lower.endsWith("min") || lower.endsWith("minutes")) return (long) (number * 60 * 1000);
        if (lower.endsWith("h")) return (long) (number * 60 * 60 * 1000);

        throw new IllegalArgumentException("Unsupported time unit: " + input);
    }

    public long getMillis() {
        return millis;
    }

    @Override
    public Object value() {
        return millis;
    }

    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("original", original);
        json.addProperty("milliseconds", millis);
        return json;
    }
}
