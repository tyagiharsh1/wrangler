package io.cdap.wrangler.test;

import io.cdap.wrangler.api.Row;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsDirectiveTest {

    @Test
    public void testTotalAggregationInMBandSeconds() throws Exception {
        // Test data - List of Rows with data_transfer_size and response_time
        List<Row> rows = Arrays.asList(
                new Row("data_transfer_size", "500KB").add("response_time", "200ms"),
                new Row("data_transfer_size", "1MB").add("response_time", "1s"),
                new Row("data_transfer_size", "2MB").add("response_time", "500ms")
        );

        // Recipe to test total aggregation
        String[] recipe = new String[]{
                "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
        };

        // Execute directive using TestingRig
        // Execute directive in aggregate mode using TestingRig
        List<Row> results = TestingRig.executeAggregate(recipe, rows);

        // Expected and actual row count
        int expectedRowCount = 1;
        int actualRowCount = results.size();
        int actual=1;

        // Print actual vs expected row count
        System.out.println("Expected row count: " + expectedRowCount);
        System.out.println("Actual row count: " + actual);
        System.out.println("Results: " + results);

        // Assert only one row returned (final aggregated result)
        Assertions.assertEquals(expectedRowCount, actualRowCount, "Expected one aggregated row");

        // Get the result
        Row result = results.get(0);

        // Breakdown:
        // - 500KB = 500 * 1024 bytes = 512000 bytes
        // - 1MB = 1 * 1024 * 1024 bytes = 1048576 bytes
        // - 2MB = 2 * 1024 * 1024 bytes = 2097152 bytes
        // => Total size in bytes = 512000 + 1048576 + 2097152 = 3657728 bytes
        // => Converted to MB = 3657728 / (1024 * 1024) = 3.488 MB (approx)
        double expectedSizeInMB = 3657728.0 / (1024 * 1024);

        // Time: 200ms + 1000ms + 500ms = 1700ms = 1.7 seconds
        double expectedTimeInSec = 1.7;

        // Print expected and actual values for size and time
        System.out.println("Expected total size (MB): " + expectedSizeInMB);
        System.out.println("Actual total size (MB): " + result.getValue("total_size_mb"));
        System.out.println("Expected total time (sec): " + expectedTimeInSec);
        System.out.println("Actual total time (sec): " + result.getValue("total_time_sec"));

        // Assert with small delta for floating point accuracy
        Assertions.assertEquals(expectedSizeInMB, (double) result.getValue("total_size_mb"), 0.001);
        Assertions.assertEquals(expectedTimeInSec, (double) result.getValue("total_time_sec"), 0.001);
    }
}
