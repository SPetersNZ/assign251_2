package assign251_2;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// custom Log4j appender to store logs in memory
public class MemAppender extends AppenderSkeleton {

    // set up Singleton instance for dependency injection
    private static MemAppender instance;

    // storage for logging events
    private List<LoggingEvent> logs;

    // default value for num of logs
    private int maxSize = 1000;

    private long discardedLogCount = 0;

    // private constructor to enforce singleton pattern
    private MemAppender() {
        this.logs = new ArrayList<>();
    }

    // private constructor for dependency injection to insert logs
    private MemAppender(List<LoggingEvent> logList) {
        this.logs = logList;
    }

    // return singleton instance
    public static synchronized MemAppender getInstance() {
        if (instance == null) {
            instance = new MemAppender();
        }
        return instance;
    }

    // return singleton instance with dependency injection to insert logs against MemAppender instance
    public static synchronized MemAppender getInstance(List<LoggingEvent> logList) {
        if (instance == null) {
            instance = new MemAppender(logList);
        }
        return instance;
    }

    // reset singleton instance
    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    // send logging events here to append them - subclass of AppenderSkeleton
    @Override
    protected void append(LoggingEvent event) {
        logs.add(event);
        int listSize = logs.size();
        if (listSize > maxSize) {
            logs.remove(0);
            discardedLogCount++;
        }
    }

    // clear logs when closing - inherited from org.apache.log4j.Appender
    @Override
    public void close() {
        if (logs != null) {
            logs.clear();
        }
    }

    // confirms if appender requires a layout - org.apache.log4j.Appender
    @Override
    public boolean requiresLayout() {
        return false;
    }

    // return unmodifiable list of logs as per assignment doc
    public List<LoggingEvent> getCurrentLogs() {
        return Collections.unmodifiableList(logs);
    }

    // return unmodifiable list of formatted strings as per assignment doc
    public List<String> getEventStrings() {

        // confirm layout has been set
        if (layout == null) {
            throw new IllegalStateException("Layout is not set!");
        }

        List<String> formattedLogs = new ArrayList<>();

        // format each log
        for (LoggingEvent event : logs) {
            String formatted = layout.format(event);
            formattedLogs.add(formatted);
        }

        // return unmodifiable list
        return Collections.unmodifiableList(formattedLogs);
    }


    // print all logs to console and then clear the logs as per assignment doc
    public void printLogs() {

        // confirm layout has been set
        if (layout == null) {
            throw new IllegalStateException("Layout is not set!");
        }

        // display each log
        for (LoggingEvent event : logs) {
            String formatted = layout.format(event);
            System.out.println(formatted);
        }

        // clear logs
        logs.clear();
    }

    // getters and setters
    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public long getDiscardedLogCount() {
        return discardedLogCount; // as per assignment doc
    }

}
