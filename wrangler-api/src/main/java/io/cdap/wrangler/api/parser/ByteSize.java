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
 * Token implementation for representing byte sizes like "10KB", "1.5MB", etc.
 */
public class ByteSize implements Token {
    private final long bytes;
    private final String original;

    public ByteSize(String value) {
        this.original = value;
        this.bytes = parseBytes(value);
    }

    private long parseBytes(String input) {
        String lower = input.trim().toLowerCase();
        double number = Double.parseDouble(lower.replaceAll("[a-zA-Z]+", ""));
        if (lower.endsWith("kb")) return (long) (number * 1024);
        if (lower.endsWith("mb")) return (long) (number * 1024 * 1024);
        if (lower.endsWith("gb")) return (long) (number * 1024 * 1024 * 1024);
        if (lower.endsWith("b")) return (long) number;
        throw new IllegalArgumentException("Unsupported byte unit: " + input);
    }

    public long getBytes() {
        return bytes;
    }

    @Override
    public Object value() {
        return bytes;
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("original", original);
        json.addProperty("bytes", bytes);
        return json;
    }
}
