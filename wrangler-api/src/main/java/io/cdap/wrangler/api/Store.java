/*
 *  Copyright © 2024 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.cdap.wrangler.api;

/**
 * A simple key-value store used to persist values across directive executions.
 * This is typically used during aggregation operations to store intermediate results.
 */
public interface Store {
    /**
     * Retrieves a value associated with the specified key, or returns the default value if not present.
     *
     * @param key          The key to retrieve the value for.
     * @param defaultValue The default value to return if the key does not exist.
     * @param <T>          The type of value.
     * @return The stored value or the default if the key is not found.
     */
    <T> T getOrDefault(String key, T defaultValue);

    /**
     * Stores a value with the specified key.
     *
     * @param key   The key to associate with the value.
     * @param value The value to store.
     * @param <T>   The type of value.
     */
    <T> void put(String key, T value);
}
