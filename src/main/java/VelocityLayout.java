package assign251_2;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.util.Date;

// custom Log4j layouts that use Velocity as the template engine
public class VelocityLayout extends Layout {

    // pattern/template string (e.g., "[$p] $c $d: $m")
    private String pattern;

    // constructor to initialize Velocity engine
    public VelocityLayout() {
        Velocity.init();
    }

    // constructor with pattern parameter
    public VelocityLayout(String pattern) {
        this();
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    // format the LoggingEvent into a string using the Velocity template
    @Override
    public String format(LoggingEvent event) {

        // create velocity context
        VelocityContext context = new VelocityContext();

        // put all variables into said context as per assignment doc
        context.put("c", event.getLoggerName());
        context.put("m", event.getMessage().toString());
        context.put("p", event.getLevel().toString());
        context.put("d", new Date(event.getTimeStamp()).toString());
        context.put("t", event.getThreadName());
        context.put("n", System.lineSeparator());

        // evaluate the pattern with the context
        StringWriter writer = new StringWriter();
        Velocity.evaluate(context, writer, "VelocityLayout", pattern);

        // return the string
        return writer.toString();
    }

    // handle throwable errors if they get thrown so that the program can continue to run
    @Override
    public boolean ignoresThrowable() {
        return true;
    }

    // validate that Velocity pattern was set
    @Override
    public void activateOptions() {
        // No special activation needed
    }
}
