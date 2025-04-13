/*
 * Copyright © 2024 Cask Data, Inc.
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

package io.cdap.wrangler.directives.aggregates;

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;
import io.cdap.wrangler.api.Row;

import java.util.Collections;
import java.util.List;

@Plugin(type = Directive.TYPE)
@Name("aggregate-stats")
@Description("Aggregates byte size and time duration columns and produces totals or averages in specified units.")
public abstract class AggregateStatsDirective implements Directive, AggregateDirective {

    private String sizeColumn;
    private String durationColumn;
    private String targetSizeColumn;
    private String targetTimeColumn;
    private String sizeUnit = "b";
    private String timeUnit = "ms";
    private String aggregationType = "total";

    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        sizeColumn = ((ColumnName) args.value("size_col")).value();
        durationColumn = ((ColumnName) args.value("duration_col")).value();
        targetSizeColumn = ((ColumnName) args.value("target_size_col")).value();
        targetTimeColumn = ((ColumnName) args.value("target_time_col")).value();

        if (args.contains("aggregation_type")) {
            aggregationType = ((Text) args.value("aggregation_type")).value().toLowerCase();
        }
        if (args.contains("size_unit")) {
            sizeUnit = ((Text) args.value("size_unit")).value().toLowerCase();
        }
        if (args.contains("time_unit")) {
            timeUnit = ((Text) args.value("time_unit")).value().toLowerCase();
        }
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        Store store = context.getStore("aggregate-stats");

        double totalSize = store.getOrDefault("total_size", 0.0);
        double totalDuration = store.getOrDefault("total_duration", 0.0);
        int count = store.getOrDefault("count", 0);

        for (Row row : rows) {
            Object sizeObj = row.getValue(sizeColumn);
            Object durationObj = row.getValue(durationColumn);

            if (sizeObj != null) {
                totalSize += parseByteSize(sizeObj.toString());
            }
            if (durationObj != null) {
                totalDuration += parseTimeDuration(durationObj.toString());
            }
            count++;
        }

        store.put("total_size", totalSize);
        store.put("total_duration", totalDuration);
        store.put("count", count);

        return Collections.emptyList(); // Return nothing during execute phase
    }

    @Override
    public List<Row> finalize(ExecutorContext context) throws DirectiveExecutionException {
        Store store = context.getStore("aggregate-stats");

        double totalSize = store.getOrDefault("total_size", 0.0);
        double totalDuration = store.getOrDefault("total_duration", 0.0);
        int count = store.getOrDefault("count", 1); // Prevent divide-by-zero

        if ("average".equals(aggregationType)) {
            totalSize /= count;
            totalDuration /= count;
        }

        double sizeResult = convertSize(totalSize, sizeUnit);
        double durationResult = convertTime (totalDuration, timeUnit);

        Row result = new Row();
        result.add(targetSizeColumn, sizeResult);
        result.add(targetTimeColumn, durationResult);

        return Collections.singletonList(result); // Emit only one aggregated row
    }

    private double parseByteSize(String input) throws DirectiveExecutionException {
        input = input.trim().toLowerCase();
        double multiplier = 1;

        if (input.endsWith("kb")) {
            multiplier = 1024;
            input = input.replace("kb", "");
        } else if (input.endsWith("mb")) {
            multiplier = 1024 * 1024;
            input = input.replace("mb", "");
        } else if (input.endsWith("gb")) {
            multiplier = 1024 * 1024 * 1024;
            input = input.replace("gb", "");
        } else if (input.endsWith("b")) {
            input = input.replace("b", "");
        }

        try {
            return Double.parseDouble(input) * multiplier;
        } catch (NumberFormatException e) {
            throw new DirectiveExecutionException("Invalid byte size format: " + input);
        }
    }

    private double parseTimeDuration(String input) throws DirectiveExecutionException {
        input = input.trim().toLowerCase();
        double multiplier = 1;

        if (input.endsWith("ms")) {
            input = input.replace("ms", "");
        } else if (input.endsWith("s")) {
            multiplier = 1000;
            input = input.replace("s", "");
        } else if (input.endsWith("m")) {
            multiplier = 60 * 1000;
            input = input.replace("m", "");
        } else if (input.endsWith("h")) {
            multiplier = 60 * 60 * 1000;
            input = input.replace("h", "");
        }

        try {
            return Double.parseDouble(input) * multiplier;
        } catch (NumberFormatException e) {
            throw new DirectiveExecutionException("Invalid time duration format: " + input);
        }
    }

    private double convertSize(double bytes, String unit) {
        switch (unit) {
            case "kb": return bytes / 1024;
            case "mb": return bytes / (1024 * 1024);
            case "gb": return bytes / (1024 * 1024 * 1024);
            default: return bytes;
        }
    }

    private double convertTime(double ms, String unit) {
        switch (unit) {
            case "s": return ms / 1000;
            case "m": return ms / (60 * 1000);
            case "h": return ms / (60 * 60 * 1000);
            default: return ms;
        }
    }
}