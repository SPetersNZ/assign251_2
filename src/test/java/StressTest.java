package assign251_2;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

// stress tests to measure performance of different appender/layout combinations
// comparing time and memory consumption
public class StressTest {

    private static final int NUM_LOGS = 10000;
    private Logger logger;

    // reset appender before each test
    @BeforeEach
    public void setUp() {
        assign251_2.MemAppender.resetInstance();
        logger = Logger.getLogger("StressTest");
        logger.removeAllAppenders();
    }

    // garbage collection when done
    @AfterEach
    public void tearDown() {
        if (logger != null) {
            logger.removeAllAppenders();
        }
        assign251_2.MemAppender.resetInstance();

    }

    // method to add delay so that profiler can be attached
    static {
        System.out.println("process ID: " + ProcessHandle.current().pid());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 1000000})
    public void testMemAppenderArrayListPerformance(int maxSize) {
        System.out.println("\n=== testing MemAppender with ArrayList, maxSize=" + maxSize + " ===");

        // create MemAppender with ArrayList
        assign251_2.MemAppender appender = assign251_2.MemAppender.getInstance(new ArrayList<>());
        appender.setMaxSize(maxSize);
        appender.setLayout(new PatternLayout("[%p] %c: %m%n"));

        logger.addAppender(appender);

        // measure memory before
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // measure time
        long startTime = System.currentTimeMillis();

        // generate logs
        for (int i = 0; i < NUM_LOGS; i++) {
            logger.info("test message number " + i);
        }

        // measure memory/time after
        long endTime = System.currentTimeMillis();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        // calculate results
        long timeTaken = endTime - startTime;
        long memoryUsed = memoryAfter - memoryBefore;

        // Print results
        System.out.println("time taken: " + timeTaken + " ms");
        System.out.println("memory used: " + (memoryUsed / 1024) + " KB");
        System.out.println("logs stored: " + appender.getCurrentLogs().size());
        System.out.println("logs discarded: " + appender.getDiscardedLogCount());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 1000000})
    public void testMemAppenderLinkedListPerformance(int maxSize) {
        System.out.println("\n=== testing MemAppender with LinkedList, maxSize=" + maxSize + " ===");

        // Create MemAppender with LinkedList
        assign251_2.MemAppender appender = assign251_2.MemAppender.getInstance(new LinkedList<>());
        appender.setMaxSize(maxSize);
        appender.setLayout(new PatternLayout("[%p] %c: %m%n"));

        logger.addAppender(appender);

        // measure memory before
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // measure time
        long startTime = System.currentTimeMillis();

        // generate logs
        for (int i = 0; i < NUM_LOGS; i++) {
            logger.info("test message number " + i);
        }

        // measure memory/time after
        long endTime = System.currentTimeMillis();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        // calculate results
        long timeTaken = endTime - startTime;
        long memoryUsed = memoryAfter - memoryBefore;

        // Print results
        System.out.println("time taken: " + timeTaken + " ms");
        System.out.println("memory used: " + (memoryUsed / 1024) + " KB");
        System.out.println("logs stored: " + appender.getCurrentLogs().size());
        System.out.println("logs discarded: " + appender.getDiscardedLogCount());

        // allow time for profiler observation
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 1000000})
    public void testConsoleAppenderPerformance(int maxSize) {
        System.out.println("\n=== testing ConsoleAppender ===");

        // ConsoleAppender doesn't have maxSize, but we test with same log count
        ConsoleAppender appender = new ConsoleAppender(new PatternLayout("[%p] %c: %m%n"));
        logger.addAppender(appender);

        // measure memory before
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // measure time
        long startTime = System.currentTimeMillis();

        // generate logs
        for (int i = 0; i < NUM_LOGS; i++) {
            logger.info("test message number " + i);
        }

        // measure memory/time after
        long endTime = System.currentTimeMillis();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        // calculate results
        long timeTaken = endTime - startTime;
        long memoryUsed = memoryAfter - memoryBefore;

        // print results
        System.out.println("time taken: " + timeTaken + " ms");
        System.out.println("memory used: " + (memoryUsed / 1024) + " KB");

        // allow time for profiler observation
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 1000000})
    public void testFileAppenderPerformance(int maxSize) throws IOException {
        System.out.println("\n=== testing FileAppender (maxSize parameter ignored) ===");

        // create temp file
        File tempFile = File.createTempFile("stress-test-", ".log");
        tempFile.deleteOnExit();

        // FileAppender doesn't have maxSize, but we test with same log count
        FileAppender appender = new FileAppender(new PatternLayout("[%p] %c: %m%n"),
                tempFile.getAbsolutePath(), false);
        logger.addAppender(appender);

        // measure memory before
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // measure time
        long startTime = System.currentTimeMillis();

        // generate logs
        for (int i = 0; i < NUM_LOGS; i++) {
            logger.info("test message number " + i);
        }

        long endTime = System.currentTimeMillis();

        // measure memory after
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        // calculate results
        long timeTaken = endTime - startTime;
        long memoryUsed = memoryAfter - memoryBefore;

        // print results
        System.out.println("time taken: " + timeTaken + " ms");
        System.out.println("memory used: " + (memoryUsed / 1024) + " KB");
        System.out.println("file size: " + (tempFile.length() / 1024) + " KB");

        appender.close();

        // allow time for profiler observation
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 1000000})
    public void testVelocityLayoutPerformance(int maxSize) {
        System.out.println("\n=== testing VelocityLayout with MemAppender, maxSize=" + maxSize + " ===");

        // create MemAppender with ArrayList
        assign251_2.MemAppender appender = assign251_2.MemAppender.getInstance(new ArrayList<>());
        appender.setMaxSize(maxSize);
        appender.setLayout(new assign251_2.VelocityLayout("[$p] $c $d: $m"));

        logger.addAppender(appender);

        // measure memory before
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // measure time
        long startTime = System.currentTimeMillis();

        // generate logs
        for (int i = 0; i < NUM_LOGS; i++) {
            logger.info("test message number " + i);
        }

        // force formatting to measure layout performance
        appender.getEventStrings();

        // measure memory/time after
        long endTime = System.currentTimeMillis();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        // calculate results
        long timeTaken = endTime - startTime;
        long memoryUsed = memoryAfter - memoryBefore;

        // print results
        System.out.println("time taken: " + timeTaken + " ms");
        System.out.println("memory used: " + (memoryUsed / 1024) + " KB");
        System.out.println("logs stored: " + appender.getCurrentLogs().size());

        // allow time for profiler observation
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 1000000})
    public void testPatternLayoutPerformance(int maxSize) {
        System.out.println("\n=== testing PatternLayout with MemAppender, maxSize=" + maxSize + " ===");

        // create MemAppender with ArrayList
        assign251_2.MemAppender appender = assign251_2.MemAppender.getInstance(new ArrayList<>());
        appender.setMaxSize(maxSize);
        appender.setLayout(new PatternLayout("[%p] %c %d: %m"));

        logger.addAppender(appender);

        // measure memory before
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // measure time
        long startTime = System.currentTimeMillis();

        // generate logs
        for (int i = 0; i < NUM_LOGS; i++) {
            logger.info("test message number " + i);
        }

        // force formatting to measure layout performance
        appender.getEventStrings();

        // measure memory/time after
        long endTime = System.currentTimeMillis();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        // calculate results
        long timeTaken = endTime - startTime;
        long memoryUsed = memoryAfter - memoryBefore;

        // print results
        System.out.println("time taken: " + timeTaken + " ms");
        System.out.println("memory used: " + (memoryUsed / 1024) + " KB");
        System.out.println("logs stored: " + appender.getCurrentLogs().size());

        // allow time for profiler observation
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
