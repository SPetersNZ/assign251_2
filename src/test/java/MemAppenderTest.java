package assign251_2;

import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// tests for MemAppender functions
public class MemAppenderTest {

    private assign251_2.MemAppender appender;
    private Logger logger;

    @BeforeEach
    public void setUp() {
        // reset singleton before each test to flush memory
        assign251_2.MemAppender.resetInstance();

        // Get a fresh instance with ArrayList
        appender = assign251_2.MemAppender.getInstance(new ArrayList<>());

        // configure appender
        appender.setMaxSize(100);
        appender.setLayout(new SimpleLayout()); // use SimpleLayout for ease

        // create logger and attach appender
        logger = Logger.getLogger("TestLogger");
        logger.addAppender(appender);
    }

    // clean up after each test
    @AfterEach
    public void tearDown() {
        if (logger != null) {
            logger.removeAllAppenders();
        }
        assign251_2.MemAppender.resetInstance();
    }

    @Test
    public void testBasicLogging() {
        // Log a single message
        logger.info("Test message");

        // verify that only 1 log was stored
        assertEquals(1, appender.getCurrentLogs().size());

        // verify that no logs were discarded
        assertEquals(0, appender.getDiscardedLogCount());
    }

    @Test
    public void testMultipleLogsStored() {
        // log multiple messages
        logger.info("Message 1");
        logger.info("Message 2");
        logger.info("Message 3");

        // verify that all were stored
        assertEquals(3, appender.getCurrentLogs().size());
    }

    @Test
    public void testMaxSizeEnforcement() {
        // set a smaller maxSize
        appender.setMaxSize(2);

        // log 3 messages
        logger.info("Message 1");
        logger.info("Message 2");
        logger.info("Message 3");

        // should only have two logs stored
        assertEquals(2, appender.getCurrentLogs().size());

        // and one discarded
        assertEquals(1, appender.getDiscardedLogCount());
    }

    @Test
    public void testGetEventStrings() {
        // log some messages
        logger.info("Test message 1");
        logger.info("Test message 2");

        // get strings
        var eventStrings = appender.getEventStrings();

        // each string should contain the respective text
        assertTrue(eventStrings.get(0).contains("Test message 1"));
        assertTrue(eventStrings.get(1).contains("Test message 2"));
    }

    @Test
    public void testPrintLogsClears() {
        // log some messages
        logger.info("Message 1");
        logger.info("Message 2");

        // verify that the logs exist
        assertEquals(2, appender.getCurrentLogs().size());

        // print logs (which should also clear)
        appender.printLogs();

        // verify logs were cleared
        assertEquals(0, appender.getCurrentLogs().size());

        // and that they don't count as discarded
        assertEquals(0, appender.getDiscardedLogCount());
    }

    @Test
    public void testSingletonBehavior() {
        // get another reference to this object
        assign251_2.MemAppender sameInstance = assign251_2.MemAppender.getInstance();

        // confirm object is the same
        assertSame(appender, sameInstance);

        // log a message through original reference
        logger.info("Test");

        // should be able to see the log through the reference
        assertEquals(1, sameInstance.getCurrentLogs().size());
    }
}