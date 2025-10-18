package assign251_2;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// test VelocityLayout with different appenders and layouts
public class VelocityLayoutTest {

    private Logger logger;

    @BeforeEach
    public void setUp() {
        assign251_2.MemAppender.resetInstance();
        logger = Logger.getLogger("VelocityLayoutTest");
        logger.removeAllAppenders();
    }

    @AfterEach
    public void tearDown() {
        if (logger != null) {
            logger.removeAllAppenders();
        }
        assign251_2.MemAppender.resetInstance();
    }

    @Test
    public void testVelocityLayoutWithMemAppender() {
        // create VelocityLayout with pattern
        assign251_2.VelocityLayout velocityLayout = new assign251_2.VelocityLayout("[$p] $m");

        // create and configure MemAppender
        assign251_2.MemAppender appender = assign251_2.MemAppender.getInstance(new ArrayList<>());
        appender.setLayout(velocityLayout);
        appender.setMaxSize(100);

        // attach to logger
        logger.addAppender(appender);

        // log a message
        logger.info("Layout Test");

        // get formatted strings
        List<String> eventStrings = appender.getEventStrings();

        // test
        assertEquals(1, eventStrings.size());
        assertTrue(eventStrings.get(0).contains("INFO"));
        assertTrue(eventStrings.get(0).contains("Layout Test"));
    }

    @Test
    public void testVelocityLayoutWithConsoleAppender() {
        // create VelocityLayout with pattern
        assign251_2.VelocityLayout velocityLayout = new assign251_2.VelocityLayout("[$p] $c: $m");

        // create ConsoleAppender with VelocityLayout
        ConsoleAppender consoleAppender = new ConsoleAppender(velocityLayout);

        // attach to logger
        logger.addAppender(consoleAppender);

        // log messages for the console
        logger.info("Console test message 1");
        logger.warn("Console test message 2");

        // test passes if no exceptions thrown as there are no logs from console
        assertTrue(true);
    }

    @Test
    public void testVelocityLayoutWithFileAppender() throws IOException {
        // create temp file
        File tempFile = File.createTempFile("velocity-test-", ".log");
        tempFile.deleteOnExit();

        // create VelocityLayout
        assign251_2.VelocityLayout velocityLayout = new assign251_2.VelocityLayout("$p - $m$n");

        // create FileAppender with VelocityLayout
        FileAppender fileAppender = new FileAppender(velocityLayout, tempFile.getAbsolutePath(), false);

        // attach to logger
        logger.addAppender(fileAppender);

        // log messages
        logger.info("File test message 1");
        logger.error("File test message 2");

        // close appender to flush
        fileAppender.close();

        // read file and verify
        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains("INFO - File test message 1"));
        assertTrue(content.contains("ERROR - File test message 2"));
    }

    @Test
    public void testAllVelocityVariables() {
        // set up pattern given in assignment doc
        assign251_2.VelocityLayout velocityLayout = new assign251_2.VelocityLayout("[$p] $c $d: $m");

        // use MemAppender to capture output
        assign251_2.MemAppender appender = assign251_2.MemAppender.getInstance(new ArrayList<>());
        appender.setLayout(velocityLayout);
        appender.setMaxSize(100);

        logger.addAppender(appender);

        // log a message
        logger.info("testing VelocityLayout variables from VelocityLayout.java");

        // get formatted output
        List<String> eventStrings = appender.getEventStrings();

        assertEquals(1, eventStrings.size());
        String output = eventStrings.get(0);

        // verify
        assertTrue(output.contains("INFO"), "contains priority level");
        assertTrue(output.contains("VelocityLayoutTest"), "contains logger name set up in @beforeEach");
        assertTrue(output.contains("testing VelocityLayout variables from VelocityLayout.java"), "contains test message");
    }
}